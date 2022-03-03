package com.zyf.rpc.transport.netty.client;

import com.zyf.rpc.entity.RpcRequest;
import com.zyf.rpc.entity.RpcResponse;
import com.zyf.rpc.enumeration.RpcError;
import com.zyf.rpc.exception.RpcException;
import com.zyf.rpc.factory.SingletonFactory;
import com.zyf.rpc.loadbalancer.LoadBalancer;
import com.zyf.rpc.loadbalancer.RandomLoadBalancer;
import com.zyf.rpc.register.NacosServiceDiscovery;
import com.zyf.rpc.register.ServiceDiscovery;
import com.zyf.rpc.serializer.CommonSerializer;
import com.zyf.rpc.transport.RpcClient;
import com.zyf.rpc.util.RpcMessageChecker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zyf
 * @date 2022/2/28 18:56
 * @description Netty方式客户端
 */
@Slf4j
public class NettyClient implements RpcClient {

    private final ServiceDiscovery serviceDiscovery;
    private final CommonSerializer serializer;
    private static final EventLoopGroup group;
    private static final Bootstrap bootstrap;
    private final UnprocessedRequests unprocessedRequests;

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
    }

    public NettyClient() {
        //以默认序列化器调用构造函数
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }

    public NettyClient(LoadBalancer loadBalancer){
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public NettyClient(Integer serializerCode){
        this(serializerCode, new RandomLoadBalancer());
    }

    public NettyClient(Integer serializerCode, LoadBalancer loadBalancer){
        serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        serializer = CommonSerializer.getByCode(serializerCode);
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        //保证自定义实体类变量的原子性和共享性的线程安全，此处应用于rpcResponse
        AtomicReference<Object> result = new AtomicReference<>(null);
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            //从Nacos获取提供对应服务的服务端地址
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            //创建Netty通道连接
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if(!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            //将新请求放入未处理完的请求中
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            //向服务端发请求，并设置监听，关于writeAndFlush()的具体实现可以参考：https://blog.csdn.net/qq_34436819/article/details/103937188
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                if(future1.isSuccess()){
                    log.info(String.format("客户端发送消息：%s", rpcRequest.toString()));
                }else {
                    log.error("发送消息时有错误发生:", future1.cause());
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    log.error("发送消息时有错误发生: ", future1.cause());
                }
            });
            channel.closeFuture().sync();
            //AttributeMap<AttributeKey, AttributeValue>是绑定在Channel上的，可以设置用来获取通道对象
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
            //get()阻塞获取value
            RpcResponse rpcResponse = channel.attr(key).get();
            RpcMessageChecker.check(rpcRequest, rpcResponse);
            result.set(rpcResponse.getData());
        }catch (Exception e){
            //将请求从请求集合中移除
            unprocessedRequests.remove(rpcRequest.getRequestId());
            log.error(e.getMessage(), e);
            // interrupt()这里作用是给受阻塞的当前线程发出一个中断信号，让当前线程退出阻塞状态，好继续执行然后结束
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }

}

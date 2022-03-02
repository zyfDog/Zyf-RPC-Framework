package com.zyf.rpc.netty.client;

import com.zyf.rpc.RpcClient;
import com.zyf.rpc.codec.CommonDecoder;
import com.zyf.rpc.codec.CommonEncoder;
import com.zyf.rpc.entity.RpcRequest;
import com.zyf.rpc.entity.RpcResponse;
import com.zyf.rpc.enumeration.RpcError;
import com.zyf.rpc.exception.RpcException;
import com.zyf.rpc.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zyf
 * @date 2022/2/28 18:56
 * @description Netty方式客户端
 */
@Slf4j
public class NettyClient implements RpcClient {

    // private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private String host;
    private int port;
    private static final Bootstrap bootstrap;
    private CommonSerializer serializer;

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public NettyClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new CommonDecoder())
                        .addLast(new CommonEncoder(serializer))
                        .addLast(new NettyClientHandler());
            }
        });
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            log.info("客户端连接到服务端{}：{}", host, port);
            Channel channel = future.channel();
            if(channel != null){
                //向服务端发请求，并设置监听，关于writeAndFlush()的具体实现可以参考：https://blog.csdn.net/qq_34436819/article/details/103937188
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if(future1.isSuccess()){
                        log.info(String.format("客户端发送消息：%s", rpcRequest.toString()));
                    }else {
                        log.error("发送消息时有错误发生:", future1.cause());
                    }
                });
                // 阻塞等待，直到Channel关闭
                channel.closeFuture().sync();
                //AttributeMap<AttributeKey, AttributeValue>是绑定在Channel上的，可以设置用来获取通道对象
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                //get()阻塞获取value
                RpcResponse rpcResponse = channel.attr(key).get();
                return rpcResponse.getData();
            }
        }catch (InterruptedException e){
            log.error("发送消息时有错误发生:", e);
        }
        return null;
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}

package com.zyf.rpc.transport.netty.client;

import com.zyf.rpc.entity.RpcRequest;
import com.zyf.rpc.entity.RpcResponse;
import com.zyf.rpc.factory.SingletonFactory;
import com.zyf.rpc.serializer.CommonSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author zyf
 * @date 2022/2/28 19:15
 * @description 客户端Netty处理器
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private final UnprocessedRequests unprocessedRequests;

    public NettyClientHandler(){
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if(state == IdleState.WRITER_IDLE){
                log.info("发送心跳包[{}]", ctx.channel().remoteAddress());
                Channel channel = ChannelProvider.get((InetSocketAddress) ctx.channel().remoteAddress(),
                        CommonSerializer.getByCode(CommonSerializer.DEFAULT_SERIALIZER));
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setHeartBeat(true);
                //设置一个Listener监测服务端是否接收到心跳包，如果接收到就代表对方在线，不用关闭Channel
                channel.writeAndFlush(rpcRequest).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        try {
            log.info(String.format("客户端接收到消息：%s", msg));
            //将响应数据取出
            unprocessedRequests.complete(msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("过程调用中有错误发生：");
        cause.printStackTrace();
        ctx.close();
    }
}

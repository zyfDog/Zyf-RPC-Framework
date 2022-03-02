package com.zyf.rpc.transport.netty.server;

import com.zyf.rpc.entity.RpcRequest;
import com.zyf.rpc.handler.RequestHandler;
import com.zyf.rpc.util.ThreadPoolFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

/**
 * @author zyf
 * @date 2022/2/28 19:16
 * @description Netty中处理从客户端传来的RpcRequest
 * 用于接收 RpcRequest，并且执行调用，将调用结果返回封装成 RpcResponse 发送出去
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    // private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static final String THREAD_NAME_PREFIX = "netty-server-handler";
    private static final ExecutorService threadPool;

    static{
        requestHandler = new RequestHandler();
        //引入异步业务线程池，避免长时间的耗时业务阻塞netty本身的worker工作线程，耽误了同一个Selector中其他任务的执行
        threadPool = ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        threadPool.execute(() -> {
            try{
                log.info("服务端接收到请求：{}", msg);
                Object response = requestHandler.handle(msg);
                //注意这里的通道是workGroup中的，而NettyServer中创建的是bossGroup的，不要混淆
                ChannelFuture future = ctx.writeAndFlush(response);
                //添加一个监听器到channelfuture来检测是否所有的数据包都发出，然后关闭通道
                future.addListener(ChannelFutureListener.CLOSE);
            }finally {
                ReferenceCountUtil.release(msg);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("处理过程调用时有错误发生：");
        cause.printStackTrace();
        ctx.close();
    }
}

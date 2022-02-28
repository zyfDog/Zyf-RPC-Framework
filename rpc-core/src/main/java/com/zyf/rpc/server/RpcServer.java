package com.zyf.rpc.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author zyf
 * @date 2022/2/28 14:06
 * @description 进行远程调用连接的服务端
 */
@Slf4j
public class RpcServer {

    private final ExecutorService threadPool;
    // private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    public RpcServer(){
        int corePoolSize = 5;
        int maximumPoolSize = 50;
        long keepAliveTime = 60;
        /**
         * 设置上限为100个线程的阻塞队列
         */
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        /**
         * 创建线程池实例
         */
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    public void register(Object service, int port){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            log.info("服务器正在启动……");
            Socket socket;
            //当未接收到连接请求时，accept()会一直阻塞
            while ((socket = serverSocket.accept()) != null){
                log.info("客户端连接！IP：" + socket.getInetAddress());
                threadPool.execute(new RequestHandler(socket, service));
            }
        }catch (IOException e){
            log.info("连接时有错误发生：" + e);
        }
    }

}

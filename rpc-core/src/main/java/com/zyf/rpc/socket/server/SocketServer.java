package com.zyf.rpc.socket.server;

import com.zyf.rpc.RequestHandler;
import com.zyf.rpc.RpcServer;
import com.zyf.rpc.enumeration.RpcError;
import com.zyf.rpc.exception.RpcException;
import com.zyf.rpc.registry.ServiceRegistry;
import com.zyf.rpc.serializer.CommonSerializer;
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
public class SocketServer implements RpcServer {

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private final ExecutorService threadPool;
    private RequestHandler requestHandler = new RequestHandler();
    private final ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;

    public SocketServer(ServiceRegistry serviceRegistry){
        this.serviceRegistry = serviceRegistry;
        /**
         * 设置上限为100个线程的阻塞队列
         */
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        /**
         * 创建线程池实例
         */
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    /**
     * @description 服务端启动
     */
    @Override
    public void start(int port){
        if (serializer == null){
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        try(ServerSocket serverSocket = new ServerSocket(port)){
            log.info("服务器启动……");
            Socket socket;
            //当未接收到连接请求时，accept()会一直阻塞
            while ((socket = serverSocket.accept()) != null){
                log.info("客户端连接！{}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry, serializer));
            }
            threadPool.shutdown();
        }catch (IOException e){
            log.info("服务器启动时有错误发生：" + e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}

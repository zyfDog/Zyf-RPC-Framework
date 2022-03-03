package com.zyf.rpc.transport.socket.server;

import com.zyf.rpc.enumeration.RpcError;
import com.zyf.rpc.exception.RpcException;
import com.zyf.rpc.factory.ThreadPoolFactory;
import com.zyf.rpc.handler.RequestHandler;
import com.zyf.rpc.hook.ShutdownHook;
import com.zyf.rpc.provider.ServiceProvider;
import com.zyf.rpc.provider.ServiceProviderImpl;
import com.zyf.rpc.register.NacosServiceRegistry;
import com.zyf.rpc.register.ServiceRegistry;
import com.zyf.rpc.serializer.CommonSerializer;
import com.zyf.rpc.transport.RpcServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @author zyf
 * @date 2022/2/28 14:06
 * @description 进行远程调用连接的服务端
 */
@Slf4j
public class SocketServer implements RpcServer {

    /*private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;*/
    private final ExecutorService threadPool;
    private final String host;
    private final int port;
    private CommonSerializer serializer;
    private RequestHandler requestHandler = new RequestHandler();

    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    public SocketServer(String host, int port){
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        //创建线程池
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
    }

    /**
     * @description 将服务保存在本地的注册表，同时注册到Nacos注册中心
     */
    @Override
    public <T> void publishService(T service, Class<T> serviceClass) {
        if (serializer == null){
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service, serviceClass);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }

    /**
     * @description 服务端启动
     */
    @Override
    public void start(){
        try(ServerSocket serverSocket = new ServerSocket()){
            serverSocket.bind(new InetSocketAddress(host, port));
            log.info("服务器启动……");
            //添加钩子，服务端关闭时会注销服务
            ShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket;
            //当未接收到连接请求时，accept()会一直阻塞
            while ((socket = serverSocket.accept()) != null){
                log.info("客户端连接！{}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new SocketRequestHandlerThread(socket, requestHandler, serializer));
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

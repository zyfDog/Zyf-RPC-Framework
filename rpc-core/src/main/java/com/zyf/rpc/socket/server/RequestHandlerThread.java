package com.zyf.rpc.socket.server;

import com.zyf.rpc.RequestHandler;
import com.zyf.rpc.entity.RpcRequest;
import com.zyf.rpc.entity.RpcResponse;
import com.zyf.rpc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author zyf
 * @date 2022/2/28 15:38
 * @description 处理客户端RpcRequest的工作线程
 * 利用客户端传来的RpcRequest对象，从ServiceRegistry 中获取提供服务的对象。
 * RequestHandlerThread 只是一个线程，从ServiceRegistry 获取到提供服务的对象后，
 * 就会把 RpcRequest 和服务对象直接交给 RequestHandler 去处理，反射等过程被放到了 RequestHandler 里。
 */
@Slf4j
public class RequestHandlerThread implements Runnable {

    // private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;
    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run() {
        try(ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest)objectInputStream.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            // 通过接口名获取实现类
            Object service = serviceRegistry.getService(interfaceName);
            // 通过处理器执行方法，得到返回结果
            Object result = requestHandler.handle(rpcRequest, service);
            objectOutputStream.writeObject(RpcResponse.success(result));
            objectOutputStream.flush();
        }catch (IOException | ClassNotFoundException e){
            log.info("调用或发送时发生错误：" + e);
        }
    }
}
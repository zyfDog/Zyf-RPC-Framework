package com.zyf.rpc.server;

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
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handle(rpcRequest, service);
            objectOutputStream.writeObject(RpcResponse.success(result));
            objectOutputStream.flush();
        }catch (IOException | ClassNotFoundException e){
            log.info("调用或发送时发生错误：" + e);
        }
    }
}
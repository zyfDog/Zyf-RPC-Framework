package com.zyf.rpc.server;

import com.zyf.rpc.entity.RpcRequest;
import com.zyf.rpc.entity.RpcResponse;
import com.zyf.rpc.enumeration.ResponseCode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @author zyf
 * @date 2022/2/28 14:11
 * @description 实际执行方法调用任务的工作线程
 */
@Slf4j
public class RequestHandler implements Runnable{

    // private static final Logger logger = LoggerFactory.getLogger(WorkerThread.class);

    private Socket socket;
    private Object service;

    public RequestHandler(Socket socket, Object service){
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest)objectInputStream.readObject();
//            //利用反射原理找到远程所需调用的方法
//            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
//            //invoke(obj实例对象,obj可变参数)
//            Object returnObject = method.invoke(service, rpcRequest.getParameters());
            Object returnObject = invokeMethod(rpcRequest);
            objectOutputStream.writeObject(RpcResponse.success(returnObject));
            objectOutputStream.flush();
        }catch (IOException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e){
            log.info("调用或发送时有错误发生：" + e);
        }
    }

    private Object invokeMethod(RpcRequest rpcRequest) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException{
        Class<?> clazz = Class.forName(rpcRequest.getInterfaceName());
        //判断是否为同一类型或存在父子、接口关系
        if(!clazz.isAssignableFrom(service.getClass())){
            return RpcResponse.fail(ResponseCode.ClASS_NOT_FOUND);
        }
        Method method;
        try{
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        }catch (NoSuchMethodException e){
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND);
        }
        return method.invoke(service, rpcRequest.getParameters());
    }
}

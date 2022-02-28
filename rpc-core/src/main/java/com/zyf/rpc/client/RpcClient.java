package com.zyf.rpc.client;

import com.zyf.rpc.entity.RpcRequest;
import com.zyf.rpc.entity.RpcResponse;
import com.zyf.rpc.enumeration.ResponseCode;
import com.zyf.rpc.enumeration.RpcError;
import com.zyf.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author zyf
 * @date 2022/2/27 22:35
 * @description 进行远程调用的客户端
 */
@Slf4j
public class RpcClient {

    // private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    /**
     * 直接使用Java的序列化方式，通过Socket传输。创建一个Socket，获取ObjectOutputStream对象，
     * 然后把需要发送的对象传进去即可，接收时获取ObjectInputStream对象，readObject()方法就可以获得一个返回的对象。
     * @param rpcRequest
     * @param host
     * @param port
     * @return
     */
    public Object sendRequest(RpcRequest rpcRequest, String host, int port) {
        /**
         * socket套接字实现TCP网络传输
         * try()中一般放对资源的申请，若{}出现异常，()资源会自动关闭
         */
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            // return objectInputStream.readObject();

            // 新增错误和异常处理
            RpcResponse rpcResponse = (RpcResponse)objectInputStream.readObject();
            if(rpcResponse == null){
                log.error("服务调用失败，service:{}" + rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, "service:" + rpcRequest.getInterfaceName());
            }
            if(rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode() != ResponseCode.SUCCESS.getCode()){
                log.error("服务调用失败，service:{} response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, "service:" + rpcRequest.getInterfaceName());
            }
            return rpcResponse.getData();
        } catch (IOException | ClassNotFoundException e) {
            log.error("调用时有错误发生：" + e);
            // return null;
            throw new RpcException("服务调用失败：", e);
        }
    }
}

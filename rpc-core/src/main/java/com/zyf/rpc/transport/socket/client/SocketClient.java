package com.zyf.rpc.transport.socket.client;

import com.zyf.rpc.entity.RpcRequest;
import com.zyf.rpc.entity.RpcResponse;
import com.zyf.rpc.enumeration.RpcError;
import com.zyf.rpc.exception.RpcException;
import com.zyf.rpc.register.NacosServiceDiscovery;
import com.zyf.rpc.register.ServiceDiscovery;
import com.zyf.rpc.serializer.CommonSerializer;
import com.zyf.rpc.transport.RpcClient;
import com.zyf.rpc.transport.socket.util.ObjectReader;
import com.zyf.rpc.transport.socket.util.ObjectWriter;
import com.zyf.rpc.util.RpcMessageChecker;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author zyf
 * @date 2022/2/27 22:35
 * @description 进行远程调用的客户端
 */
@Slf4j
public class SocketClient implements RpcClient {

    // private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    private final ServiceDiscovery serviceDiscovery;
    private final CommonSerializer serializer;

    public SocketClient() {
        this(DEFAULT_SERIALIZER);
    }

    public SocketClient(Integer serializerCode) {
        serviceDiscovery = new NacosServiceDiscovery();
        serializer = CommonSerializer.getByCode(serializerCode);
    }

    /**
     * 直接使用Java的序列化方式，通过Socket传输。创建一个Socket，获取ObjectOutputStream对象，
     * 然后把需要发送的对象传进去即可，接收时获取ObjectInputStream对象，readObject()方法就可以获得一个返回的对象。
     * @param rpcRequest
     * @return
     */
    @Override
    public Object sendRequest(RpcRequest rpcRequest){
        if (serializer == null) {
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        //从Nacos获取提供对应服务的服务端地址
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
        /**
         * socket套接字实现TCP网络传输
         * try()中一般放对资源的申请，若{}出现异常，()资源会自动关闭
         */
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);
            Object obj = ObjectReader.readObject(inputStream);
            RpcResponse rpcResponse = (RpcResponse) obj;
            RpcMessageChecker.check(rpcRequest, rpcResponse);
            return rpcResponse.getData();
        } catch (IOException e) {
            log.error("调用时有错误发生：" + e);
            throw new RpcException("服务调用失败：", e);
        }
    }

}

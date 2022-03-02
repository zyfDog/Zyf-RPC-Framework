package com.zyf.rpc.test;

import com.zyf.rpc.api.HelloService;
import com.zyf.rpc.serializer.ProtostuffSerializer;
import com.zyf.rpc.transport.netty.server.NettyServer;

/**
 * @author zyf
 * @date 2022/2/28 20:50
 * @description 测试用Netty服务端
 */
public class NettyTestServer {
    public static void main(String[] args) {
        /*HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        NettyServer server = new NettyServer();
        server.setSerializer(new ProtostuffSerializer());
        server.start(9999);*/
        HelloService helloService = new HelloServiceImpl();
        NettyServer server = new NettyServer("127.0.0.1", 9999);
        server.setSerializer(new ProtostuffSerializer());
        server.publishService(helloService, HelloService.class);
    }
}

package com.zyf.rpc.test;

import com.zyf.rpc.api.HelloService;
import com.zyf.rpc.netty.server.NettyServer;
import com.zyf.rpc.registry.DefaultServiceRegistry;
import com.zyf.rpc.registry.ServiceRegistry;
import com.zyf.rpc.serializer.KryoSerializer;

/**
 * @author zyf
 * @date 2022/2/28 20:50
 * @description 测试用Netty服务端
 */
public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        NettyServer server = new NettyServer();
        server.setSerializer(new KryoSerializer());
        server.start(9999);
    }
}

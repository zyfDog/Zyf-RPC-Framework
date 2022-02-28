package com.zyf.rpc.test;

import com.zyf.rpc.api.HelloService;
import com.zyf.rpc.registry.DefaultServiceRegistry;
import com.zyf.rpc.registry.ServiceRegistry;
import com.zyf.rpc.socket.server.SocketServer;

/**
 * @author zyf
 * @date 2022/2/28 14:41
 * @description 测试用服务端
 */
public class SocketTestServer {
    public static void main(String[] args) {
        //创建服务对象
        HelloService helloService = new HelloServiceImpl();
        //创建服务容器
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        //注册服务对象到服务容器中
        serviceRegistry.register(helloService);
        //将服务容器纳入到服务端
        SocketServer rpcServer = new SocketServer(serviceRegistry);
        //启动服务端
        rpcServer.start(9000);
    }
}

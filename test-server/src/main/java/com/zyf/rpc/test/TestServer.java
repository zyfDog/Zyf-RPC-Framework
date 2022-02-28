package com.zyf.rpc.test;

import com.zyf.rpc.api.HelloService;
import com.zyf.rpc.server.RpcServer;

/**
 * @author zyf
 * @date 2022/2/28 14:41
 * @description 测试用服务端
 */
public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        //注册HelloServiceImpl服务
        rpcServer.register(helloService, 9000);
    }
}

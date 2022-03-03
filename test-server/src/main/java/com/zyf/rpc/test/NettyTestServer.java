package com.zyf.rpc.test;

import com.zyf.rpc.api.HelloService;
import com.zyf.rpc.serializer.CommonSerializer;
import com.zyf.rpc.transport.netty.server.NettyServer;

/**
 * @author zyf
 * @date 2022/2/28 20:50
 * @description 测试用Netty服务端
 */
public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        NettyServer server = new NettyServer("127.0.0.1", 9999, CommonSerializer.PROTOBUF_SERIALIZER);
        server.publishService(helloService, HelloService.class);
    }
}

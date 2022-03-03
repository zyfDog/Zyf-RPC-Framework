package com.zyf.rpc.test;

import com.zyf.rpc.api.HelloObject;
import com.zyf.rpc.api.HelloService;
import com.zyf.rpc.serializer.CommonSerializer;
import com.zyf.rpc.transport.RpcClient;
import com.zyf.rpc.transport.RpcClientProxy;
import com.zyf.rpc.transport.netty.client.NettyClient;

/**
 * @author zyf
 * @date 2022/2/28 20:50
 * @description 测试用Netty客户端
 */
public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient(CommonSerializer.PROTOBUF_SERIALIZER);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "this is netty style");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}

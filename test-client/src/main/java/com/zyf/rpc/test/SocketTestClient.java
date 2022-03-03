package com.zyf.rpc.test;

import com.zyf.rpc.api.ByeService;
import com.zyf.rpc.api.HelloObject;
import com.zyf.rpc.api.HelloService;
import com.zyf.rpc.loadbalancer.RoundRobinLoadBalancer;
import com.zyf.rpc.serializer.CommonSerializer;
import com.zyf.rpc.transport.RpcClientProxy;
import com.zyf.rpc.transport.socket.client.SocketClient;

/**
 * @author zyf
 * @date 2022/2/28 14:43
 * @description 测试用客户端
 */
public class SocketTestClient {
    public static void main(String[] args) {
        SocketClient client = new SocketClient(CommonSerializer.KRYO_SERIALIZER, new RoundRobinLoadBalancer());
        RpcClientProxy proxy = new RpcClientProxy(client);
        //创建代理对象
        HelloService helloService = proxy.getProxy(HelloService.class);
        //接口方法的参数对象
        HelloObject object = new HelloObject(12, "This is test message");
        //由动态代理可知，代理对象调用hello()实际会执行invoke()
        String res = helloService.hello(object);
        System.out.println(res);
        ByeService byeService = proxy.getProxy(ByeService.class);
        System.out.println(byeService.bye("Netty"));
    }
}

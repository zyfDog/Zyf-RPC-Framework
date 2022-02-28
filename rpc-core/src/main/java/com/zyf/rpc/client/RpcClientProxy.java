package com.zyf.rpc.client;

import com.zyf.rpc.entity.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author zyf
 * @date 2022/2/27 22:37
 * @description Rpc客户端动态代理
 * 客户端方面，由于在客户端这一侧我们并没有接口的具体实现类，就没有办法直接生成实例对象。
 * 这时，我们可以通过动态代理的方式生成实例，并且调用方法时生成需要的RpcRequest对象并且发送给服务端。
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private String host;
    private int port;

    /**
     * 传递host和port来指明服务端的位置
     * @param host
     * @param port
     */
    public RpcClientProxy(String host, int port){
        this.host = host;
        this.port = port;
    }

    /**
     *
     * @param clazz
     * @param <T>
     * @return
     */
    //抑制编译器产生警告信息
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz){
        //创建代理对象
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("调用方法：{}#{}", method.getDeclaringClass().getName(), method.getName());
        //客户端向服务端传输的对象,
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
        //进行远程调用的客户端
        RpcClient rpcClient = new RpcClient();
        // return ((RpcResponse)rpcClient.sendRequest(rpcRequest, host, port)).getData();
        return rpcClient.sendRequest(rpcRequest, host, port);
    }
}

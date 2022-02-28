package com.zyf.rpc.api;

/**
 * @author zyf
 * @date 2022/2/27 21:56
 * @description 通用接口
 */
public interface HelloService {
    /**
     * 接口方法 传输一个可序列化的对象
     * @param object
     * @return
     */
    String hello(HelloObject object);
}

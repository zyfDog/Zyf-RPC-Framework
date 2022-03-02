package com.zyf.rpc;

import com.zyf.rpc.serializer.CommonSerializer;

/**
 * @author zyf
 * @date 2022/2/28 18:20
 * @description 服务端类通用接口
 */
public interface RpcServer {

    void start();

    void setSerializer(CommonSerializer serializer);

    /**
     * @description 向Nacos注册服务
     */
    <T> void publishService(Object service, Class<T> serviceClass);
}

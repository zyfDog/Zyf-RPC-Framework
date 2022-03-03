package com.zyf.rpc.transport;

import com.zyf.rpc.serializer.CommonSerializer;

/**
 * @author zyf
 * @date 2022/2/28 18:20
 * @description 服务端类通用接口
 */
public interface RpcServer {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    void start();

    /**
     * @description 向Nacos注册服务
     */
    <T> void publishService(T service, String serviceName);

}

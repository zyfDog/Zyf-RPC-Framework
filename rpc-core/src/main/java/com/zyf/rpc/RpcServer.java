package com.zyf.rpc;

import com.zyf.rpc.serializer.CommonSerializer;

/**
 * @author zyf
 * @date 2022/2/28 18:20
 * @description 服务端类通用接口
 */
public interface RpcServer {

    void start(int port);

    void setSerializer(CommonSerializer serializer);
}

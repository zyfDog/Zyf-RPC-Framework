package com.zyf.rpc.transport;

import com.zyf.rpc.entity.RpcRequest;
import com.zyf.rpc.serializer.CommonSerializer;

/**
 * @author zyf
 * @date 2022/2/28 18:23
 * @description 客户端类通用接口
 */
public interface RpcClient {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    Object sendRequest(RpcRequest rpcRequest);

}

package com.zyf.rpc;

import com.zyf.rpc.entity.RpcRequest;

/**
 * @author zyf
 * @date 2022/2/28 18:23
 * @description 客户端类通用接口
 */
public interface RpcClient {

    Object sendRequest(RpcRequest rpcRequest);
}

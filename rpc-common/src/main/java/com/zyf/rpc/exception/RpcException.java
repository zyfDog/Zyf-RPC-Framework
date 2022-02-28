package com.zyf.rpc.exception;

import com.zyf.rpc.enumeration.RpcError;

/**
 * @author zyf
 * @date 2022/2/28 14:58
 * @description Rpc调用异常
 */
public class RpcException extends RuntimeException{

    public RpcException(RpcError error, String detail){
        super(error.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause){
        super(message, cause);
    }

    public RpcException(RpcError error){
        super(error.getMessage());
    }
}

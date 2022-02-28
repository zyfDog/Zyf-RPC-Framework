package com.zyf.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zyf
 * @date 2022/2/28 14:57
 * @description Rpc调用过程中出现的错误
 */
@AllArgsConstructor
@Getter
public enum RpcError {

    SERVICE_INVOCATION_FAILURE("服务调用出现失败"),
    SERVICE_CAN_NOT_BE_NULL("注册的服务不能为空"),
    SERVICE_NOT_FOUND("找不到对应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现接口");

    private final String message;
}

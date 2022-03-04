package com.zyf.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zyf
 * @date 2022/2/27 22:13
 * @description 响应状态码对象
 */
@AllArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS(200,"调用方法成功"),
    FAIL(500,"调用方法失败"),
    METHOD_NOT_FOUND(500,"未找到指定方法"),
    ClASS_NOT_FOUND(500,"未找到指定类");

    private final int code;
    private final String message;
}

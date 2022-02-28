package com.zyf.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zyf
 * @date 2022/2/28 19:02
 * @description 字节流中标识序列化和反序列化器
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {
    KRYO(0),
    JSON(1),
    HESSIAN(2);

    private final int code;
}

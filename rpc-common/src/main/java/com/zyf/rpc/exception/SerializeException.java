package com.zyf.rpc.exception;

/**
 * @author zyf
 * @date 2022/2/28 21:34
 * @description 序列化异常
 */
public class SerializeException extends RuntimeException{

    public SerializeException(String msg){
        super(msg);
    }
}

package com.zyf.rpc.serializer;

/**
 * @author zyf
 * @date 2022/2/28 19:00
 * @description 通用的序列化反序列化接口
 */
public interface CommonSerializer {

    /**
     * 序列化
     * @param obj
     * @return
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     * @param bytes
     * @param clazz
     * @return
     */
    Object deserialize(byte[] bytes, Class<?> clazz);

    /**
     * 获得该序列化器的编号
     * @return
     */
    int getCode();

    /**
     * 根据编号获取序列化器
     * @param code
     * @return
     */
    static CommonSerializer getByCode(int code){
        switch (code){
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }

}

package com.zyf.rpc.registry;

/**
 * @author zyf
 * @date 2022/2/28 15:15
 * @description 服务注册表通用接口
 */
public interface ServiceRegistry {

    /**
     * 将一个服务注册进注册表
     * @param service
     * @param <T>
     */
    <T> void register(T service);

    /**
     * 根据服务名获取服务实体
     * @param serviceName
     * @return
     */
    Object getService(String serviceName);

}

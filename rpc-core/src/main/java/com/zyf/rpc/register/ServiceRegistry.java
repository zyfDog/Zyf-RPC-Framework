package com.zyf.rpc.register;

import java.net.InetSocketAddress;

/**
 * @author zyf
 * @date 2022/3/2 22:47
 * @description 服务注册接口
 */
public interface ServiceRegistry {

    /**
     * @description 将一个服务注册到注册表
     * @param serviceName, inetSocketAddress 服务名称，提供服务的地址
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);

    /**
     * @description 根据服务名查找服务实体
    InetSocketAddress lookupService(String serviceName);*/

}

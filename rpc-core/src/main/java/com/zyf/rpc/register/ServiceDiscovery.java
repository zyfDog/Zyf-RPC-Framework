package com.zyf.rpc.register;

import java.net.InetSocketAddress;

/**
 * @author zyf
 * @date 2022/3/3 20:00
 * @description 服务发现接口
 */
public interface ServiceDiscovery {

    /**
     * @description 根据服务名称查找服务端地址
     */
    InetSocketAddress lookupService(String serviceName);
}

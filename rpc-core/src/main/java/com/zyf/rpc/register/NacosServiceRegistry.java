package com.zyf.rpc.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.zyf.rpc.enumeration.RpcError;
import com.zyf.rpc.exception.RpcException;
import com.zyf.rpc.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author zyf
 * @date 2022/3/2 22:47
 * @description Nacos服务注册中心
 */
@Slf4j
public class NacosServiceRegistry implements ServiceRegistry{

    /**
     * @description 将服务的名称和地址注册进服务注册中心
     */
    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            //向Nacos注册服务
            NacosUtil.registerService(serviceName, inetSocketAddress);
        }catch (NacosException e) {
            log.error("注册服务时有错误发生" + e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }
}

package com.zyf.rpc.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
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

    /*private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static final NamingService namingService;*/
    public final NamingService namingService;

    /*static {
        try {
            //连接Nacos创建命名服务
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        }catch (NacosException e){
            logger.error("连接Nacos时有错误发生：" + e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }*/
    public NacosServiceRegistry() {
        namingService = NacosUtil.getNacosNamingService();
    }

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

    /**
     * @description 根据服务名称从注册中心获取到一个服务提供者的地址
    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            //利用列表获取某个服务的所有提供者
            List<Instance> instances = namingService.getAllInstances(serviceName);
            Instance instance = instances.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        }catch (NacosException e) {
            log.error("获取服务时有错误发生" + e);
        }
        return null;
    }*/
}

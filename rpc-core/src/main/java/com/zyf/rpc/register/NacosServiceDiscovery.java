package com.zyf.rpc.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.zyf.rpc.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author zyf
 * @date 2022/3/3 19:57
 * @description 服务发现
 */
@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery{

    // private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);
    private final NamingService namingService;

    public NacosServiceDiscovery(){
        namingService = NacosUtil.getNacosNamingService();
    }

    /**
     * @description 根据服务名称从注册中心获取到一个服务提供者的地址
     */
    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            //利用列表获取某个服务的所有提供者
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            Instance instance = instances.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        }catch (NacosException e) {
            log.error("获取服务时有错误发生", e);
        }
        return null;
    }
}

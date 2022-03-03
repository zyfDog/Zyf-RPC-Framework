package com.zyf.rpc.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.zyf.rpc.loadbalancer.LoadBalancer;
import com.zyf.rpc.loadbalancer.RandomLoadBalancer;
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
    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer){
        if (loadBalancer == null){
            this.loadBalancer = new RandomLoadBalancer();
        }else {
            this.loadBalancer = loadBalancer;
        }
    }

    /**
     * @description 根据服务名称从注册中心获取到一个服务提供者的地址
     */
    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            //利用列表获取某个服务的所有提供者
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            //负载均衡获取一个服务实体
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        }catch (NacosException e) {
            log.error("获取服务时有错误发生", e);
        }
        return null;
    }
}

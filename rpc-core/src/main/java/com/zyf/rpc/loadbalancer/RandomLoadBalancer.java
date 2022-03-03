package com.zyf.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * @author zyf
 * @date 2022/3/3 22:52
 * @description 随机选择一个
 */
public class RandomLoadBalancer implements LoadBalancer{

    @Override
    public Instance select(List<Instance> instances) {
        return instances.get(new Random().nextInt(instances.size()));
    }
}

package com.zyf.rpc.provider;

/**
 * @author zyf
 * @date 2022/3/2 22:45
 * @description 保存和提供服务实例对象
 */
public interface ServiceProvider {

    <T> void addServiceProvider(T service, Class<T> serviceClass);

    Object getServiceProvider(String serviceName);

}

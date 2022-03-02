package com.zyf.rpc.provider;

import com.zyf.rpc.enumeration.RpcError;
import com.zyf.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zyf
 * @date 2022/3/2 22:45
 * @description 默认的服务注册表，保存服务端本地服务
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

    // private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);
    /**
     * key = 服务名称(即接口名), value = 服务实体(即实现类的实例对象)
     */
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    /**
     * 用来存放实现类的名称，Set存取更高效，存放实现类名称相比存放接口名称占的空间更小，因为一个实现类可能实现了多个接口，查找效率也会更高
     */
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public <T> void addServiceProvider(T service) {
        String serviceImplName = service.getClass().getCanonicalName();
        if(registeredService.contains(serviceImplName)){
            return;
        }
        registeredService.add(serviceImplName);
        //可能实现了多个接口，故使用Class数组接收
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length == 0){
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for(Class<?> i : interfaces){
            serviceMap.put(i.getCanonicalName(), service);
        }
        log.info("向接口：{} 注册服务：{}", interfaces, serviceImplName);
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service == null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}

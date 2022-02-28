package com.zyf.rpc.registry;

import com.zyf.rpc.enumeration.RpcError;
import com.zyf.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zyf
 * @date 2022/2/28 15:18
 * @description 默认的服务注册表
 */
@Slf4j
public class DefaultServiceRegistry implements ServiceRegistry{

    // private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);
    /**
     * key = 服务名称(即接口名), value = 服务实体(即实现类的实例对象)
     * 将服务名与提供服务的对象的对应关系保存在一个 ConcurrentHashMap 中
     *
     * 接口名 + 实现类
     */
    private final static Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    /**
     * 用来存放实现类的名称，Set存取更高效，存放实现类名称相比存放接口名称占的空间更小，因为一个实现类可能实现了多个接口
     * 使用一个 Set 来保存当前有哪些对象已经被注册
     *
     * 实现类名
     */
    private final static Set<String> registeredService = ConcurrentHashMap.newKeySet();

    /**
     *
     * @param service
     * @param <T>
     */
    @Override
    public synchronized <T> void register(T service) {
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
    public synchronized Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service == null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}

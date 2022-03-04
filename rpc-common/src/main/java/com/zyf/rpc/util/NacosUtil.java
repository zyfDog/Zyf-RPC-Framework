package com.zyf.rpc.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.zyf.rpc.enumeration.RpcError;
import com.zyf.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author zyf
 * @date 2022/3/3 19:51
 * @description 管理Nacos连接等工具类
 */
@Slf4j
public class NacosUtil {

    // private static final Logger logger = LoggerFactory.getLogger(NacosUtil.class);

    private static final NamingService namingService;
    private static final Set<String> serviceNames = new HashSet<>();
    private static InetSocketAddress address;
    private static final String SERVER_ADDR = "127.0.0.1:8848";

    static {
        namingService = getNacosNamingService();
    }

    /**
     * @description 连接到Nacos创建命名空间 服务发现
     */
    public static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(SERVER_ADDR);
        }catch (NacosException e) {
            log.error("连接到Nacos时有错误发生：", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    /**
     * @description 注册服务到Nacos
     */
    public static void registerService(String serviceName, InetSocketAddress address) throws NacosException {
        /**
         * 服务名、服务实例IP、服务实例port
         */
        namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
        NacosUtil.address = address;
        //保存注册的服务名
        serviceNames.add(serviceName);
    }

    /**
     * @description 获取所有提供该服务的服务端地址
     */
    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    /**
     * @description 注销服务
     */
    public static void clearRegistry() {
        if(!serviceNames.isEmpty() && address != null) {
            String host = address.getHostName();
            int port = address.getPort();
            //利用迭代器迭代注销
            Iterator<String> iterator = serviceNames.iterator();
            while (iterator.hasNext()) {
                String serviceName = iterator.next();
                try {
                    //注销服务
                    namingService.deregisterInstance(serviceName, host, port);
                }catch (NacosException e) {
                    log.error("注销服务{}失败", serviceName, e);
                }
            }
        }
    }
}

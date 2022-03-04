package com.zyf.rpc.transport;

import com.zyf.rpc.annotation.Service;
import com.zyf.rpc.annotation.ServiceScan;
import com.zyf.rpc.enumeration.RpcError;
import com.zyf.rpc.exception.RpcException;
import com.zyf.rpc.provider.ServiceProvider;
import com.zyf.rpc.register.ServiceRegistry;
import com.zyf.rpc.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * @author zyf
 * @date 2022/3/3 23:48
 * @description 扫描服务类并进行服务注册
 */
@Slf4j
public abstract class AbstractRpcServer implements RpcServer{

    // protected Logger logger = LoggerFactory.getLogger(AbstractRpcServer.class);

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    public void scanServices(){
        //获取main()入口所在类的类名，即启动类
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            //获取启动类对应的实例对象
            startClass = Class.forName(mainClassName);
            //判断启动类是否存在ServiceScan注解
            if(!startClass.isAnnotationPresent(ServiceScan.class)){
                log.error("启动类缺少@ServiceScan注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        }catch (ClassNotFoundException e){
            log.info("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        //获取ServiceScan注解接口对应value()的值，默认设置的“”
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if("".equals(basePackage)){
            //获取启动类所在的包，因为服务类也放在这个包下面的
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        //获取包下面的所有类Class对象
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for(Class<?> clazz : classSet){
            //利用Service注解判断该类是否为服务类
            if(clazz.isAnnotationPresent(Service.class)){
                //获取Service注解接口对应name()的值，默认设置的“”
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try{
                    //创建服务Impl类的实例
                    obj = clazz.newInstance();
                }catch (IllegalAccessException | InstantiationException e){
                    log.error("创建" + clazz + "时有错误发生");
                    continue;
                }
                if("".equals(serviceName)){
                    //一个服务Impl类可能实现了多个服务接口
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for(Class<?> oneInterface : interfaces){
                        // 实现类实例 + 接口名
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                }else {
                    publishService(obj, serviceName);
                }
            }
        }
    }

    /**
     * @description 将服务保存在本地的注册表，同时注册到Nacos
     */
    @Override
    public <T> void publishService(T service, String serviceName){
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }

}

package com.zyf.rpc;

import com.zyf.rpc.entity.RpcRequest;
import com.zyf.rpc.entity.RpcResponse;
import com.zyf.rpc.enumeration.ResponseCode;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zyf
 * @date 2022/2/28 14:11
 * @description 实际执行方法调用的处理器,通过反射进行方法调用
 */
@Slf4j
public class RequestHandler{

    // private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    /**
     *
     * @param rpcRequest
     * @param service 实现类
     * @return
     */
    public Object handle(RpcRequest rpcRequest, Object service){
        Object result = null;
        try{
            result = invokeTargetMethod(rpcRequest, service);
            log.info("服务：{}成功调用方法：{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        }catch (IllegalAccessException | InvocationTargetException e){
            log.info("调用或发送时有错误发生：" + e);
        }
        return result;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest,Object service) throws InvocationTargetException, IllegalAccessException{
        Method method;
        try{
            // getClass()获取的是实例对象的类型
            // 利用反射原理找到远程所需调用的方法
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        }catch (NoSuchMethodException e){
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND);
        }
        // invoke(obj实例对象,obj可变参数)
        return method.invoke(service, rpcRequest.getParameters());
    }
}

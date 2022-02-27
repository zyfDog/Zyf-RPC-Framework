package com.zyf.rpc.test;

import com.zyf.rpc.api.HelloObject;
import com.zyf.rpc.api.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zyf
 * @date 2022/2/27 22:01
 * @description 服务端api接口实现
 */
@Slf4j
public class HelloServiceImpl implements HelloService {

    /**
     * 使用HelloServiceImpl初始化日志对象，方便在日志输出的时候，可以打印出日志信息所属的类。
     */
    // private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        //使用{}可以直接将getMessage()内容输出
        log.info("接收到：{}", object.getMessage());
        return "这是调用的返回值：id=" + object.getId();
    }
}

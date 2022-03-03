package com.zyf.rpc.test;

import com.zyf.rpc.api.HelloObject;
import com.zyf.rpc.api.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zyf
 * @date 2022/3/3 20:55
 * @description 服务端api接口实现
 */
@Slf4j
public class HelloServiceImpl2 implements HelloService {

    // private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl2.class);

    @Override
    public String hello(HelloObject object) {
        log.info("接收到消息：{}", object.getMessage());
        return "本次处理来自Socket服务";

    }
}

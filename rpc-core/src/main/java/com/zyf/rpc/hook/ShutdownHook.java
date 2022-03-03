package com.zyf.rpc.hook;

import com.zyf.rpc.factory.ThreadPoolFactory;
import com.zyf.rpc.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zyf
 * @date 2022/3/3 21:18
 * @description
 */
@Slf4j
public class ShutdownHook {

    // private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    // private final ExecutorService threadPool = ThreadPoolFactory.createDefaultThreadPool("shutdown-hook");
    /**
     *单例模式创建钩子，保证全局只有这一个钩子
     */
    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook(){
        return shutdownHook;
    }

    //注销服务的钩子
    public void addClearAllHook() {
        log.info("服务端关闭前将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            //关闭所有线程池
            ThreadPoolFactory.shutDownAll();
        }));
    }
}

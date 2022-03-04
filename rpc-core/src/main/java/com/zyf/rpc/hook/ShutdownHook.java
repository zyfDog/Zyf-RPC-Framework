package com.zyf.rpc.hook;

import com.zyf.rpc.factory.ThreadPoolFactory;
import com.zyf.rpc.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zyf
 * @date 2022/3/3 21:18
 * @description 实现服务自动注销
 */
@Slf4j
public class ShutdownHook {

    // private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    // private final ExecutorService threadPool = ThreadPoolFactory.createDefaultThreadPool("shutdown-hook");
    /**
     *静态单例模式创建钩子，保证全局只有这一个钩子
     */
    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook(){
        return shutdownHook;
    }

    /**
     * 注销服务的钩子
     * 在addClearAllHook()中，Runtime对象是JVM虚拟机的运行时环境，调用其addShutdownHook()方法增加一个钩子函数，
     * 创建一个新线程调用clearRegistry()完成注销工作。这个钩子函数会在JVM关闭之前被调用。这样只需要把钩子放在服务端，
     * 启动服务端时就能注册钩子了，以NettyServer为例，启动服务端后再关闭，就会发现Nacos中的注册信息已经被注销了。
     */
    public void addClearAllHook() {
        log.info("服务端关闭前将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            //关闭所有线程池 Socket中的？
            ThreadPoolFactory.shutDownAll();
        }));
    }
}

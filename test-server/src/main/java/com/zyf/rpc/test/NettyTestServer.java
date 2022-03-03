package com.zyf.rpc.test;

import com.zyf.rpc.annotation.ServiceScan;
import com.zyf.rpc.serializer.CommonSerializer;
import com.zyf.rpc.transport.RpcServer;
import com.zyf.rpc.transport.netty.server.NettyServer;

/**
 * @author zyf
 * @date 2022/2/28 20:50
 * @description 测试用Netty服务端
 */
@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        RpcServer server = new NettyServer("127.0.0.1", 9999, CommonSerializer.PROTOBUF_SERIALIZER);
        server.start();
    }
}

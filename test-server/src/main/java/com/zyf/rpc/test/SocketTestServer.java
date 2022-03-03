package com.zyf.rpc.test;

import com.zyf.rpc.annotation.ServiceScan;
import com.zyf.rpc.serializer.CommonSerializer;
import com.zyf.rpc.transport.RpcServer;
import com.zyf.rpc.transport.socket.server.SocketServer;

/**
 * @author zyf
 * @date 2022/2/28 14:41
 * @description 测试用服务端
 */
@ServiceScan
public class SocketTestServer {
    public static void main(String[] args) {
        RpcServer server = new SocketServer("127.0.0.1", 9998, CommonSerializer.HESSIAN_SERIALIZER);
        server.start();
    }
}

package com.zyf.rpc.transport.socket.server;

import com.zyf.rpc.handler.RequestHandler;
import com.zyf.rpc.entity.RpcRequest;
import com.zyf.rpc.serializer.CommonSerializer;
import com.zyf.rpc.transport.socket.util.ObjectReader;
import com.zyf.rpc.transport.socket.util.ObjectWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author zyf
 * @date 2022/2/28 15:38
 * @description 处理客户端RpcRequest的工作线程
 * 利用客户端传来的RpcRequest对象，从ServiceRegistry 中获取提供服务的对象。
 * RequestHandlerThread 只是一个线程，从ServiceRegistry 获取到提供服务的对象后，
 * 就会把 RpcRequest 和服务对象直接交给 RequestHandler 去处理，反射等过程被放到了 RequestHandler 里。
 */
@Slf4j
public class SocketRequestHandlerThread implements Runnable {

    // private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private CommonSerializer serializer;

    public SocketRequestHandlerThread(Socket socket, RequestHandler requestHandler, CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        try(InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream()) {
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            Object response = requestHandler.handle(rpcRequest);
            ObjectWriter.writeObject(outputStream, response, serializer);
        }catch (IOException e){
            log.info("调用或发送时发生错误：" + e);
        }
    }
}
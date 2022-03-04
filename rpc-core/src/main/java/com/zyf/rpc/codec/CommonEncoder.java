package com.zyf.rpc.codec;

import com.zyf.rpc.entity.RpcRequest;
import com.zyf.rpc.enumeration.PackageType;
import com.zyf.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author zyf
 * @date 2022/2/28 19:07
 * @description 通用编码拦截器
 * 把 Message（实际要发送的对象）转化成 Byte 数组
 * 编码器的工作就是把原始数据转换为字节流，然后根据上面提到的协议格式，将各个字段写到一个字节数组中（堆外内存ByteBuf[ ]），
 * 这样的字节数组就是发送出去的自定义协议包。
 */
public class CommonEncoder extends MessageToByteEncoder {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer){
        this.serializer = serializer;
    }

    /**
     * 协议 ：Magic Number + Package Type + Serializer Type + Data Length + Data
     * @param ctx
     * @param msg RpcRequest对象
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        //数据写到缓冲区
        // 魔数
        out.writeInt(MAGIC_NUMBER);
        // 包类型
        if(msg instanceof RpcRequest){
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        }else {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        // 序列化器编号
        out.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        // 实际数据的长度
        out.writeInt(bytes.length);
        // 经过序列化后的实际数据
        out.writeBytes(bytes);
    }
}

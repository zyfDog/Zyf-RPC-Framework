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
 */
public class CommonEncoder extends MessageToByteEncoder {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer){
        this.serializer = serializer;
    }

    /**
     * 协议 ：Magic Number + Package Type + Serializer Type + Data Length
     * @param ctx
     * @param msg RpcRequest对象
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        //数据写到缓冲区
        out.writeInt(MAGIC_NUMBER);
        if(msg instanceof RpcRequest){
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        }else {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        out.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}

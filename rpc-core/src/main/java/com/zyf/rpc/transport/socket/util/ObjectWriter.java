package com.zyf.rpc.transport.socket.util;

import com.zyf.rpc.entity.RpcRequest;
import com.zyf.rpc.enumeration.PackageType;
import com.zyf.rpc.serializer.CommonSerializer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author zyf
 * @date 2022/3/2 19:20
 * @description Socket方式将数据序列化并写入输出流中【编码】
 */
public class ObjectWriter {

    // private static final Logger logger = LoggerFactory.getLogger(ObjectWriter.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    /**
     *
     * @param out 输出流
     * @param object 请求或响应
     * @param serializer 序列化器
     * @throws IOException
     */
    public static void writeObject(OutputStream out, Object object, CommonSerializer serializer) throws IOException {
        // 魔数+通信包类型+序列化器编号+序列化后实际数据长度+序列化后实际数据
        out.write(intToBytes(MAGIC_NUMBER));
        if(object instanceof RpcRequest) {
            out.write(intToBytes(PackageType.REQUEST_PACK.getCode()));
        } else {
            out.write(intToBytes(PackageType.RESPONSE_PACK.getCode()));
        }
        out.write(intToBytes(serializer.getCode()));
        byte[] bytes = serializer.serialize(object);
        out.write(intToBytes(bytes.length));
        out.write(bytes);
        out.flush();
    }

    /**
     * @description 将Int转换为字节数组
     */
    private static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16) & 0xFF);
        src[2] = (byte) ((value>>8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }
}

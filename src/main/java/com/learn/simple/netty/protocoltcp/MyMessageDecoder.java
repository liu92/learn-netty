package com.learn.simple.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @ClassName: MyMessageDecoder
 * @Description: 解码器
 * @Author: lin
 * @Date: 2020/10/21 22:39
 * History:
 * @<version> 1.0
 */
public class MyMessageDecoder extends ReplayingDecoder<MessageProtocol> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("MyMessageDecoder decode 方法被调用了");

        //需要将得到二进制字节码-> MessageProtocol 数据包(对象)
        int length = in.readInt();
        //根据从ByteBuf中读取到 长度，然后将其长度作为 byte数组的大小。
        byte[] content = new byte[length];

        //然后将 读取到的bytes数据 放入到  byte数组中
        in.readBytes(content);

        //将获取到长度，和内容 封装成 MessageProtocol 对象，
        // 放入 out， 传递给下一个 handler 业务处理
        MessageProtocol protocol = new MessageProtocol();
        protocol.setLength(length);
        protocol.setContent(content);

        out.add(protocol);
    }
}

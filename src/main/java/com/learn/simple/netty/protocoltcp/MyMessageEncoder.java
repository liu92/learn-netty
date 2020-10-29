package com.learn.simple.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 对发送信息进行编码处理
 * @ClassName: MyMessageEncoder
 * @Description:
 * @Author: lin
 * @Date: 2020/10/21 22:36
 * History:
 * @<version> 1.0
 */
public class MyMessageEncoder extends MessageToByteEncoder<MessageProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) throws Exception {
        System.out.println("MyMessageEncoder encode 方法被调用了");

        out.writeInt(msg.getLength());
        out.writeBytes(msg.getContent());
    }
}

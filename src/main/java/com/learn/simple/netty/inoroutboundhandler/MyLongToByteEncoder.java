package com.learn.simple.netty.inoroutboundhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @ClassName: MyLongToByteEncoder
 * @Description:
 * @Author: lin
 * @Date: 2020/10/21 14:09
 * History:
 * @<version> 1.0
 */
public class MyLongToByteEncoder extends MessageToByteEncoder<Long> {
    /**
     * 编码的方法
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {

        System.out.println("MyLongToByteEncoder encoder 被调用");
        System.out.println("msg=" + msg);

        //将数据写出去 就可以了
        out.writeLong(msg);
    }
}

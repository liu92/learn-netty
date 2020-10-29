package com.learn.simple.netty.inoroutboundhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @ClassName: MyByteToLongDecoder2
 * @Description: 自定义的解码器
 * @Author: lin
 * @Date: 2020/10/21 13:50
 * History:
 * @<version> 1.0
 */
public class MyByteToLongDecoder2 extends ReplayingDecoder<Void> {
    /**
     * @param ctx 上下文
     * @param in 入站的byteBuf
     * @param out 把数据放到list集合中，然后将解码后是数据 传递到下一个 InboundHandler
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        System.out.println("MyByteToLongDecoder2 被调用");
        int count = 8;
        //在ReplayingDecoder 不需要判断数据是否足够读取，内部会进行管理
        out.add(in.readLong());
    }
}

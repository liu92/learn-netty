package com.learn.simple.netty.inoroutboundhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @ClassName: MyByteToLongDecoder
 * @Description: 自定义的解码器
 * @Author: lin
 * @Date: 2020/10/21 13:50
 * History:
 * @<version> 1.0
 */
public class MyByteToLongDecoder extends ByteToMessageDecoder {

    /**
     * decoder 会根据接收的数据，被调用多次，直到确定没有新的元素被添加到list，
     *  或者是ByteBuf 没有更多的可读字节为止; 如果list out 不为空，就会将list的内容传递给下一个
     *  channelInboundHandler处理，该处理器的方法 也会被调用多次
     *
     * @param ctx 上下文
     * @param in 入站的byteBuf
     * @param out 把数据放到list集合中，然后将解码后是数据 传递到下一个 InboundHandler
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        System.out.println("MyByteToLongDecoder 被调用");
        int count = 8;
        //因为long是8个字节, 需要判断有8个字节,才能读取一个字节
        if(in.readableBytes() >= count ){
            out.add(in.readLong());
        }
    }
}

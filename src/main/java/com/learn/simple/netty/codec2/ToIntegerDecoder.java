package com.learn.simple.netty.codec2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @ClassName: ToIntegerDecoder
 * @Description:
 * @Author: lin
 * @Date: 2020/10/21 11:01
 * History:
 * @<version> 1.0
 */
public class ToIntegerDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
         if(in.readableBytes() >=4){
             out.add(in.readInt());
         }
    }
}

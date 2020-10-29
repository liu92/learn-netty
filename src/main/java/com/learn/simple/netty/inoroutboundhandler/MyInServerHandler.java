package com.learn.simple.netty.inoroutboundhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName: MyInServerHandler
 * @Description: 自定义的handler来 验证handler调用机制
 * @Author: lin
 * @Date: 2020/10/21 13:48
 * History:
 * @<version> 1.0
 */
public class MyInServerHandler extends SimpleChannelInboundHandler<Long> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {

        //这里接收从myByteToLongDecoder传 过来的数据，数据类型是Long，所以这里定义类型是Long

        System.out.println("从客户端" + ctx.channel().remoteAddress() + " 读取到long " + msg);

        //回送消息 给客户端
        ctx.writeAndFlush(98765L);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

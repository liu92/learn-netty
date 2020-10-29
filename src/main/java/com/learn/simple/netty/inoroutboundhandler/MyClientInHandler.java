package com.learn.simple.netty.inoroutboundhandler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @ClassName: MyClientInHandler
 * @Description:
 * @Author: lin
 * @Date: 2020/10/21 14:13
 * History:
 * @<version> 1.0
 */
public class MyClientInHandler extends SimpleChannelInboundHandler<Long> {
    /**
     * 读取 服务器回复的消息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        System.out.println("服务器的ip=" + ctx.channel().remoteAddress());
        System.out.println("收到服务器消息" + msg);
    }

    /**
     * 发送数据
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyClientInHandler 发送了数据");
        //在发送数据时，是往byteBuf里面发送
        //发送的是一个Long
        ctx.writeAndFlush(12345L);

        //如果发送字符串
        //1: 发送16个字节 ，这里会有一个问题，就是在发送字符串类型是时候，在调用这个编码处理器是父类
        //  会去判断这个发送信息的类型是不是 应该处理的类型，如果是就encode,如果不是就跳过
        //2：该处理器的前一个handler是 MyLongToByteEncoder,
        //3: MyLongToByteEncoder父类是 MessageToByteEncoder
        //4: MessageToByteEncoder
//        ctx.writeAndFlush(Unpooled.copiedBuffer("addadsfsfsfwesfe", CharsetUtil.UTF_8));







    }
}

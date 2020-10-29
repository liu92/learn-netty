package com.learn.simple.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 客户端自定义 handler 来发送消息和 接收服务端返回下消息。
 * @ClassName: NettyClientHandler
 * @Description: 客户端自定义 handler 同样继承 ChannelInboundHandlerAdapter
 * @Author: lin
 * @Date: 2020/10/16 10:45
 * History:
 * @<version> 1.0
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * 当通道就绪时，就会触发该方法, 给服务端发送消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client " + ctx);

        //这里 发送一个student 对象到服务器
        StudentPoJo.Student student = StudentPoJo.Student.newBuilder().setId(1)
                .setName("豹子头 林冲")
                .build();
        ctx.writeAndFlush(student);
    }

    /**
     * 当通道有读取事件时，会触发该方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("服务器回复的消息:" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("服务器的地址： "+ ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //打印异常信息
        cause.printStackTrace();
        //关闭管道
        ctx.close();
    }
}

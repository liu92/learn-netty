package com.learn.simple.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 服务端-----处理器 ，按照 byteBuf接收数据类型
 * @ClassName: MyTcpServerHandler
 * @Description:
 * @Author: lin
 * @Date: 2020/10/21 21:23
 * History:
 * @<version> 1.0
 */
public class MyTcpServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private int count ;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

        byte[] buffer = new byte[msg.readableBytes()];
        //将 数据 对到 byte字节数组中
        msg.readBytes(buffer);

        //将buffer转换成字符串，便于输出
        String message = new String(buffer, StandardCharsets.UTF_8);

        System.out.println("服务器端接收到数据 " + message);
        System.out.println("服务器端接收到消息量= " + (++this.count));

        //服务器回送数据给客户端，回送一个随机id,
        ByteBuf responseByteBuf = Unpooled.copiedBuffer(UUID.randomUUID().toString() + "\n", StandardCharsets.UTF_8);

        ctx.writeAndFlush(responseByteBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

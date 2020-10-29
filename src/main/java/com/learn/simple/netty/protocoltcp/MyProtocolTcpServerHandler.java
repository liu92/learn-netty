package com.learn.simple.netty.protocoltcp;

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
public class MyProtocolTcpServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    private int count ;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
         //接收到数据，并处理
        int length = msg.getLength();
        byte[] content = msg.getContent();

        System.out.println();
        System.out.println("服务器接收到信息如下");
        System.out.println("长度=" + length);
        //将字节转换成 字符串
        System.out.println("内容=" + new String(content, StandardCharsets.UTF_8));
        System.out.println("服务器接收到消息包数量=" + (++this.count));

        //回送消息

        String str = UUID.randomUUID().toString();
        byte[] responseContent = str.getBytes(StandardCharsets.UTF_8);
        int responseLength = str.getBytes(StandardCharsets.UTF_8).length;
        //构建一个协议包
        MessageProtocol protocol = new MessageProtocol();
        protocol.setLength(responseLength);
        protocol.setContent(responseContent);
        ctx.writeAndFlush(protocol);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常信息" + cause.getMessage());
        ctx.close();
    }
}

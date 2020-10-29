package com.learn.simple.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName: MyTcpClientHandler
 * @Description: 自定义客户端处理器---tcp粘包和拆包 示例
 * @Author: lin
 * @Date: 2020/10/21 21:16
 * History:
 * @<version> 1.0
 */
public class MyProtocolTcpClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    private  int count;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //使用客户端发送5次

        int c =5;
        //一个for循环发送5次
        for (int i = 0; i <c ; i++) {
            String msg = "今天有点冷哟~";
            byte[] content = msg.getBytes(StandardCharsets.UTF_8);
            int length = msg.getBytes(StandardCharsets.UTF_8).length;

            //设置协议包
            MessageProtocol protocol = new MessageProtocol();
            protocol.setLength(length);
            protocol.setContent(content);
            // 发送到服务端， 因为是一个对象所有进行编码的处理
            ctx.writeAndFlush(protocol);
        }
    }



    /**
     * 读取从服务端 回送的消息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
        int length = msg.getLength();
        byte[] content = msg.getContent();
        System.out.println("客户端接收消息如下：");
        System.out.println("长度=" + length);
        System.out.println("内容=" + new String(content, StandardCharsets.UTF_8));
        System.out.println("客户端接收到消息数量= " + (++this.count));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

package com.learn.simple.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName: MyTcpClientHandler
 * @Description: 自定义客户端处理器---tcp粘包和拆包 示例
 * @Author: lin
 * @Date: 2020/10/21 21:16
 * History:
 * @<version> 1.0
 */
public class MyTcpClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private  int count;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //使用客户端发送10条数据 hello server+ 编号 到服务端

        int c =10;
        //一个for循环发送10条数据
        for (int i = 0; i <c ; i++) {
            ByteBuf buf = Unpooled.copiedBuffer("hello server" + i +" ", StandardCharsets.UTF_8);
            //将buf 发送到管道中
            ctx.writeAndFlush(buf);
        }
    }



    /**
     * 读取从服务端 回送的消息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] buffer = new byte[msg.readableBytes()];
        //将 数据 对到 byte字节数组中
        msg.readBytes(buffer);

        //将buffer转换成字符串，便于输出
        String message = new String(buffer, StandardCharsets.UTF_8);

        System.out.println("客户端接收到消息 " + message);
        System.out.println("客户端接收到消息数量= " + (++this.count));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

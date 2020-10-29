package com.learn.simple.netty.tcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * 服务端----TCP粘包和拆包 示例
 * @ClassName: MyTcpServerInitializer
 * @Description:
 * @Author: lin
 * @Date: 2020/10/21 21:22
 * History:
 * @<version> 1.0
 */
public class MyTcpServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new MyTcpServerHandler());
       
    }
}

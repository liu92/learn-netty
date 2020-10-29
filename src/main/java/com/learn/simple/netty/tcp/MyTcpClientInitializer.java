package com.learn.simple.netty.tcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @ClassName: MyTcpClientInitializer
 * @Description: 自定义初始对象----测试TCP粘包和拆包
 * @Author: lin
 * @Date: 2020/10/21 21:14
 * History:
 * @<version> 1.0
 */
public class MyTcpClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new MyTcpClientHandler());
    }
}

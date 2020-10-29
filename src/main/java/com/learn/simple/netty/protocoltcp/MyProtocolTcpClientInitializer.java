package com.learn.simple.netty.protocoltcp;

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
public class MyProtocolTcpClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //添加一个自定义编码器
        pipeline.addLast(new MyMessageEncoder());
        //添加解码器
        pipeline.addLast(new MyMessageDecoder());
        pipeline.addLast(new MyProtocolTcpClientHandler());
    }
}

package com.learn.simple.netty.protocoltcp;

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
public class MyProtocolTcpServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //添加解码器
        pipeline.addLast(new MyMessageDecoder());
        //添加一个编码器
        pipeline.addLast(new MyMessageEncoder());
        pipeline.addLast(new MyProtocolTcpServerHandler());
    }
}

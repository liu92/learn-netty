package com.learn.simple.netty.inoroutboundhandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @ClassName: MyServerInitializer
 * @Description: 自定义的 Initializer
 * @Author: lin
 * @Date: 2020/10/21 13:46
 * History:
 * @<version> 1.0
 */
public class MyServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        //入站的 handler 进行解码
        //pipeline.addLast(new MyByteToLongDecoder());
        pipeline.addLast(new MyByteToLongDecoder2());

        //出站的handler进行编码, 这两个是部冲突的 独立工作
        pipeline.addLast(new MyLongToByteEncoder());

        //下一个handler处理业务逻辑
        pipeline.addLast(new MyInServerHandler());

    }
}

package com.learn.simple.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @ClassName: TestServerInitializer
 * @Description: 创建ChannelInitializer，将原来的写法提出了单独写，这样方便维护
 * @Author: lin
 * @Date: 2020/10/19 8:16
 * History:
 * @<version> 1.0
 */
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
         //向管道里加入处理器

         ChannelPipeline pipeline = ch.pipeline();

         //加入一个netty提供的 httpServerCodec  codec=>[coder--decoder] 这个是编解码器
         //httpServerCodec 作用说明
         //1. httpServerCodec的 netty提供的一个处理Http的编解码器
         pipeline.addLast("MyHttpServerCodec", new HttpServerCodec());
          //增加一个自定义的handler
         pipeline.addLast("MyTestHttpServerHandler", new TestHttpServerHandler());

         System.out.println("ok.......");
    }
}

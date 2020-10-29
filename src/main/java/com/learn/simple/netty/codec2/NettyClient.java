package com.learn.simple.netty.codec2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
 * @ClassName: NettyClient
 * @Description: 客户端，测试服务端发生消息。并接收服务端回复的消息
 * @Author: lin
 * @Date: 2020/10/16 10:28
 * History:
 * @<version> 1.0
 */
public class NettyClient {
    public static void main(String[] args)throws  Exception {
        //客户端需要一个事件循环组
        EventLoopGroup group = new NioEventLoopGroup();

        try {
        //创建客户端启动对象，服务端使用的ServerBootstrap, 客户端使用Bootstrap
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
                  // 设置客户端通道的实现类(反射)
                 .channel(NioSocketChannel.class)
                  //客户端也需要handler来处理。
                 .handler(new ChannelInitializer<SocketChannel>() {
                     @Override
                     protected void initChannel(SocketChannel ch) throws Exception {
                         ChannelPipeline pipeline = ch.pipeline();

                         //在使用protobuf的时候，客户端在向服务端发送消息时，必须要编码器进行编码
                         //不然服务器是收不到的。
                         pipeline.addLast("encoder", new ProtobufEncoder());
                         //加入自己的处理器
                         pipeline.addLast(new NettyClientHandler());
                     }
                 });
        System.out.println("客户端 ok..");


        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8879).sync();


        channelFuture.channel().closeFuture().sync();
        }finally {
           //关闭
           group.shutdownGracefully();
        }
    }
}

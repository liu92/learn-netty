package com.learn.simple.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

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

        //设置相关参数
        //1.设置线程组, 放入事件循环组，客户端也是处于一个循环的状态, 如果服务端还有消息给客户端，那么就要循环处理
        bootstrap.group(group)
                  // 设置客户端通道的实现类(反射)
                 .channel(NioSocketChannel.class)
                  //客户端也需要handler来处理。
                 .handler(new ChannelInitializer<SocketChannel>() {
                     @Override
                     protected void initChannel(SocketChannel ch) throws Exception {
                         //加入自己的处理器
                         ch.pipeline().addLast(new NettyClientHandler());
                     }
                 });
        System.out.println("客户端 ok..");

        //启动客户端去连接服务器端
        //关于 ChannelFuture 要分析，涉及到 netty 的异步模型
        // 使用sync让这个方法不 阻塞在这里
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8879).sync();

        //给关闭通道进行监听
        //关闭通道不是马上关闭，而是在关闭通道事件发生后，才去关闭并且进行监听。
        channelFuture.channel().closeFuture().sync();
        }finally {
           //关闭
           group.shutdownGracefully();
        }
    }
}

package com.learn.simple.netty.inoroutboundhandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 使用自定义的编码器和解码器来说明 Netty 的 handler 调用机制 --服务端
 * @ClassName: MyInServer
 * @Description: 使用自定义的编码器和解码器来说明 Netty 的 handler 调用机制
 * @Author: lin
 * @Date: 2020/10/21 13:43
 * History:
 * @<version> 1.0
 */
public class MyInServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(workerGroup, workerGroup)
                     .channel(NioServerSocketChannel.class)
                    //使用自定义的ChannelInitializer
                     .childHandler(new MyServerInitializer());

            ChannelFuture channelFuture = serverBootstrap.bind(8879).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
           bossGroup.shutdownGracefully();
           bossGroup.shutdownGracefully();
        }
    }
}

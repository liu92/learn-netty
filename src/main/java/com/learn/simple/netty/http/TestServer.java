package com.learn.simple.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Netty 做 Http 服务开发，并且理解 Handler 实例和客户端及其请求的关系.
 * @ClassName: TestServer
 * @Description: 使用 Netty来进行http 示例的开发
 * @Author: lin
 * @Date: 2020/10/19 8:14
 * History:
 * @<version> 1.0
 */
public class TestServer {
    public static void main(String[] args)throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(workerGroup, workerGroup)
                           .channel(NioServerSocketChannel.class)
                          //这里将原来的 写法，全部提出来单独些，这样方便维护
                           .childHandler(new TestServerInitializer());
            ChannelFuture channelFuture = serverBootstrap.bind(7778).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}

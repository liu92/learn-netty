package com.learn.simple.netty.protocoltcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Tcp 粘包和拆包 示例
 * @ClassName: MyProtocolTcpServer
 * @Description: Tcp 粘包和拆包 示例 --服务端
 * @Author: lin
 * @Date: 2020/10/21 13:43
 * History:
 * @<version> 1.0
 */
public class MyProtocolTcpServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(workerGroup, workerGroup)
                     .channel(NioServerSocketChannel.class)
                    //使用自定义的ChannelInitializer
                     .childHandler(new MyProtocolTcpServerInitializer() );

            ChannelFuture channelFuture = serverBootstrap.bind(8879).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
           bossGroup.shutdownGracefully();
           bossGroup.shutdownGracefully();
        }
    }
}

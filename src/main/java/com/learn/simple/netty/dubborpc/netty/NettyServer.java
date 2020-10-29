package com.learn.simple.netty.dubborpc.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @ClassName: NettyServer
 * @Description: 服务端 NettyServer
 * @Author: lin
 * @Date: 2020/10/26 14:16
 * History:
 * @<version> 1.0
 */
public class NettyServer {

    public static void startServer(String hostName, int port) throws Exception {
        startServer0(hostName, port);
    };

    /**
     * 初始NettyServer的初始化和启动
     * @param hostName
     * @param port
     */
    public static void startServer0(String hostName, int port) throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //这里要接收来值服务消费者的请求，所以要进行 解码和编码操作
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());

                            //业务处理器
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            //启动服务
            ChannelFuture channelFuture = serverBootstrap.bind(hostName, port).sync();
            System.out.println("服务提供方开始提供服务......");
            channelFuture.channel().closeFuture().sync();

        }finally{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}

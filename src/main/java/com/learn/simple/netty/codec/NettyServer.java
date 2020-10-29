package com.learn.simple.netty.codec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

/**
 *  Netty入门实例 ---- 服务端
 * @ClassName: NettyServer
 * @Description: Netty入门实例
 * @Author: lin
 * @Date: 2020/10/16 8:59
 * History:
 * @<version> 1.0
 */
public class NettyServer {
    public static void main(String[] args)throws Exception {


        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try {


        //4、创建服务器端启动的对象，然后去配置参数
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup)
                 //使用 NioServerSocketChannel 作为服务器的通道实现
                 //针对bossGroup，使用的是NioServerSocketChannel，针对workerGroup使用的是SocketChannel
                 .channel(NioServerSocketChannel.class)
                 //设置线程队列得到连接个数
                 .option(ChannelOption.SO_BACKLOG, 128)
                 //设置整个连接时活动状态
                 .childOption(ChannelOption.SO_KEEPALIVE, true)

                 .childHandler(new ChannelInitializer<SocketChannel>() {
                     // 操作整个过程，底层使用的是SocketChannel

                     //这里完成一个任务，任务是 向这个workerGroup关联的pipeline里面增加
                     //一个handler ,也就是 给 pipeline 设置处理器
                     @Override
                     protected void initChannel(SocketChannel ch) throws Exception {

                         System.out.println("客户端SocketChannel hashcode=" + ch.hashCode());

                         ChannelPipeline pipeline = ch.pipeline();

                         //客户端在进行编码后，服务需要解码器进行解码
                         // 并且必须要指定对那种对象进行解码。
                         pipeline.addLast("decoder", new ProtobufDecoder(StudentPoJo.Student.getDefaultInstance()));

                         pipeline.addLast( new NettyServerHandler());

                     }
                 });// 很关键，给我们的 workerGroup 的 EventLoop 对应的管道设置处理器

        System.out.println(".....服务器 is ready...");

        //绑定一个端口并且同步, 生成了一个 ChannelFuture 对象
        //这里就相当于 启动服务器(并绑定端口)
        ChannelFuture channelFuture = bootstrap.bind(8879).sync();

        channelFuture.addListener((ChannelFutureListener) future -> {
         if(future.isSuccess()) {
             System.out.println("监听端口 8879 成功");
         }else {
             System.out.println("监听端口 8879 失败");
         }
        });


        channelFuture.channel().closeFuture().sync();
        }finally {
            //这里没有捕获异常，是因为 将异常抛出去了
            //如果再服务器端出现了异常，那么就要关闭。
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

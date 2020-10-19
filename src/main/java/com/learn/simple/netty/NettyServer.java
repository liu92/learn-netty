package com.learn.simple.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

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

        //创建BossGroup 和 WorkerGroup
        //说明：
        //1、创建两个线程组 bossGroup和WorkerGroup
        //2、bossGroup只处理连接请求，真正的和客户端进行业务处理的，会交给WorkerGroup来完成
        //3、这两个都是无限循环。
        //4. bossGroup 和 workerGroup 含有的子线程(NioEventLoop)的个数
        // 默认实际 cpu 核数 * 2
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try {


        //4、创建服务器端启动的对象，然后去配置参数
        ServerBootstrap bootstrap = new ServerBootstrap();

        //5、使用链式编程来设置配置参数。

        //设置两个线程组
        // 注意： bossGroup 和 workerGroup都是服务端 线程，它们两个处理的事情不同
        bootstrap.group(bossGroup, workerGroup)
                 //使用 NioServerSocketChannel 作为服务器的通道实现
                 //针对bossGroup，使用的是NioServerSocketChannel，针对workerGroup使用的是SocketChannel
                 .channel(NioServerSocketChannel.class)
                 //设置线程队列得到连接个数
                 .option(ChannelOption.SO_BACKLOG, 128)
                 //设置整个连接时活动状态
                 .childOption(ChannelOption.SO_KEEPALIVE, true)
                 // 对于handler和childHandler区别：handler对应bossGroup, childHandler对应workerGroup
                 // 也就是说，如果要给bossGroup加入handler，那么用的就是handler，如果要在workerGroup中加入
                 // handler，用的就是childHandler。
//                 .handler(null)
                 //创建一个通过初始化对象(匿名对象)，
                 .childHandler(new ChannelInitializer<SocketChannel>() {
                     // 操作整个过程，底层使用的是SocketChannel

                     //这里完成一个任务，任务是 向这个workerGroup关联的pipeline里面增加
                     //一个handler ,也就是 给 pipeline 设置处理器
                     @Override
                     protected void initChannel(SocketChannel ch) throws Exception {
                         // 方案3：可以将SocketChannel 放到一个集合中进行管理，再推送消息时，可以将业务加入到各个channel
                         // 对应的 NioEventLoop的 taskQueue 或者scheduleTaskQueue中
                         System.out.println("客户端SocketChannel hashcode=" + ch.hashCode());

                         //处理器既可以是netty提供的，也可以是自定义的， 然后接收来自客户的消息
                         //addList，将管道最后添加一个处理器
                         // 接收客户端消息和发送消息到客户端，这种事情交由channel关联的pipeline的一个handler来处理。
                         //这里使用自定义的handler来接收客户端的消息，并且将消息返回给客户端
                         ch.pipeline().addLast( new NettyServerHandler());

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

        //对关闭通道进行监听， 当有关闭通道的消息或者事件时才会进行一个处理，而不是立即去关闭
        channelFuture.channel().closeFuture().sync();
        }finally {
            //这里没有捕获异常，是因为 将异常抛出去了
            //如果再服务器端出现了异常，那么就要关闭。
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

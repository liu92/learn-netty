package com.learn.simple.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName: MyServer
 * @Description: 使用netty来编写一个心跳检测案例 ----服务端
 * @Author: lin
 * @Date: 2020/10/20 10:48
 * History:
 * @<version> 1.0
 */
public class MyServer {
    public static void main(String[] args) throws Exception {
        //创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(workerGroup, workerGroup)
                 .channel(NioServerSocketChannel.class)
                            //在bossGroup增加一个日志处理器
                 .handler(new LoggingHandler(LogLevel.INFO))
                 .childHandler(new ChannelInitializer<SocketChannel>() {
                   @Override
                   protected void initChannel(SocketChannel ch) throws Exception {
                       ChannelPipeline pipeline = ch.pipeline();

                       //加入一个netty提供的 IdleStateHandler
                       /**
                        * 说明：
                        * 1、IdleStateHandler是netty提供的处理空闲状态的处理器
                        * 2、long readerIdleTime: 表示多长时间没有读(就是server没有读取客户端发送的消息),
                        *                         就会发送一个心跳检测包检测是否连接
                        * 3、long writerIdleTime: 表示多长时间没有写, 就会发送一个心跳检测包检测是否连接
                        * 4、long allIdleTime： 表示多长时间没有读写(两者都没有发生),
                        *                       就会发送一个心跳检测包检测是否连接
                        * 5、文档说明
                        *  Triggers an {@link IdleStateEvent} when a {@link Channel} has not performed
                        *  read, write, or both operation for a while.
                        *  当这个连接没有执行 读、写、或者两者都没有 那么就会触发一个 IdleStateEvent事件
                        *  为什么还要提供一个心跳检测呢？在上面的群聊中有handlerRemove不是可以 检测到客户端断开吗？
                        *  这里要注意：因为在有些时候 服务器端和客户端 连接断开之后，server是无法感知的 连接断开。
                        *
                        *  6. 当 IdleStateEvent 触发后 , 就会传递给管道 的下一个 handler去处理
                        *  通过调用(触发)下一个 handler 的 userEventTiggered ,
                        *  在该方法中去处理 IdleStateEvent(读 空闲，写空闲，读写空闲)
                        *
                        *  如果3秒中没有读，那么就会触发读空闲
                        *  如果5秒中没有写，那么就会触发写空闲
                        *  如果7秒中没有写和读，那么就会触发读写空闲
                        */
                       pipeline.addLast(new IdleStateHandler(1000,10,1000, TimeUnit.SECONDS));

                       //加入一个对空闲检测进一步处理的 handler(自定义)
                       pipeline.addLast(new MyServerHandler());
                   }
                   });
            ChannelFuture channelFuture = serverBootstrap.bind(7000).sync();
            channelFuture.channel().closeFuture().sync();

        }finally {
          bossGroup.shutdownGracefully();
          workerGroup.shutdownGracefully();
        }

    }
}

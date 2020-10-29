package com.learn.simple.netty.groupchant;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @ClassName: GroupChatServer
 * @Description: 使用netty 简单实现群聊系统 ---服务端
 * @Author: lin
 * @Date: 2020/10/19 22:53
 * History:
 * @<version> 1.0
 */
public class GroupChatServer {

    /**
     * 监听端口
     */
    private  int  port;

    public  GroupChatServer(int port ){
        this.port = port;
    }

    public  void run() throws Exception {
        //创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //8个线程
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try {


        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(workerGroup, workerGroup)
                       .channel(NioServerSocketChannel.class)
                       .option(ChannelOption.SO_BACKLOG, 128)
                       .childOption(ChannelOption.SO_KEEPALIVE, true)
                       .childHandler(new ChannelInitializer<SocketChannel>() {
                           @Override
                           protected void initChannel(SocketChannel ch) throws Exception {
                              //获取到pipeline
                               ChannelPipeline pipeline = ch.pipeline();

                               //向pipeline中加入解码器
                               pipeline.addLast("decoder", new StringDecoder());
                               //向pipeline中加入编码器
                               pipeline.addLast("encoder", new StringEncoder());
                               // TODO 向pipeline中加入自定义的handler
                               pipeline.addLast(new GroupChatServerHandler());

                           }
                       });
            System.out.println("netty 服务器启动");
            //绑定端口，并且做异步处理, 异步绑定服务器；调用sync()方法阻塞等待直到绑定完成。
            //sync阻塞等待任务结束，如果任务失败，将“导致失败的异常”重新抛出来

            // serverBootstrap.bind(port) 这个方法会返回一个 ChannelFuture，bind() 是一个异步方法，
            // 当某个执行线程执行了真正的绑定操作后，那个执行线程一定会标记这个 future 为成功（我们假定 bind 会成功），
            // 然后这里的 sync() 方法（main 线程）就会返回了。
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

            //一旦绑定端口 bind 成功
            //channel.closeFuture() 也会返回一个 ChannelFuture，然后调用了 sync() 方法，
            // 这个 sync() 方法返回的条件是：有其他的线程关闭了 NioServerSocketChannel，
            // 往往是因为需要停掉服务了，然后那个线程会设置 future 的状态（ setSuccess(result) 或
            // setFailure(cause) ），这个 sync() 方法才会返回。
            //监听关闭
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new GroupChatServer(7000).run();
    }

}

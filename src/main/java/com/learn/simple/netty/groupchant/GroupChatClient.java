package com.learn.simple.netty.groupchant;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * @ClassName: GroupChatClient
 * @Description: 使用netty来编写 群聊系统， 客户端
 * @Author: lin
 * @Date: 2020/10/20 9:28
 * History:
 * @<version> 1.0
 */
public class GroupChatClient {

    /**
     * 属性，主机地址
     */
    private final String host;
    /**
     * 端口号
     */
    private final int port;

    public GroupChatClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    /**
     * 定义一个方法，不同的往服务端发送消息
     */
    public void run() throws Exception {
        EventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //得到pipeline
                            ChannelPipeline pipeline = ch.pipeline();
                            //加入相关handler
                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("encoder", new StringEncoder());

                            //加入自定义的 handler
                            pipeline.addLast(new GroupChatClientHandler());
                        }
                    });
            //启动， 异步处理，不然会阻塞在这里
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            //得到 channel， 提示一下
            Channel channel = channelFuture.channel();
            System.out.println("-------" + channel.localAddress()+ "--------");

            //客户需要输入信息，因此需要创建一个扫描器
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()){
                String msg = scanner.nextLine();
                //通过 channel 将输入的消息 发送到服务器端
                channel.writeAndFlush(msg + " \n");
            }

        }finally {
            nioEventLoopGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new GroupChatClient("127.0.0.1", 7000).run();
    }
}

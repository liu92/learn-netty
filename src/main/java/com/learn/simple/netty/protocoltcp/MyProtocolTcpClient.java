package com.learn.simple.netty.protocoltcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Tcp 粘包和拆包 示例
 * @ClassName: MyInClient
 * @Description: Tcp 粘包和拆包 示例 -----客户端
 * @Author: lin
 * @Date: 2020/10/21 14:02
 * History:
 * @<version> 1.0
 */
public class MyProtocolTcpClient {
    public static void main(String[] args) throws InterruptedException {
        //客户端只需要一个group
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    //自定义一个初始化对象
                    // 测试的是出站，也就是从客户端到服务端，那么这个handler就是从尾部---到----头部的方式来调用
                    .handler(new MyProtocolTcpClientInitializer());

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1",
                    8879).sync();
            channelFuture.channel().closeFuture().sync();

        }finally {
            group.shutdownGracefully();
        }
    }
}

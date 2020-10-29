package com.learn.simple.netty.websocket;

import com.learn.simple.netty.heartbeat.MyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName: WebSocketMyServer
 * @Description: Netty通过WebSocket编写服务器和客户端长连接
 * @Author: lin
 * @Date: 2020/10/20 14:02
 * History:
 * @<version> 1.0
 */
public class WebSocketMyServer {
    public static void main(String[] args)throws Exception {
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
                            //因为基于http协议，所以使用http的编码和解码器
                            pipeline.addLast( new HttpServerCodec());
                            //因为是基于http协议的，这个过程是以块方式写的，所以要添加ChunkedWriterHandler处理器
                            pipeline.addLast(new ChunkedWriteHandler());

                            /**
                             * 说明：
                             * 1、http数据在传输过程中是分段的，所以HttpObjectAggregator 就是将多个段聚会起来。
                             * 2、这就是为什么，当浏览器发送大量数据时，就会发出多次http请求
                             */
                            pipeline.addLast(new HttpObjectAggregator(8192));

                            /**
                             * 说明：
                             * 1、对应 websocket ，它的数据是以 帧(frame) 形式传递
                             * 2、可以看到 WebSocketFrame 下面有六个子类
                             * 3、浏览器请求时 ws://localhost:7000/hello ，表示请求的 uri，
                             *          浏览器在请求时要和 这里设置websocketPath对应。
                             * 4、WebSocketServerProtocolHandler 核心功能是将 http 协议升级为 ws 协议 ,
                             *     保持长连接
                             * 5、是通过一个 状态码 101
                             *
                             * 这个WebSocketServerProtocolHandler有两个作用：
                             *  1、识别请求的资源
                             *  2、核心功能是将http协议升级为ws协议，才能保持长连接
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));

                            //自定义 handler，处理业务逻辑。经过了上面的handler处理才到 这个自定义的handler处理。
                            pipeline.addLast(new MyWebSocketFrameHandler());
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

package com.learn.simple.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * 自定义服务端处理器，来处理客户端的消息，并且返回消息给客户端
 *
 * 1. 我们自定义一个 Handler 需要继承 netty 规定好的某个 HandlerAdapter(规范)
 * 2. 这时我们自定义一个 Handler , 才能称为一个 handler
 *
 * @ClassName: NettyServerHandler
 * @Description: 自定义服务端处理器，来处理客户端的消息，并且返回消息给客户端
 *      ChannelInboundHandlerAdapter 一个入栈Handler适配器
 * @Author: lin
 * @Date: 2020/10/16 9:49
 * History:
 * @<version> 1.0
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * //读取数据实际(这里我们可以读取客户端发送的消息)，
     *   通过pipeline的调用会把消息传输到pipeline关联的handler上
     * 1. ChannelHandlerContext ctx:上下文对象, 含有 管道 pipeline(业务逻辑处理) , 通道 channel(注重数据的读写), 地址等等
     * 2. Object msg: 就是客户端发送的数据 默认 Object
     * @param ctx 上下文对象
     * @param msg 客户端发送的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //比如这里我们有一个非常耗时长的业务-> 异步执行 -> 提交该 channel 对应的
        // NIOEventLoop 的 taskQueue 中

//        Thread.sleep(10 * 1000);
//        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 2 ", CharsetUtil.UTF_8));
//


        //任务处理耗时，解决方案 1 用户程序自定义的普通任务

//        ctx.channel().eventLoop().execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    System.out.println("线程名称： " + Thread.currentThread().getName());
//                    Thread.sleep(10 * 1000);
//                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 2 ",
//                            CharsetUtil.UTF_8));
//                } catch (InterruptedException e) {
//                    System.out.println("发生异常" + e.getMessage());
//                }
//            }
//        });
//
//        ctx.channel().eventLoop().execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    System.out.println("线程名称： " + Thread.currentThread().getName());
//                    Thread.sleep(20 * 1000);
//                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 3 ",
//                            CharsetUtil.UTF_8));
//                } catch (InterruptedException e) {
//                    System.out.println("发生异常" + e.getMessage());
//                }
//            }
//        });
//
//
//        ctx.channel().eventLoop().schedule(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    System.out.println("线程名称： " + Thread.currentThread().getName());
//                    Thread.sleep(5 * 1000);
//                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 4 ",
//                            CharsetUtil.UTF_8));
//                } catch (InterruptedException e) {
//                    System.out.println("发生异常" + e.getMessage());
//                }
//            }
//            // 延迟时间5s。
//        }, 5, TimeUnit.SECONDS);
//
//
//        System.out.println("go on ...");

        System.out.println("服务器读取线程 " + Thread.currentThread().getName() + " channel=" + ctx.channel());
        System.out.println("server ctx =" + ctx);
        System.out.println("看看 channel 和 pipeline 的关系");
        Channel channel = ctx.channel();
        //本质是一个双向链接, 出站入站
        ChannelPipeline pipeline = ctx.pipeline();
         //在Nio中 拿到数据后将其放到缓冲里。
        // 所以这里拿到数据msg后，将 msg 转成一个 ByteBuf进行处理，更好些。
        // ByteBuf 是 Netty 提供的，不是 NIO 的 ByteBuffer。 netty是对Nio的一次再包装
        ByteBuf byteBuf =(ByteBuf) msg;
        System.out.println("客户端发送消息是:" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址:" + channel.remoteAddress());
    }


    /**
     * 数据读取完毕后，返回给客户端回复一个消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // writeAndFlush 方法将数据写入到缓存，并刷新 ，writeAndFlush= write +flush
        // 如果使用write那么就是将数据写缓冲区，但是没有刷新到通道中去。
        // 一般讲，我们对这个发送的数据进行编码。 设置编码并放到 buffer中去
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵 1", CharsetUtil.UTF_8));
    }

    /**
     * 处理异常, 如果发生异常 一般是需要关闭通道
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //都可以
//        ctx.channel().closeFuture();
        ctx.close();
    }
}

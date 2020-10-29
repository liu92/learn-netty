package com.learn.simple.netty.dubborpc.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

/**
 * 服务消费者方 处理器 ， 这里要继承ChannelInboundHandlerAdapter
 *  并且还要实现Callable接口，为什么要实现这个接口呢？ 这是因为在 调用服务提供者api时，是通过call方法来调用
 *
 * @ClassName: NettyClientHandler
 * @Description: 服务消费者方 处理器
 * @Author: lin
 * @Date: 2020/10/26 14:45
 * History:
 * @<version> 1.0
 */
public class NettyClientHandler  extends ChannelInboundHandlerAdapter implements Callable {
    /**
     * context上下文。
     */
    private ChannelHandlerContext context;

    /**
     * 调用后返回的结果
     */
    private String result;

    /**
     * 客户端调用服务端 方法时传入的参数
     */
    private String param;

    /**
     *  1、这个方法是第一个被调用的
     * 与服务器的连接创建后，就会被调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("第一步：channelActive 被调用");
        //在连接到服务后，获取到这个context上下文，在其它的方法也要用到当前的context
        context = ctx;
    }

    /**
     * 收到服务器的数据后，调用方法
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("第四步：channelRead 被调用");
        result = msg.toString();
        //收到服务器数据后，唤醒等待线程
        notify();
        System.out.println("channelRead 线程=" + Thread.currentThread().getName());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       ctx.close();
    }

    /**
     *  这个call方法是通过 反射机制来调用的，在被代理对象调用后 消息没有 立即回来，
     *   所以在这个方法里面让其" wait()" 方法等待。
     *
     * 客户端调用服务端 API,其实是 调用call方法，将消息发送给服务器，
     * 然后服务响应后，等待服务器返回结果。 而基于Netty 服务端响应的结果是
     *  给ChannelRead方法， 所以在ChannelRead中  通过 "notify()" 方法 唤醒线程 再继续往下执行其它逻辑,
     *  然后将ChannelRead方法获取的结果 返回给代理对象。
     *  大至流程：
     *  被代理对象调用,发送数据给服务器----->wait()------->等待被唤醒(channelRead)----->返回结果
     *  -------> 继续执行 将返回结果 返回个代理对象
     *
     * @return
     * @throws Exception
     */
    @Override
    public synchronized Object call() throws Exception {
        System.out.println("第三步：call1 被调用");
        System.out.println("等待线程=" + Thread.currentThread().getName());
        context.writeAndFlush(param);
        //等待channelRead方法来 唤醒该方法
        wait();
        System.out.println("第五步：唤醒线程后call2 被调用");
        System.out.println("唤醒等待线程=" + Thread.currentThread().getName());
        //result 是服务方提供方 返回的结果
        return result;
    }


    /**
     * 2、设置参数
     * @param param
     */
    void setParam(String param){
        System.out.println("第二步：setParam 被调用");
        this.param = param;
    }
}

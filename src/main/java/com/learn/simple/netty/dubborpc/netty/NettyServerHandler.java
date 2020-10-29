package com.learn.simple.netty.dubborpc.netty;

import com.learn.simple.netty.dubborpc.provider.HelloServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 服务器端 处理业务逻辑handler
 * @ClassName: NettyServerHandler
 * @Description: 自定义处理器
 * @Author: lin
 * @Date: 2020/10/26 14:26
 * History:
 * @<version> 1.0
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 获取客户端发送过来的消息，并调用 API服务
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("客户端消息=" + msg);
        //根据 自己定义的 协议规则来处理客户端发送过来的信息
        //客户端在调用服务器api 时，需要指定一个协议来处理，
        // 比如 每次发送消息过来 都必须以某个字符串开头  例如 ："#HelloService#hello#"  后面的才是消息
        String contentMsg = "#HelloService#hello#";
        if(msg.toString().startsWith(contentMsg)){

            //这里取调用服务端API
            //这里+1, 是因为找到最后一个#，然后从这个位置的下一个开始才是 消息
            String result = new HelloServiceImpl().hello(msg.toString().substring(msg.toString().lastIndexOf("#") + 1));

            //在调用服务端API后将 结果响应回 服务调用方去
            ctx.writeAndFlush(result);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       ctx.close();
    }
}

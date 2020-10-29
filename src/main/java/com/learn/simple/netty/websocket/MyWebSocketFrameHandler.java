package com.learn.simple.netty.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 这里是服务器自定义的 handler
 * @ClassName: MyWebSocketFrameHandler
 * @Description: 这里是服务器自定义的 handler
 *   这里继承SimpleChannelInboundHandler是因为有数据的交互，并且数据类型是TextWebSocketFrame
 *   那么表示一个文本帧(frame), 浏览器和服务器在进行交互的时候以这种数据类型来进行交互
 * @Author: lin
 * @Date: 2020/10/20 14:27
 * History:
 * @<version> 1.0
 */
public class MyWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("服务器收到消息 " + msg.text());

        //在服务端接收到消息后， 回复消息给客户端
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间:" + LocalDateTime.now() + " " +
                msg.text()));
    }

    /**
     * 当web客户端连接后，触发方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //id表示唯一的值，LongText是唯一的；而shortText不是唯一的
        System.out.println("handlerAdded 被调用" + ctx.channel().id().asLongText());
        System.out.println("handlerAdded 被调用" + ctx.channel().id().asShortText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved 被调用" + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常发生 "+cause.getMessage());
        ctx.close();
    }
}

package com.learn.simple.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * 服务端处理器
 * @ClassName: TestHttpServerHandler
 * @Description: 服务端处理器
 *               说明
 *               1： SimpleChannelInboundHandler 是 ChannelInboundHandler的一个子类
 *               2：HttpObject 是客户端和服务器端相互通讯的数据被封装成 HttpObject , 所以重写的方法中 msg对应成了httpObject
 *               这里在继承的时候 指定了客户端和服务器端在进行处理的时候的 数据类型
 * @Author: lin
 * @Date: 2020/10/19 8:16
 * History:
 * @<version> 1.0
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    /**
     *  当有读取事件发生时，就会触发该方法
     *  channelRead0 读取客户端数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        System.out.println("对应的channel=" + ctx.channel() + " pipeline=" + ctx.pipeline() +
                "通过pipeline获取channel" +ctx.pipeline().channel());

        System.out.println("当前的ctx的handler=" + ctx.handler());

        //判断msg是不是 httpRequest请求
        if(msg instanceof HttpRequest){
            System.out.println("ctx 类型是=" + ctx.getClass());

            System.out.println("pipeline hashcode" + ctx.pipeline().hashCode() + " TestHttpServerHandler hash=" + this.hashCode());

            System.out.println("msg 类型" + msg.getClass());
            System.out.println("客户端地址" + ctx.channel().remoteAddress());

            //这里获取到httpRequest请求
            HttpRequest httpRequest = (HttpRequest) msg;
            //获取uri
            URI uri = new URI(httpRequest.uri());
            //获取到uri后 判断uri中的路径中是否有图标路径
            String  icoStr  = "/favicon.ico";
            if(icoStr.equals(uri.getPath())){
                System.out.println("请求了 favicon.ico, 不做响应");
                return;
            }

            //浏览器向服务器请求，发送了一个http请求，这里就可以进行回复信息给浏览器了
            //那么回复信息给浏览器，这个回复信息要做成 http协议的才可以回复。
            ByteBuf content =Unpooled.copiedBuffer("hello, 我是服务器", CharsetUtil.UTF_8);

            //构造一个http的响应，即HttpResponse。
            // 使用默认的http响应，并且指定协议版本和状态
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, content);

            //设置返回头信息，以及长度
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            //将构建好的response返回
            ctx.writeAndFlush(response);

        }
    }
}

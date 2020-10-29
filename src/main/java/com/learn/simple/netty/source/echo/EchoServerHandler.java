/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.learn.simple.netty.source.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    static final EventExecutorGroup group = new DefaultEventExecutorGroup(16);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
        System.out.println("EchoServerHandler 的线程是" + Thread.currentThread().getName());

//        ctx.channel().eventLoop().execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    System.out.println("EchoServerHandler execute线程名称： " + Thread.currentThread().getName());
//                    Thread.sleep(5 * 1000);
//                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 1 ",
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
//                    System.out.println("EchoServerHandler execute线程名称： " + Thread.currentThread().getName());
//                    Thread.sleep(5 * 1000);
//                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 2 ",
//                            CharsetUtil.UTF_8));
//                } catch (InterruptedException e) {
//                    System.out.println("发生异常" + e.getMessage());
//                }
//            }
//        });

        // 在handler中 加入线程池
//        group.submit(new Callable<Object>() {
//            @Override
//            public Object call() throws Exception {
//                ByteBuf buf = (ByteBuf) msg;
//                byte[] req = new byte[buf.readableBytes()];
//                buf.readBytes(req);
//                String body = new String(req, StandardCharsets.UTF_8);
//                Thread.sleep(10*1000);
//                System.out.println("group submit 的 call 线程是=" + Thread.currentThread().getName());
//                ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 1 ",
//                            CharsetUtil.UTF_8));
//
//                return null;
//            }
//        });
//
//
//        group.submit(new Callable<Object>() {
//            @Override
//            public Object call() throws Exception {
//                ByteBuf buf = (ByteBuf) msg;
//                byte[] req = new byte[buf.readableBytes()];
//                buf.readBytes(req);
//                String body = new String(req, StandardCharsets.UTF_8);
//                Thread.sleep(10*1000);
//                System.out.println("group submit 的 call 线程是=" + Thread.currentThread().getName());
//                ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 2 ",
//                        CharsetUtil.UTF_8));
//
//                return null;
//            }
//        });
//
//        group.submit(new Callable<Object>() {
//            @Override
//            public Object call() throws Exception {
//                ByteBuf buf = (ByteBuf) msg;
//                byte[] req = new byte[buf.readableBytes()];
//                buf.readBytes(req);
//                String body = new String(req, StandardCharsets.UTF_8);
//                Thread.sleep(10*1000);
//                System.out.println("group submit 的 call 线程是=" + Thread.currentThread().getName());
//                ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 3 ",
//                        CharsetUtil.UTF_8));
//
//                return null;
//            }
//        });

        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, StandardCharsets.UTF_8);
        Thread.sleep(10*1000);
        System.out.println("普通调用的 线程是=" + Thread.currentThread().getName());
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 1 ",
                CharsetUtil.UTF_8));

        System.out.println("go on....");

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}

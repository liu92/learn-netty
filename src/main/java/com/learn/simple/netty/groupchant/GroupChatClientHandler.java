package com.learn.simple.netty.groupchant;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 客户端自定义handler
 * @ClassName: GroupChatClientHandler
 * @Description: 客户端自定义handler
 * @Author: lin
 * @Date: 2020/10/20 9:43
 * History:
 * @<version> 1.0
 */
public class GroupChatClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //这里 直接输出信息就可以了
        //把 从服务端拿到的消息显示处理就可以了
        System.out.println(msg.trim());
    }
}

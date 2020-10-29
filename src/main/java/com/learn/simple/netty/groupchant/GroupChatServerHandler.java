package com.learn.simple.netty.groupchant;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: GroupChatServerHandler
 * @Description: 服务器端 业务处理handler
 * @Author: lin
 * @Date: 2020/10/20 8:28
 * History:
 * @<version> 1.0
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * handler是每一个客户端都会有自己的独立的handler
     * 因此，这个线程组是所有的handler 共享的， 所有定义为static
     *
     *  定义一个channel组，管理所有的channel,
     *  GlobalEventExecutor.INSTANCE是一个全局的事件执行器， 是一个单例
     *  这个执行器就是用来帮助执行 channelGroup
     */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //也可以使用这种方式，在调用发送的时候遍历这个list。不过这种方式没有 channelGroup.writeAndFlush()方便。
    // private static List<Channel> channels = new ArrayList<>();

    /**
     * 如果要点对点的发送消息，那么使用map来存放数据, 让后用户登录，将其id获取到
     * channelMap.put("用户id", channel);这样来设置
     */
//    private static Map<String, Channel> channelMap = new HashMap<>();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String localDate = formatter.format(LocalDateTime.now());;


    /**
     * 1、
     * 这个方法表示 连接建立，一旦连接后，第一个执行
     * 将当前 channel 加入到 channelGroup
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //将该客户加入聊天的信息推送给其它在线的客户端
        //在加入到channelGroup之前，将某个客户端加进去的消息推送给所有客户端
        /**该方法会将 channelGroup 中所有的 channel 遍历，并发送 消息， 我们不需要自己遍历 */
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() +" 加入聊天"+ localDate+" \n");
        channelGroup.add(channel);

    }


    /**
     * 2、表示channel处于一个活动状态，提示某某客户端上线
     * 这个方法用来 提示客户端上线
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //这个只需要在服务端打印下就可以了，没有必要把信息再发送一遍。因为上面的handlerAdded 已经发送了消息
        System.out.println(ctx.channel().remoteAddress() + " 上线了~");
    }

    /**
     * 4、断开连接会触发这个方法, 将 某某客户离开信息 推送给当前在线的客户
     *   这个方法执行了 就会导致当前channel从 channelGroup中 移除掉了。所以不需要再进行移除操作
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]" +channel.remoteAddress() +" 离开了\n");
        System.out.println("channelGroup size" + channelGroup.size());
    }

    /**
     * 3、表示channel处于非活动状态， 就会触发这个方法
     *   提示xx 离线了
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 离线了~");
    }

    /**
     * 5、消息转发， 真正的读取数据，将读取到的数据 转发给其它客户端
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //获取当前channel
        Channel channel = ctx.channel();
        //这时我们遍历 channelGroup, 根据不同的情况，回送不同的消息
        channelGroup.forEach(ch ->{
            //如果当前的channel和 channelGroup中的channel不相同，那么就转发消息
            if(!channel.equals(ch)){
                // 当前的channel 转发消息给 ch。
                ch.writeAndFlush("[客户]" + channel.remoteAddress() + " 发送了消息" + msg + "\n");
            }else {
                //这个是给自己发送消息
                ch.writeAndFlush("[自己]发送了消息" + msg + "\n");
            }
        });
        System.out.println("form 客户端" + channel.remoteAddress() +":" + msg);
    }

    /**
     * 发生异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //关闭通道
        ctx.close();
    }
}

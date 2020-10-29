package com.learn.simple.netty.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @ClassName: MyServerHandler
 * @Description: netty 心跳检测 ----自定义handler
 * @Author: lin
 * @Date: 2020/10/20 11:14
 * History:
 * @<version> 1.0
 */
public class MyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 这个方法用来 监听 空闲事件
     * @param ctx 上下文
     * @param evt 事件
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        //判断这个事件是不是 IdleStateEvent
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            String eventType = null;
            switch (event.state()){
                case READER_IDLE:
                     eventType = "读空闲";
                     break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
                default:
                    break;
            }
            System.out.println(ctx.channel().remoteAddress() + "---超时时间---" + eventType);
            System.out.println("服务器做相应处理");
            //如果发生空闲，直接关闭通过
            ctx.channel().close();
        }
    }
}

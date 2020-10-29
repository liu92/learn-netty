package com.learn.simple.netty.codec2;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

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
//public class NettyServerHandler extends ChannelInboundHandlerAdapter {
public class NettyServerHandler extends SimpleChannelInboundHandler<MyDataInfo.MyMessage>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyDataInfo.MyMessage msg) throws Exception {

        //根据dataType，来显示不同的信息
        MyDataInfo.MyMessage.DataType dataType = msg.getDataType();
        if(dataType == MyDataInfo.MyMessage.DataType.StudentType){
            MyDataInfo.Student student = msg.getStudent();
            System.out.println("学生id=" + student.getId() + " 学生名字=" + student.getName());
        }else if(dataType == MyDataInfo.MyMessage.DataType.WorkerType){
            MyDataInfo.Worker worker = msg.getWorker();
            System.out.println("工人的名字=" + worker.getName() + " 年龄=" + worker.getAge());
        }else {
            System.out.println("传输的类型不正确");
        }

    }

//    /**
//     * //读取数据实际(这里我们可以读取客户端发送的消息)，
//     *   通过pipeline的调用会把消息传输到pipeline关联的handler上
//     * 1. ChannelHandlerContext ctx:上下文对象, 含有 管道 pipeline(业务逻辑处理) , 通道 channel(注重数据的读写), 地址等等
//     * 2. Object msg: 就是客户端发送的数据 默认 Object
//     * @param ctx 上下文对象
//     * @param msg 客户端发送的数据
//     * @throws Exception
//     */
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//
//        //读取从客户端发送的StudentPoJo.Student.getDefaultInstance()
//        //先转换类型
//        StudentPoJo.Student student = (StudentPoJo.Student) msg;
//        System.out.println("客户端发送的数据 id=" + student.getId() + " 名字" + student.getName());
//    }



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

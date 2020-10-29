package com.learn.simple.netty.dubborpc.netty;

import com.learn.simple.DefaultThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.Proxy;
import java.util.concurrent.*;

/**
 * @ClassName: NettyClient
 * @Description: 服务调用方
 * @Author: lin
 * @Date: 2020/10/26 15:18
 * History:
 * @<version> 1.0
 */
public class NettyClient {

    /**
     * 创建线程池
     */
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime
            .getRuntime().availableProcessors());

    /**
     * 定义handler属性
     */
    private static NettyClientHandler client;

    private int count = 0;

    /**
     * 编写方法使用代理模式，获取一个代理对象
     * @param serviceClass 要代理的类
     * @param providerName 定义的协议头
     * @return
     */
    public Object getBean(final Class<?> serviceClass, final String providerName){
        //传入类的加载器, 类的实例
         return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                 //proxy代理对象本身， method方法 ， args传递的参数
                 new Class<?>[] {serviceClass}, (proxy, method, args)->{
                     //{}  这部分代码，客户端每调用一次hello，就会进入该代码
                     System.out.println("(proxy, method, args) 进入...." + (++count) + " 次");

                      if(client == null){
                          //初始化
                          initClient();
                      }

                      //设置 要发送给服务器的信息, 定义的协议头 + 传入的信息
                     // providerName: 协议头;  args[0]:就是客户端调用api hello(String msg)的参数
                     // 比如："#HelloService#hello#" + "你好！"
                     client.setParam(providerName + args[0]);

                      //返回值，就通过 实现Callable接口的 NettyClientHandler处理，来执行call方法
                     return executorService.submit(client).get();
                 });
    }


    /**
     * 初始化客户端
     */
    public static  void initClient(){
        client = new NettyClientHandler();

        //创建
        EventLoopGroup group = new NioEventLoopGroup();


            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                  .channel(NioSocketChannel.class)
                    //不延时
                  .option(ChannelOption.TCP_NODELAY, true)
                  .handler(new ChannelInitializer<SocketChannel>() {
                      @Override
                      protected void initChannel(SocketChannel ch) throws Exception {
                          ChannelPipeline pipeline = ch.pipeline();

                          pipeline.addLast(new StringDecoder());
                          pipeline.addLast(new StringEncoder());
                          pipeline.addLast(client);

                      }
                  });


        try{
            bootstrap.connect("127.0.0.1", 8900).sync();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}

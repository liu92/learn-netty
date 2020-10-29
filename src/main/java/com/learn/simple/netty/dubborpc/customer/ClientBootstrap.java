package com.learn.simple.netty.dubborpc.customer;

import com.learn.simple.netty.dubborpc.netty.NettyClient;
import com.learn.simple.netty.dubborpc.pulicinterface.HelloService;

/**
 * @ClassName: ClientBootstrap
 * @Description:
 * @Author: lin
 * @Date: 2020/10/26 15:45
 * History:
 * @<version> 1.0
 */
public class ClientBootstrap {

    /**
     * 这里定义协议头
     */
    private static final String providerName = "#HelloService#hello#";

    public static void main(String[] args) throws Exception {
        //创建一个消费者
        NettyClient customer = new NettyClient();

        //创建代理对象
        HelloService bean = (HelloService) customer.getBean(HelloService.class, providerName);

        for (;;) {
            Thread.sleep(2 * 1000);
            //通过代理对象调用 服务提供者的方法
            String result = bean.hello("你好，Dubbo rpc~");
            System.out.println("调用返回的结果 res=" + result +"\n");
        }

    }
}

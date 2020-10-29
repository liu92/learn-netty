package com.learn.simple.netty.dubborpc.provider;

import com.learn.simple.netty.dubborpc.pulicinterface.HelloService;

/**
 * @ClassName: HelloServiceImpl
 * @Description: 实现类，服务端提供服务实现类
 * @Author: lin
 * @Date: 2020/10/26 14:08
 * History:
 * @<version> 1.0
 */
public class HelloServiceImpl implements HelloService {
    private static int count = 0;
    /**
     * 当消费方调用该方法时，就返回一个结果
     * @param msg
     * @return
     */
    @Override
    public String hello(String msg) {
        System.out.println("收到客户端消息=" + msg);
        if(msg != null){
            return "你好客户端，我已接收到你的消息 [" +msg +"] 第" + (++count) + " 次";
        }else {
            return "你好客户端，我已接收到你的消息";
        }
    }
}

package com.learn.simple;

import com.learn.simple.netty.dubborpc.netty.NettyClientProxyHandler;
import com.learn.simple.netty.dubborpc.provider.HelloServiceImpl;
import com.learn.simple.netty.dubborpc.pulicinterface.HelloService;
import io.netty.util.NettyRuntime;

/**
 * @ClassName: Test
 * @Description:
 * @Author: lin
 * @Date: 2020/10/16 14:09
 * History:
 * @<version> 1.0
 */
public class Test {
    public static void main(String[] args) {
//        System.out.println("cpu核心数" + NettyRuntime.availableProcessors());
//
//        long minValue = Long.MIN_VALUE;
//        double maxValue = Double.MAX_VALUE;
//        System.out.println("long 最小值=" + minValue);
        HelloService target = new HelloServiceImpl();
        HelloService proxyInstance =(HelloService)new NettyClientProxyHandler(target).getProxyInstance();
        System.out.println("proxyInstance=" + proxyInstance);
    }
}

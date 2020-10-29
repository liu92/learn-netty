package com.learn.simple.netty.dubborpc.pulicinterface;

/**
 * @ClassName: HelloService
 * @Description: 共用接口类，服务提供和服务消费者 都需要的接口类
 * @Author: lin
 * @Date: 2020/10/26 14:07
 * @History:
 * @<version> 1.0
 */
public interface HelloService {

    /**
     * 接口方法
     * @param msg
     * @return
     */
    String hello(String msg);
}

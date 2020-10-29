package com.learn.simple.netty.dubborpc.provider;

import com.learn.simple.netty.dubborpc.netty.NettyServer;

/**
 *  MyServerBootstrap 只会启动一个服务提供者， 就是nettyServer
 * @ClassName: MyServerBootstrap
 * @Description: 服务端启动类
 * @Author: lin
 * @Date: 2020/10/26 14:13
 * History:
 * @<version> 1.0
 */
public class MyServerBootstrap {

    public static void main(String[] args) throws Exception {
        NettyServer.startServer("127.0.0.1", 8900);
    }

}

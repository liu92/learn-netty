package com.learn.simple;

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
        System.out.println("cpu核心数" + NettyRuntime.availableProcessors());
    }
}

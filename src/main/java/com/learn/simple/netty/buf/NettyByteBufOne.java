package com.learn.simple.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @ClassName: NettyButeBufOne
 * @Description: Unpooled 获取 Netty 的数据容器 ByteBuf 的基本使用
 * @Author: lin
 * @Date: 2020/10/19 17:20
 * History:
 * @<version> 1.0
 */
public class NettyByteBufOne {
    public static void main(String[] args) {

        //创建一个ByteBuf
        //说明
        //1. 创建对象，该对象包含一个数组arr，是一个byte[10]，可以存放10个字节的byte数组
        //2. 在Netty的buf中，不需要使用flip进行反转，
        //  因为底层维护了readerIndex和 writerIndex
        //3. 通过readerIndex和 writerIndex 以及capacity，将buffer分成三个区域
        // 0-----readerIndex: 表示已读取的区域
        // readerIndex----writerIndex: 表示可读的区域
        // writerIndex---capacity：表示可写的区域
        ByteBuf byteBuf = Unpooled.buffer(10);

        int count = 10;

        for (int i = 0; i <count ; i++) {
             byteBuf.writeByte(i);
        }

        System.out.println("capacity =:" + byteBuf.capacity());

//        for (int i = 0; i <byteBuf.capacity() ; i++) {
//            //这个方法是根据索引来获取，所以readIndex不会发生变化
//            System.out.println(byteBuf.getByte(i));
//        }

        for (int i = 0; i < byteBuf.capacity() ; i++) {
            //这个方法可以看到readIndex的变化
            System.out.println(byteBuf.readByte());
        }
        System.out.println("执行完毕.....");

    }
}

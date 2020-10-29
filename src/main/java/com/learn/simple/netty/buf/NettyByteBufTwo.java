package com.learn.simple.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName: NettyByteBufTwo
 * @Description:
 * @Author: lin
 * @Date: 2020/10/19 21:09
 * History:
 * @<version> 1.0
 */
public class NettyByteBufTwo {
    public static void main(String[] args) {
        //创建ByteBuf
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello,world!", StandardCharsets.UTF_8);

        //使用相关方法
        if(byteBuf.hasArray()){

            byte[] content = byteBuf.array();

            //将content转换成字符串
            System.out.println(new String(content, StandardCharsets.UTF_8));

            System.out.println("byteBuf=" + byteBuf);
            //数组偏移量 0
            System.out.println(byteBuf.arrayOffset());
            //这个readerIndex 现在还是0，因为还没有开始读取
            System.out.println(byteBuf.readerIndex());
            // 存放的字节大小 ， 要写从12开始
            System.out.println(byteBuf.writerIndex());
            //容量大小
            System.out.println(byteBuf.capacity());

            //如果这里读取了，那么下面的length就等于11
            //System.out.println(byteBuf.readByte());

            //getByte() 不会导致readerIndex变化。所以下面的length还是12
            System.out.println(byteBuf.getByte(0));

            int length = byteBuf.readableBytes();
            System.out.println("可读取字节数=" + length);

            //for循环取出各个字节
            for (int i = 0; i <length ; i++) {
                System.out.println((char) byteBuf.getByte(i));
            }

            //从0开始读取，总共读取长度是4。
            System.out.println(byteBuf.getCharSequence(0, 4 , StandardCharsets.UTF_8));

            //从4开始读取，总共读取长度是6
            System.out.println(byteBuf.getCharSequence(4, 6, StandardCharsets.UTF_8));

        }
    }
}

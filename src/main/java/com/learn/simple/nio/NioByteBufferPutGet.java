package com.learn.simple.nio;

import java.nio.ByteBuffer;

/**
 * ByteBuffer支持类型化的put和get，put放入的是什么数据类型，get就应该使用相应的数据类型来取出，
 * 否则可能有BufferUnderflowException异常。
 * @ClassName: NioByteBufferPutGet
 * @Description:
 * @Author: lin
 * @Date: 2020/10/12 22:56
 * History:
 * @<version> 1.0
 */
public class NioByteBufferPutGet {
    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(64);
        //类型化方式放入数据
        byteBuffer.putInt(100);
        byteBuffer.putLong(9);
        byteBuffer.putChar('你');
        byteBuffer.putShort((short) 6);

        byteBuffer.flip();

        System.out.println("========");

        //更加放入的类型方式正确的获取
        //System.out.println(byteBuffer.getInt());
        //System.out.println(byteBuffer.getLong());
        //System.out.println(byteBuffer.getChar());
        //System.out.println(byteBuffer.getShort());


        /**
         * 打印结果
         * ========
         * 100
         * 9
         * 你
         * 6
         */


        // 每一个根据方式类型方式，获取数据
        //取数据的时候，没有根据放入的类型方式类，那么就会报错报错。
        System.out.println(byteBuffer.getShort());
        System.out.println(byteBuffer.getInt());
        System.out.println(byteBuffer.getLong());
        System.out.println(byteBuffer.getLong());

        /**
         * 打印结果
         * 就会报 这个错误。
         * Exception in thread "main" java.nio.BufferUnderflowException
         * 	at java.nio.Buffer.nextGetIndex(Buffer.java:506)
         * 	at java.nio.HeapByteBuffer.getLong(HeapByteBuffer.java:415)
         * 	at com.learn.netty.nio.NioByteBufferPutGet.main(NioByteBufferPutGet.java:50)
         */
    }
}

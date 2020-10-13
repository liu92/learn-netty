package com.learn.simple.nio;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用ByteBuffer和FileChannel,将数据写入文件中去
 *
 * @ClassName: NioFileChannel01
 * @Description:
 * @Author: lin
 * @Date: 2020/10/12 16:03
 * History:
 * @<version> 1.0
 */
public class NioFileChannel01 {
    public static void main(String[] args) throws Exception {
        String str = "Hello, 世界";
        //创建一个输出流，为什么要创建输出流呢？因为NIO是对 java 原生io的包装。
        // 因此还是会用到 原生io的知识
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\file01.txt");

        // 通过fileOutputStream 获取对应的FileChannel
        // 这个FileChannel真实的类型是FileChannelImpl
        FileChannel fileChannel = fileOutputStream.getChannel();

        //创建一个ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // 创建ByteBuffer用来存放 str， 一个中文 按照UTF-8,一个中文对应三个字节
        byteBuffer.put(str.getBytes());

        //当数据存放到 byteBuffer后，这时就需要 反转，将数据从ByteBuffer 写入到channel中
        byteBuffer.flip();

        //将ByteBuffer中的数据，写入到channel中
        fileChannel.write(byteBuffer);
        //关闭流
        fileOutputStream.close();
    }
}

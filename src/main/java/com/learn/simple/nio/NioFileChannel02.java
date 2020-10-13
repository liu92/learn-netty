package com.learn.simple.nio;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用ByteBuffer和FileChannel,将文件的数据读取出来
 *
 * @ClassName: NioFileChannel02
 * @Description:
 * @Author: lin
 * @Date: 2020/10/12 16:03
 * History:
 * @<version> 1.0
 */
public class NioFileChannel02 {
    public static void main(String[] args) throws Exception {

       //创建文件的输入流
        File file = new File("d:\\file01.txt");

        // 输入流
        FileInputStream fileInputStream = new FileInputStream(file);

        //获取channel
        FileChannel channel = fileInputStream.getChannel();

        //分配一个缓冲区, 这里根据文件的大小来分配缓冲区大小
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());

        //然后 从通道读取数据并放到缓冲区中
        channel.read(byteBuffer);

        //打印出数据
        System.out.println(new String(byteBuffer.array()));

        //关闭输入流
        fileInputStream.close();


    }
}

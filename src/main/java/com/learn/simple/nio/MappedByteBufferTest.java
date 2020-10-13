package com.learn.simple.nio;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * MappedByteBuffer,可以让文件直接在内存(堆外的内存)中进行修改, 操作系统不需要拷贝一次
 * @ClassName: MappedByteBufferTest
 * @Description:
 * @Author: lin
 * @Date: 2020/10/13 9:02
 * History:
 * @<version> 1.0
 */
public class MappedByteBufferTest {

    public static void main(String[] args) throws Exception {
        // 使用RandomAccessFile , 指定对那个文件进行操作，
        // 第二个参数表示 模式, 这里rw 表示读写模式
        RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt", "rw");

        //获取通道
        FileChannel fileChannel = randomAccessFile.getChannel();

        /**
         * 参数含义：
         * 参数1： FileChannel.MapMode.READ_WRITE：表示使用的读写模式
         * 参数2： 0 表示可以直接修改的起始位置
         * 参数3: 5 是映射到内存的大小(不是索引位置)，即将 1.txt的多少给字节映射到内存
         * 可以直接修改的范围是0~5
         * 实际类型是 DirectByteBuffer
         */
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

        //将第一个索引位置的值改为 小写的h
        mappedByteBuffer.put(0, (byte) 'h');
        // 将索引为3的位置的值改为 7
        mappedByteBuffer.put(3, (byte)'7');

        //这里修改的是索引为5下标的数据，但是上面mappedByteBuffer中 size表示的是映射到内存的地址，所以能修改
        //的位置是索引下标为4的数据，
        // 因此这里修改下标为5的 数据，会报索引越界异常
//        mappedByteBuffer.put(5, (byte) 'Y');

        randomAccessFile.close();
        System.out.println("修改成功......");
    }

}

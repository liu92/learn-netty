package com.learn.simple.nio.zerocopy;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * @ClassName: NewIoClient
 * @Description: 测试Nio 传输文件的 时间， 客户端
 * @Author: lin
 * @Date: 2020/10/14 22:36
 * History:
 * @<version> 1.0
 */
public class NewIoClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();

        //获取到SocketChannel后，然后连接服务器
        socketChannel.connect(new InetSocketAddress("localhost", 7001));

        String fileName = "protoc-3.6.1-win32.zip";

        //得到一个文件的channel
        FileChannel fileChannel = new FileInputStream(fileName).getChannel();

        //准备发送， 记录时间
        long startTime = System.currentTimeMillis();
        // 注意：
        // 在 linux 下一个 transferTo 方法就可以完成传输
        // 在 windows 下 一次调用 transferTo 只能发送 8m , 就需要分段传输文件, 而且要注意
        // 传输时的位置
        //transferTo 底层使用到零拷贝
        // 指定位置， 和传输的大小，将这些传输到 socketChannel中去
        long transferCount = 0;

        // 在windows下如果传输的文件大于8m，那么就需要计算 8m对应的字节数
        // 如果计算出来的结果是小数，那么就要向上取整，如果计算出来是整数就不要加1。
        //计算出来 的是需要调用次数, 然后把每次传输的位置记录下来，再下一次从这个位置进行传输
        //double byteCount = Math.ceil((double) fileChannel.size())/(8*1024*1024);
        //for (int i = 0; i < byteCount; i++) {
        //    long l = fileChannel.transferTo(i*(8 * 1024 * 1024), 8 * 1024 * 1024, socketChannel);
        //    transferCount=transferCount+l;
        //}

        //linux下直接传就完事了
        transferCount = fileChannel.transferTo(0, fileChannel.size(), socketChannel);

        System.out.println(" 发 送 的 总 的 字 节 数 =" + transferCount + " 耗 时 :" +
                (System.currentTimeMillis() - startTime));
        //关闭
        fileChannel.close();
    }
}

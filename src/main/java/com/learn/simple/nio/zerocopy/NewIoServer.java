package com.learn.simple.nio.zerocopy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @ClassName: NewIoServer
 * @Description: 使用NIO来测试读取数据的时间 ，服务端
 * @Author: lin
 * @Date: 2020/10/14 22:13
 * History:
 * @<version> 1.0
 */
public class NewIoServer {
    public static void main(String[] args) throws IOException {
         //指定端口
        InetSocketAddress address = new InetSocketAddress(7001);

        //创建serverSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //获取serverSocket
        ServerSocket serverSocket = serverSocketChannel.socket();

        //绑定端口
        serverSocket.bind(address);

        //创建buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);

        while (true){
            //等待连接
            SocketChannel socketChannel = serverSocketChannel.accept();

            int readCount = 0;
            while (-1 != readCount){
                try {
                    //通过socketChannel, 读取数据到buffer。
                    // 返回读取到的数量
                   readCount = socketChannel.read(byteBuffer);
                }catch (IOException e){
//                    e.printStackTrace();
                    //这里不捕获异常，直接退出
                    break;
                }

            }

            //注意这里要将 buffer 倒带，也就是将 position设置为0 ，mark 设置为-1
            //所以调用rewind方法
            byteBuffer.rewind();
        }

    }
}

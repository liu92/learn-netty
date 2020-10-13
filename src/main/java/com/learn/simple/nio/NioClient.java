package com.learn.simple.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @ClassName: NioClient
 * @Description: 客户端
 * @Author: lin
 * @Date: 2020/10/13 16:48
 * History:
 * @<version> 1.0
 */
public class NioClient {
    public static void main(String[] args) throws Exception{
        //得到一个网络通道socketChannel
        SocketChannel socketChannel = SocketChannel.open();
        //设置非阻塞模式
        socketChannel.configureBlocking(false);
        //提供服务器端的ip和端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);

        //连接服务器

        if(!socketChannel.connect(inetSocketAddress)){
             while (!socketChannel.finishConnect()){
                 System.out.println("因为连接需要时间，客户端不会阻塞，可以做其它工作......");
             }
        }

        // 如果连接成功，就发生数据
        String str = "hi，哈哈哈哈";

        //根据字节数组的大小，来产生一个buffer。不用再指定一个大小了
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());

        //发生数据，将buff数据写入到channel
        socketChannel.write(buffer);

        //不让客户的结束，让其客户端停在这里
        System.in.read();

    }
}

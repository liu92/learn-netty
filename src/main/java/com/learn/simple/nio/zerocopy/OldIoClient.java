package com.learn.simple.nio.zerocopy;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * @ClassName: OldIoClient
 * @Description: 传统io 客户端，测试发送大文件，消耗的时间
 * @Author: lin
 * @Date: 2020/10/14 17:27
 * History:
 * @<version> 1.0
 */
public class OldIoClient {
    public static void main(String[] args) throws IOException {
        //客户连接到到ip= localhost， 端口号是7001的地址
        Socket socket = new Socket("localhost", 7001);

        String fileName = "protoc-3.6.1-win32.zip";
        //根据文件关联一个流
        InputStream inputStream = new FileInputStream(fileName);

        //然后通过socket，获取到一个输出流
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        byte[] buffer = new byte[4096];
        long readCount;
        int total = 0;
        long startTime = System.currentTimeMillis();

        //循环不停的去读取，然后将这个文件数据放到 这个byte数组中。
        while ((readCount = inputStream.read(buffer)) >= 0){
             total += readCount;
             //然后将byte数组中的数据写入 输出流中
             dataOutputStream.write(buffer);
        }
        System.out.println("发送总字节数： " + total + ", 耗时： " + (System.currentTimeMillis() - startTime));

        dataOutputStream.close();
        socket.close();
        inputStream.close();
    }
}

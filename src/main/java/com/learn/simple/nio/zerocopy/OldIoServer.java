package com.learn.simple.nio.zerocopy;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 传统io 服务端，测试发送大文件，消耗的时间
 * @ClassName: OldIoServer
 * @Description:
 * @Author: lin
 * @Date: 2020/10/14 17:27
 * History:
 * @<version> 1.0
 */
public class OldIoServer {
    public static void main(String[] args) throws IOException {
        //服务端在 端口7001上进行监听
        ServerSocket serverSocket = new ServerSocket(7001);

        while (true){
            //等待客户端连接
            Socket socket = serverSocket.accept();
            //连接成功后，得到一个 InputStream流
            //然后返回 DadaInputStream输入流
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            //创建一个byte数组
            byte[] bytes = new byte[4096];
            while (true){
                int read = dataInputStream.read(bytes, 0, bytes.length);
                //如果等于-1,那么就是没有读取到
                if( -1 == read){
                    break;
                }
            }

        }
    }
}

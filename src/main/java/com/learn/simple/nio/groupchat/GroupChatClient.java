package com.learn.simple.nio.groupchat;


import com.learn.simple.DefaultThreadFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 *  群聊系统---客户端
 * @ClassName: GroupChatClient
 * @Description:
 * @Author: lin
 * @Date: 2020/10/14 11:19
 * History:
 * @<version> 1.0
 */
public class GroupChatClient {
    /**
     * 定义selector
     */
    private Selector selector;

    /**
     * 定义socketChannel
     */
    private SocketChannel socketChannel;

    /**
     * 服务器ip
     */
    private final  String HOST = "127.0.0.1";

    /**
     * 服务器端口
     */
    private final int PORT = 6667;

    /**
     * 客户端发送消息是会自己取名字，那么这里就声明一个名字
     */
    private String userName;

    public static void main(String[] args) throws IOException {

        //启动客户端
        GroupChatClient chatClient = new GroupChatClient();

        ExecutorService poolExecutor = new ThreadPoolExecutor(5, 10,
                1L, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(3),
                new DefaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
        //启动一个线程，每隔3秒，读取从服务器发送的数据
//        new Thread(){
//            @Override
//            public void run() {
//                while (true){
//                    chatClient.readInfo();
//                    try {
//                        sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
         poolExecutor.execute(new MyThread(chatClient));



        //发送数据给服务端, 因为发送数据是从控制台输入的
        //所以创建一个扫描器，
        Scanner scanner = new Scanner(System.in);
        // 循环，只有还是下一行就去读取
        // 等待客户端不停的输入
        while (scanner.hasNextLine()){
            //读取下一行，得到的就是一个字符串了，
            String s = scanner.nextLine();
            //将读取的到是字符 发送给服务器端，就可以了
            chatClient.sendInfo(s);
        }
    }


    /**
     * 1、构造器，完成初始化工作
     */
    public  GroupChatClient() throws IOException {
        selector = Selector.open();
        //连接服务器
        socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
        //设置非阻塞
        socketChannel.configureBlocking(false);
        //将channel注册到 selector中
        socketChannel.register(selector, SelectionKey.OP_READ);
        //得到本地的地址，然后拼接 最后分配给userName
        userName = socketChannel.getLocalAddress().toString().substring(1);

        System.out.println(userName + " is ok...");
    }

    /**
     * 2、向服务端发送消息，那么就需要知道 要发送什么消息
     * @param info
     */
    public void sendInfo(String info){
        //拼接 一下要发送的 消息
        info = userName + " 说："+ info;
        try {
            //从给定的buffer缓冲区, 往通道里面写数据
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 3、读取从服务端 回复的消息
     */
    public void readInfo(){
       try {
         int readChannels = selector.select();
         //使用有可用的通道
         if(readChannels > 0){
             //返回获取迭代器，进行循环处理
             Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

             while (iterator.hasNext()){
                 SelectionKey key = iterator.next();
                 //判断是否可读
                 if(key.isReadable()){
                     //得到相关的通道
                     SocketChannel sc =(SocketChannel) key.channel();
                     //得到一个ByteBuffer
                     ByteBuffer buffer = ByteBuffer.allocate(1024);
                     //从通道里读取数据到buffer
                     sc.read(buffer);

                     //把读取到的缓冲区数据转换成字符串
                     String msg = new String(buffer.array());

                     //打印信息
                     System.out.println(msg.trim());

                 }
             }

             //注意: 不要忘记删除 当前的SelectionKey，防止重复操作
             iterator.remove();
         }else{
             //System.out.println("没有可用的通道");
         }
       }catch (Exception e){
          e.printStackTrace();
       }

    }
}


class MyThread implements Runnable{

    GroupChatClient chatClient;

    public MyThread(){}

    public MyThread(GroupChatClient chatClient){
        this.chatClient = chatClient;
    }

    @Override
    public void run() {
        while (true){
            chatClient.readInfo();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
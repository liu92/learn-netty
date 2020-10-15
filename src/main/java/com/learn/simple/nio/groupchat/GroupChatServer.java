package com.learn.simple.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 *  群聊系统---服务端实现类
 * @ClassName: GroupChatServer
 * @Description:
 * @Author: lin
 * @Date: 2020/10/14 10:33
 * History:
 * @<version> 1.0
 */
public class GroupChatServer {
    /**
     * 定义相关属性
     */
    private  Selector selector;

    private  ServerSocketChannel listenChannel;
    /**
     * 监听端口号
     */
    private final static  int PORT = 6667;


    public static void main(String[] args) {
           GroupChatServer chatServer = new GroupChatServer();
           chatServer.listen();
    }



    /**
     * 构造器， 进行初始化工作
     */
    public  GroupChatServer() {
        try {
            //得到选择器
            selector = Selector.open();
            //得到serverSocketChannel
            listenChannel = ServerSocketChannel.open();
            //绑定端口
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            //设置非阻塞
            listenChannel.configureBlocking(false);

            //将listenChannel注册到selector中, 返回selectionKey
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 监听端口，然后循环读取客户端发送过来的数据
     */
    public  void  listen(){
      System.out.println("监听线程：" + Thread.currentThread().getName());
      try {
         while (true){
          // 让其阻塞等待
         int count = selector.select();
         //表示有事件处理
          if(count > 0 ){
            //遍历得到的SelectionKey集合
              Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
              while (iterator.hasNext()){
                  //取出selectionKey
                  SelectionKey key = iterator.next();

                  //监听事件
                  if(key.isAcceptable()){
                      SocketChannel sc = listenChannel.accept();
                      //设置channel非阻塞
                      sc.configureBlocking(false);

                      //将channel注册到selector
                      sc.register(selector, SelectionKey.OP_READ);

                      //提示那个客户端上线了
                      System.out.println(sc.getRemoteAddress() + " 上线");
                  }

                  //其它事件，有数据需要读取
                  // 通道发送read事件，即通道是可读的状态， 将通道中的数据读取到buffer中去
                  if(key.isReadable()){
                      //调用读取数据方法，传入key
                     readData(key);
                  }

                  //当前的key删除，防止重复处理
                  iterator.remove();
              }
          }else{
             // System.out.println("等待......");
          }
         }
      } catch (IOException e) {
         e.printStackTrace();
      }finally {
          //
      }

    }


    /**
     * 读取数据，从通道读取数据到buffer
     * @param key
     */
    private void  readData(SelectionKey key){
        //关联到channel
        SocketChannel channel = null;

        try {
            channel = (SocketChannel)key.channel();
            //创建buffer

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            //将channel的数据读取到buffer
            int count = channel.read(byteBuffer);
            //根据count值处理
            if(count > 0){
                 //把缓冲区的数据转换成字符串
                String msg = new String(byteBuffer.array());
                //输出该消息
                System.out.println("form 客户端：" + msg);

                //然后向其它的客户端发送消息(去掉自己)，专门下一个方法来处理
                sendInfoToOtherClients(msg, channel);
            }


        }catch (IOException e){
            try {
                System.out.println(channel.getRemoteAddress() + " 离线了.....");
                //取消注册
                key.cancel();
                //关闭通道
                channel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }


    /**
     * 转发给其它客户端(通道)
     * @param msg
     * @param self
     * @throws IOException
     */
    private void sendInfoToOtherClients(String msg, SocketChannel self) throws IOException {
        System.out.println("服务器转发消息中......");
        System.out.println("服务器转发数据给客户端线程：" + Thread.currentThread().getName());
        //遍历所有注册到Selector中的SocketChannel,并排除self
        for (SelectionKey key : selector.keys()) {
            //通过key取出对应的 SocketChannel, 因为实现了chanel
            Channel targetChannel = key.channel();

            //排除自己
            if(targetChannel instanceof  SocketChannel && targetChannel != self){
               //这个时候就可以转发消息了
               //转换， 将消息发送到那个channel
               SocketChannel dest = (SocketChannel)targetChannel;
               //将msg 存储到buffer中
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());

               //将buffer的数据写入到通道中
                dest.write(buffer);

            }
        }
    }

}

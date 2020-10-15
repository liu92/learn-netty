package com.learn.simple.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 服务端
 * @ClassName: NioServer
 * @Description:
 * @Author: lin
 * @Date: 2020/10/13 15:46
 * History:
 * @<version> 1.0
 */
public class NioServer {
    public static void main(String[] args) throws Exception {
        //1、创建serverSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //2、创建selector
        Selector selector = Selector.open();

        //3、绑定一个端口，然后服务端进行监听
        InetSocketAddress socketAddress = new InetSocketAddress(6666);
        serverSocketChannel.socket().bind(socketAddress);

        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        //4、把serverSocketChannel 注册到selector ，然后关心的事件为 OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // selector.keys： 表示所有
        // selector.selectedKeys(): 表示channel里面的事件个数
        System.out.println("注册后的SelectionKey 数量" + selector.keys().size());

        //

        //可以支持多个线程 循环监听
        //循环等待客户端连接，
        while (true) {

            //看看 有哪些事件过来
            // 这里只等待 1秒，如没有时间发生，则返回
            // 如果等待时间过来那么也不会阻塞在这里，
            // 如果等于0，那么表示没有事件发生
            if(selector.select(1000) == 0){
                System.out.println("服务器等待了1s，无连接");
                continue;
            }

            // 如果返回的 大于>0 ，就获取到相关的SelectionKey集合
            // 1.如果大于>0 ,表示已经获取到关注的事件了
            // 2.通过selector.selectedKeys() 返回关注事件的集合
            // 3.通过SelectionKey 反向获取到通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            //遍历Set<SelectionKey>,使用迭代器遍历
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            while (keyIterator.hasNext()){
                 //获取到SelectionKey
                SelectionKey key = keyIterator.next();
                //根据key对应的通道发生的事件做相应的处理, 也就是key关联的通道到底发生的什么事情
                //如果是OP_ACCEPT, 那么就是有新的客户端连接
                if(key.isAcceptable()) {
                    //当一个客户端来连接，那么就应该产生一个新的通道 socketChannel
                    // 使用key为该客户端生成一个SocketChannel,
                    // 这里注意：一般来说accept()是阻塞，但是这里 已经知道了告诉了连接已经发生了
                    // 这里就不会等待了。在传统的方式中accept是不知道有没有连接来进行连接。
                    // 因为这里NIO的事件驱动，到这一步已经知道了连接，所以马上就会执行。
                    SocketChannel socketChannel = serverSocketChannel.accept();

                    System.out.println("客户端连接成 生成了一个 socketChannel " + socketChannel.hashCode());

                    // 因为在创建socketChannel 没有将其设置非阻塞的, 就会报非法的阻塞模型异常
                    //将SocketChannel设置为非阻塞，如果不设置那么就会报异常.
                    socketChannel.configureBlocking(false);

                    //将SocketChannel注册到Selector上, 关注事件为OP_READ( 这个通道里面有读的事件发生了就去读取传过来的数据),
                    // 同时给socketChannel关联一个buffer
                    // 第一参数：选择器
                    // 第二个参数：事件选择器
                    // 第三个参数：给注册通道绑定 一个Buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                    System.out.println("客户端连接后，注册后的SelectionKey 数量" + selector.keys().size());
                }

                /**得到一个连接过后，对应客户端来讲第一次连接好，生成好SocketChannel 就可以发生数据了
                 * 发生数据也是这里，因为select返回的事件不只是连接事件，还有其它事件
                 */

                //将通道注册好了之后，下一个就要发生数据
                // 发生数据就会进行下面这个判断， 发生OP_READ事件
                if(key.isReadable()){
                   //通过key 反向获取channel(因为这个通道可能发生了 连接事件也可能发生 读的事件 这是两个不同的业务)
                   // 向下转型
                   SocketChannel channel = (SocketChannel)key.channel();
                   // 然后获取 这个channel关联的buffer, 因为在socketChannel注册到selector时 也设置了一个buffer
                   // 这里通过attachment()获取一个对象 并强制转换。
                   ByteBuffer buffer = (ByteBuffer)key.attachment();

                   // 把当前通道中的数据读入到buffer中去
                    channel.read(buffer);
                    System.out.println("form 客户端 " + new String(buffer.array()));
                }

                //最后特别重要一件事情，要及时的把当前的key删除掉
                // 手动从当前集合中移除 当前的SelectionKey， 防止重复操作。
                // 为什么要移除？ 因为在进行遍历的时候，是一个多线程的问题，如果没有及时删除
                // 就会造成重复操作
                keyIterator.remove();

            }
        }


    }
}

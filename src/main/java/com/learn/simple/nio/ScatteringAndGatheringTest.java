package com.learn.simple.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Scattering：将数据写入到buffer时，可以采用buffer数组，依次写入[分散]
 * Gathering：从buffer读取数据时，可以采用buffer数组，依次读
 *
 * @ClassName: ScatteringAndGatheringTest
 * @Description:
 * @Author: lin
 * @Date: 2020/10/13 9:39
 * History:
 * @<version> 1.0
 */
public class ScatteringAndGatheringTest {
    public static void main(String[] args)throws  Exception {
      //使用ServerSocketChannel 和SocketChannel 网络
      ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

      //创建一个server address。
      InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);

      //绑定端口到socket，并启动
      serverSocketChannel.socket().bind(inetSocketAddress);

      //创建buffer数组
      ByteBuffer[] byteBuffers = new ByteBuffer[2];
      //分别创建两个元素，并且分配两个元素的字节，
      // 这样分配是为了 看到buffer是如何依次写入和读取出来的。
      byteBuffers[0] = ByteBuffer.allocate(5);
      byteBuffers[1] = ByteBuffer.allocate(3);

      //等待客户端连接， 这个连接成功了就 得到了SocketChannel,然后这个Channel就和服务器关联起来了。
      SocketChannel socketChannel = serverSocketChannel.accept();
      // 假定从客户端接收8个字节
      int messageLength = 8;

      //因为不知道客户端 发送了多少数据过来，所以循环读取
      while (true){
          //到达读取了多少个字节，记录下来
          int byteRead = 0;

          //如果读取的字节数不够，那么就继续读取。最多不超过8个字节
          // 然后看到这8个字节，是不是先放到 buffer的第一个，再放第二个
          while (byteRead < messageLength){
              // 将数据 放到 buffer中，
              // 注意这里 buffer是一个数组，它会自己去分散的放到 第一个buffer中，还是第二个buffer中
              long l = socketChannel.read(byteBuffers);
              //累计读取的字节数
              byteRead += l;
              System.out.println("byteRead=:" + byteRead);

              //使用流打印，看看当前的这个buffer的position和limit是多少
              Arrays.stream(byteBuffers).map(buffer -> "position=" +
                      buffer.position() + ",limit=" + buffer.limit()).forEach(System.out::println);
          }

          //将所有的buffer进行反转，因为将来可能会进行输出等等
          Arrays.asList(byteBuffers).forEach(buffer -> buffer.flip());

          //反转完后，将数据读出 显示到客户端
          // 定义一个变量，来控制回显的大小
          long byteWrite = 0;

          //当回显的数据小于 定义的字节数，那么就循环来处理
          while (byteWrite < messageLength){
              long l = socketChannel.write(byteBuffers);
              byteWrite +=l;
          }

          //将所有的buffer进行clear操作
          Arrays.asList(byteBuffers).forEach(buffer -> buffer.clear());
          System.out.println("byteRead:=" + byteRead + " byteWrite=" + byteWrite + ",messageLength" + messageLength);
      }

    }


















}

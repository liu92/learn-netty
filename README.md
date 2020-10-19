# Netty学习笔记

## 1、Netty是什么

   1)、Netty是由JBOSS提供的一个java开源框架，现为Github上的独立项目名

   2)、Netty是一个异步的，基于事件驱动的网络应用框架，用以快速开发高性能、高可靠性的网络IO程序

   3)、Netty主要针对在TCP协议下，面向Clients端的高并发应用，或者Peer-to-Peer场景下的大量数据持续传输的应用。

   4)、Netty本质是一个NIO框架，适用于服务器通讯相关的多种应用场景。

   5)、要透彻理解Netty，首先要学习NIO, 才能读懂源码

 Netty框架是在这个一层一层的底层演变出来的。

![image](image/netty-framwork-01.png)

### 1.2、Netty的应用场景

 1)、互联网行业：在分布式系统中，各个节点之间需要远程服务调用，高性能的RPC框架必不可少,Netty作为异步高性能的通信框架，往往作为集成通信组件被这些RPC框架使用。

2)、典型的应用有：阿里分布式框架Dubbo的RPC框架使用Dubbo协议进行节点间的通信，Dubbo协议默认使用Netty作为基础通信组件，用于实现进程节点之间的内部通信。

3)、游戏行业：

​       a.无论是手游服务端还是大型的网络游戏，java语言得到了越来越广泛的应用

​       b.netty作为高性能的基础通信组件，提供了TCP/UDP和HTTP协议栈，方便定制和开发私有协议栈，账号登录服务器。

​      c.地图服务器之间可以方便的通过Netty进行高性能的通信。

4)、大数据领域：

​      a.经典的Hadoop的高性能和序列化组件(AVRO 实现数据文件共享)的RPC框架，默认采用Netty进行跨界点通信

​      b.它的Netty Service基于Netty框架进行二次封装实现



## 2、java BIO模型

###   2.1 I/O模型

1)、I/O模型简单的理解：就是用什么样的通道进行数据的发送和接收，很大程度上决定了程序通信的性能

2)、JAVA共支持3中网络编程模型I/O模型：BIO、NIO、AIO

3)、Java BIO：同步并阻塞(传统阻塞型)，服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器端就需要启动一个线程进行处理，如果这个连接不做任何事情会造成不必要的线程开销 【简单示意图】



下图展示的是一个简单的BIO模型，当一个请求过来服务器端就会产生一个线程，这个线程就会和服务端进行通讯。底层有socket，

通过这个socket进行读或者写。

![image](image/java-bio-01.png)

这种模型的缺点：就是当客户端非常多情况下，就会创建很多线程，而线程的创建是有开销，并且服务器端的压力非常大。这些连接也并不是时时刻刻的在进行读写，这就造成了不必要的开销，同时在读的时候会造成阻塞。



4) Java NIO: 同步非阻塞，服务器实现模式为一个线程处理多个请求(连接)，即客户端发送的连接请求都会注册到多路复用器上，多路复用器轮询到连接有I/O请求就进行处理。【简单示意图如下】



服务器启动一个线程，这个线程会去维护一个selector(选择器)，这个selector去维护多个通道。通过去轮询监听事件的发生。

![image](image/java-nio-01.png)



5)、Java AIO(NIO.2): 异步非阻塞，AIO引入异步通道的概念，采用了Proactor模式，简化了程序编写，有效的请求才启动线程，它的特点是由操作系统完成后才通知服务端程序启动线程去处理，一般适用于连接数较多且较长的应用

### 2.2、BIO、NIO、AIO适用场景分析

 1)、BIO方式适用于连接数目比较小且固定的架构，这种方式对服务器资源要求比较高，并发局限 于 应用中，JDK1.4以前的唯一选择，但是程序简单易理解。

2)、NIO方式适用于连接数目多且连接比较短(轻操作)的架构，比如聊天服务器，弹幕系统，服务器间通讯等。编程比较复杂，JDK1.4开始支持。

3)、AIO方式适用连接数目多且连接比较长(重操作)的架构，比如相册服务器，充分调用OS参与并发操作，编程比较复杂, JDK7开始支持。

### 2.3 Java BIO 基本介绍

 1)、Java BIO就是传统的Java io 编程，其相关的类和接口在 java.io包下面

 2)、BIO(blocking I/O): 同步阻塞，服务器实现模型为一个连接一个线程，即客户端有连接请求时服务器端就需要启动一个线程进行处理，如果这个连接不做任何事情会造成不必要的线程开销，可以通过线程池机制改善(实现多个客户连接服务器)

3)、BIO方式适用于连接数目比较小且固定的架构，这种方式对服务器资源要求比较高，并发局限 于 应用中，JDK1.4以前的唯一选择，但是程序简单易理解。

### 2.4、Java BIO 工作机制

![image](image/java-bio-01.png)

BIO编程简单流程

1)、服务器端启动一个ServerSocket

2)、客户端启动Socket对服务器进行通信，默认情况下服务器端需要对每个客户建立一个线程与之通讯

3)、客户端发出请求后，先询问服务器是否有线程响应，如果没有则会等待，或者被拒绝

4)、如果有响应，客户端线程会等待请求结束后，在继续执行

### 2.5、 java BIO应用实例

1）使用BIO模型编写一个服务器，监听6666端口，当有客户端连接时，就启动一个线程与之通讯。

2）要求使用线程池机制改善，可以连接多个客户端

3）服务器可以接收客户端发送的数据(telnet方式 即可)

4）代码示例：

```java
package com.learn.simple.bio;

import com.learn.simple.DefaultThreadFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @ClassName: BioServer
 * @Description:
 * @Author: lin
 * @Date: 2020/10/12 8:35
 * History:
 * @<version> 1.0
 */
public class BioServer {
    public static void main(String[] args) throws IOException {
        // 思路：
        //1、使用线程池改善
        //2、如果有客户端连接了，那么就创建一个线程，与之通讯(单独写一个方法)
        ExecutorService poolExecutor = new ThreadPoolExecutor(5, 10,
                1L, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(3),
                Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());

       //3、创建ServerSocket
        ServerSocket serverSocket = new ServerSocket(6666);

        System.out.println("服务器启动了");

        while (true){
            System.out.println("线程信息 id= " + Thread.currentThread().getId() + " 名字=" +
                    Thread.currentThread().getName());

            //监听，等待客户端连接
             System.out.println("等待连接......");
            final  Socket socket = serverSocket.accept();
            System.out.println("连接到一个客户端了");

            //就创建一个线程，与之通讯(单独写一个方法)
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                   handler(socket);
                }
            });
        }
    }

    /**
     * 编写一个handler方法，和客户端通讯
     */
    public static void handler(Socket socket){
       try {
           //打印线程id，以此来区别启动不同的客户端，线程id是否相同
           System.out.println("线程信息 id= " + Thread.currentThread().getId() + " 名字=" +
                   Thread.currentThread().getName());


           //接收数据
           byte[] bytes = new byte[1024];
           //通过socket获取输入流
           InputStream inputStream = socket.getInputStream();
           //输入流就可以获取到管道里面的数据
           //循环的读取客户端发送的数据
           while (true){
               System.out.println("线程信息 id= " + Thread.currentThread().getId() + " 名字=" +
                       Thread.currentThread().getName());
               //将数据读取到 byte数组中数据
                System.out.println("read......");
               int read = inputStream.read(bytes);

               //如果read不等于-1，那么就还没有读取完
               if(read != -1){
                   //输出客户端发送的数据
                   // 将byte转换成字符串，从0开始转换。
                   System.out.println(new String(bytes, 0, read));
               }else {
                   //如果读取完了，就退出循环
                   break;
               }
           }

       } catch (IOException e) {
           e.printStackTrace();
       }finally {
           System.out.println("关闭和client的连接");
           try {
               socket.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

    }


}

```

使用cmd，然后输入telnet 127.0.0.1 6666 来开启一个客户端，然后输入ctrl+] 进行数据的发送测试

![image](image/java-bio-03.png)



执行结果,可以看到在启动两个不同的客户端时，会创建两个不同的线程，这个和上面说流程一致。并且如果没有得到客户端连接，那么服务端 serverSocket.accept(); 就会一直阻塞，一直等待有客户端进来。 inputStream.read(bytes); 这里没有读取到数据，也会阻塞

![image](image/java-bio-02.png)

注意：在这个示例中，没有一个客户端连接，那么就会进行阻塞等待，直到有客户端连接

![image](image/java-bio-04.png)

同样，如果没有数据，同样也会阻塞

![image](image/java-bio-05.png)



这里为什么等待连接打印了两次？ 这也是因为在创建了线程id=19的线程后，然后又回到了main线程 去监听。这种情况就像是，在服务端有一个线程一直监听是否又客户端连接过来，如果有客户端连接过来那么就会创建一个子线程去进行通讯，然后主线程又回到服务端去监听，当有客户端连接进来，又会产生一个子线程去执行。。。。



### 2.6、 java BIO 问题分析

1)、每个请求需要创建独立的线程，与对应的客户端进行数据的Read，业务处理，数据Write。

2)、当并发数较大时，需要创建大量线程来处理连接，系统资源占用较大。

3)、连接建立后，如果当前线程暂时没有数据可读，则线程就阻塞在Read操作上，造成资源浪费。



## 3、Java NIO 编程

### 3.1、Java NIO基本介绍

 1)、Java NIO全称java no-blocking IO，是指JDk提供的新的API。从JDK1.4开始，Java提供了一系列改进的输入/输出的新特性,被统称为NIO(New IO)，是$\textcolor{Red}{同步非阻塞的}$。

 2)、NIO相关类都被放在java.nio包以子包下，并且对原java.io包中的很多类进行改写。

 3)、NIO有三大核心部分：$\textcolor{Red}{Channel(通道)}$，$\textcolor{Red}{Buffer(缓存区)}$，$\textcolor{Red}{Selector(选择器)}$

![image](image/java-nio-02.png)

4)、$\textcolor{Red}{NIO是面向缓冲区，或者面向块编程的}$。数据读取到一个它稍后处理的缓冲区，需要时可在缓冲区中前后移动，这就增加了处理过程中 的灵活性，使用它可以提供非阻塞是的高伸缩性网络

5)、Java NIO的非阻塞模式，使一个线程从某通道发送请求 或者读取数据，但是它仅能得到目前可用的数据，如果目前没有数据可用时，就什么都不会获取，$\textcolor{Red}{而不是保持线程阻塞}$，所以直至数据变的可以读取之前，该线程可以继续做其它的事情。非阻塞写也是如此，一个线程请求写入一些数据到某通道，但不需要等待它完全写入，这个线程同时可以去做别的事情。

6)、同事理解：NIO是可以做到用一个线程处理多个操作的，假设有10000个请求过来，根据实际情况，可以分配50或者100线程来处理。不想之前的阻塞IO那样，非得分配1000个。

7)、HTTP2.0使用了多路复用的技术，做到同一连接并发处理多个请求，而且并发请求的数量比HTTP1.1大了好几个数量级。

8)、案例NIO的 Buffer

```java
package com.learn.simple.nio;

import java.nio.IntBuffer;

/**
 * @ClassName: BasicBuffer
 * @Description:
 * @Author: lin
 * @Date: 2020/10/12 11:12
 * History:
 * @<version> 1.0
 */
public class BasicBuffer {
    public static void main(String[] args) {

        //举例说明Buffer的使用(简单说明)
        //创建一个buffer，大小为5，可以存放5个int
        IntBuffer intBuffer = IntBuffer.allocate(6);

//        intBuffer.put(10);
//        intBuffer.put(11);
//        intBuffer.put(12);
//        intBuffer.put(13);
//        intBuffer.put(14);


        //项buffer，存放数据。
        for (int i = 0; i < intBuffer.capacity() ; i++) {
            intBuffer.put(i * 2);
        }

        //如何从Buffer中读取数据
        //将buffer转换，读写切换
        intBuffer.flip();

        //是否有剩余的
        while (intBuffer.hasRemaining()){
            System.out.println(intBuffer.get());
        }

    }


}

```





在NIO模型中，selector选择channle，channle(通道)和buffer进行数据的交互，而程序不再和channle进行交互，程序是和buffer进行数据交互。这样就可以实现一个非阻塞的机制





### 3.2、NIO和BIO的比较

1)、BIO以流的方式处理数据，而NIO以块 方式处理数据，块I/O的效率比流I/O高很多。

2)、BIO是阻塞的，NIO则是非阻塞的

3)、BIO基于字节流和字符流进行操作，而NIO基于Channel(通道)和Buffer(缓冲区)进行操作，数据总是从通道读取到缓冲区中，或者从缓冲区写入到通道中。Selector(选择器)用于监听多个通道的事件(比如：连接请求，数据到达等)，因此使用单个线程就可以监听多个客户端通道。



### 3.3、NIO三大核心原理示意图

一张图描述NIO的Selector、Channel和 Buffer的关系。

![image](image/java-nio-03.png)



Selector、Channel和Buffer的关系说明：

1)、每个Channel都会对应一个Buffer

2)、Selector对应一个线程，一个线程对应多个Channel(可以理解为一个连接)。

3)、该图反应了有三个channel注册到 Selector上

4)、程序切换到那个channel上，是由事件决定的，Event是一个非常重要的概念。

5)、Selecto会根据不同的事件，在各个通道上切换。

6)、Buffer就是一个内存块，底层是一个数组

7)、数据的读取写入是通过Buffer，这个是和BIO的区别，BIO中要么是输入流，或者是输出流，不能双向，

但是NIO的Buffer是可以读也可以写，需要fli配方法切换。

8)、channel是双向的，可以反应底层操作系统的情况，比如linux，底层的操作系统通道就是双向的。



### 3.4、缓冲区(Buffer)

#### 3.4.1、基本介绍

 缓冲区：缓冲区本质上是一个可以读写数据的内存块，可以理解成是一个容器对象(含数组)，该对象提供了一组方法，可以更轻松地使用内存块，缓冲区对象内置了一些机制，能够跟踪和记录缓冲区的状态变化情况。Channel提供从文件、网络读取数据的渠道，但是读取或写入的数据都必须由Buffer来完成。

![image](image/java-nio-buffer-01.png)

#### 3.4.2、Buffer类及其子类

1)、在NIO中，Buffer是一个顶层父类，它是一个抽象类，类的层级关系图如下。

在这些类中，都维护了一个数组，这个数组就是用来存放数据的。

![image](image/java-nio-buffer-02.png)



常用Buffer子类一览

```
1、ByteBuffer，存储字节数据到缓冲区
2、ShortBuffer,存储字符串数据到缓冲区
3、CharBuffer，存储字符数据到缓冲区
4、IntBuffer，存储整数数据到缓冲区
5、LongBuffer，存储长整型数据到缓冲区
6、DoubleBuffer,存储小数到缓冲区
7、FloatBuffer，存储小数到缓冲区

注意：这里是没有Boolean 缓冲区的。
```



2)、Buffer类定义了所有的缓冲区都具有的四个属性来提供关于其所包含的数据元素的信息：

```
   // Invariants: mark <= position <= limit <= capacity
    private int mark = -1;
    private int position = 0;
    private int limit;
    private int capacity;
```

| 属性     | 描述                                                         |
| -------- | ------------------------------------------------------------ |
| capacity | 容量，即可以容纳的最大数据量；在缓冲区创建时被设定并且不能改变 |
| limit    | 表示缓冲区的当前终点，不能对缓冲区超过极限的位置进行读写操作。且极限是可以修改的 |
| position | 位置，下一个要被读或写的元素的索引，每次读写缓存区数据都会改变值， |
| mark     | 标记                                                         |

我们debug来看上面几个变量的变化,开始时的position=0.  这里的容量是6，也就是说limit不能超过6.

![image](image/java-nio-buffer-03.png)

当上面的代码执行完 循环之后，可以看到这个position=6

![image](image/java-nio-buffer-04.png)

当执行到flip时，进行读写切换。进行读操作, flip方法会将position的值赋值给limit。然后将0赋值给position。这样就开始从0读取数据。

```
   public final Buffer flip() {
        limit = position;
        position = 0;
        mark = -1;
        return this;
    }
```

可以看到这个position=0，那么就从数组的0开始读取数。

![image](image/java-nio-buffer-05.png)

数据读取完了,可以看到position=6 了。

![image](image/java-nio-buffer-06.png)



3)、Buffer类相关方法

```
public abstract class Buffer{
  //jdk1.4时，引入的api
  public final int capacity()//返回此缓冲区的容量
  public final int position()//返回此缓冲区的位置
  public final Buffer position(int newPosition)//设置此缓冲区的位置
  public final int limit()//返回此缓冲区的限制
  public final Buffer limit(int newLimit)//设置此缓冲区的限制
  public final Buffer mark()//在此缓冲区的位置设置标记
  public final Buffer reset()//将此缓冲区的位置重置为以前标记的位置
  public final Buffer clear()//清除缓冲区，即将各个标记恢复到初始状态，但数据并没有正真清除
  public final Buffer flip()//反转此缓冲区
  public final Buffer rewind()//重绕此缓冲区
  public final int remaining()//返回当前位置与限制之间的元素数
  public final boolean hasRemaining()//告知当前位置与限制之间是否元素
  public final boolean isReadOnly()//告知此缓冲区是否为只读缓冲区
  
  //jdk1.6引入的api
   public abstract boolean hasArray()//告知此缓冲区是否具有可访问的底层实现数组
   public abstract Object array()//返回此缓冲区的底层实现数组
   public abstract int arrayOffset()//返回此缓冲区的底层实现数组中第一个缓冲区元素的偏移量
   public abstract boolean isDirect()//返回此缓冲区是否为直接缓冲区
}
```

#### 3.4.3、ByteBuffer(使用最多)

 从前面可以看出对于java中的基本数据类型(boolean除外)，都有一个buffer类型与之相对应，最常用的自然是ByteBuffer类(二级制数据)，该类的主要方法如下：

```
public abstract class ByteBuffer{
  //缓冲区创建相关api
  public static ByteBuffer allocateDirect(int capacity)//创建直接缓冲区
  public static ByteBuffer allocate(int capacity)//设置缓冲区的初始容量
  public static ByteBuffer wrap(byte[] array)//把一个数组放到缓冲区中使用
  public static ByteBuffer wrap(byte[] arry,int offset,int length)//构造初始化位置offset和上界lenght的缓冲区
  
  //缓存区存取相关API
  public abstract byte get()//当前位置position上get，get之后，position会自动+1
  public abstract byte get(int index)//从绝对位置get
  public abstract ByteBuffer put(byte b)//从当前位置上添加, put之后，position会自动+1
  public abstract ByteBuffer put(int index, byte b)//从绝对位置上put
  
 
}
```

### 3.5、通道(Channel)

####  3.5.1 基本介绍

  1)、NIO的通道类似于流，但是和流有区别

​       a、通道可以同时进行读写，而流只能读或者只能写

​       b、通道可以实现异步读写数据

​       c、通道可以从缓冲读数据，也可以写数据到缓冲区

![image](image/java-nio-channel-buffer-01.png)

2)、BIO中 的strem是单向的，例如FileInputStream对象只能进行读取数据的操作，而NIO中的通道(Channel)是双向的，可以读操作，也可以写操作。

3)、Channel在NIO中是一个接口

```
public interface Channel extendx Closeable{}
```

4)、常用的Channel类有：FileChannel、DatagramChannel、ServerSocketChannel和SocketChannel。【ServerSocketChannel类似 ServerSocket, SocketChannel类似 Socket】

5)、FileChannel用于文件的数据读写，DatagramChannel用于UDP的数据读写，ServerSocketChannel和SocketChannel用于TCP的数据读写。



当一个连接来连接 server的时候，有ServerSocketChannel (真实的类型是 ServerSocketChannelImpl)产生一个 与该客户端对应的一个通道，这个通道就是SocketChannel，而这个真实的实现类型是SocketChannelImpl。 然后再通过这个通道和服务端进行通讯



![image](image/java-nio-channel-01.png)





#### 3.5.2 FileChannel类

FileChannel主要用来对本地文件进行IO操作，常见的方法有

```
public int read(ByteBuffer dst), 从通道读取数据并放到缓冲区中
public int write(ByteBuffer src)，把缓冲区的数据写到通道中
public long transferFrom(ReadableByteChannel src, long position, long count) 从目标通道中复制数据到当前通道
public long transferTo(long position, long count, WritableByteChannel traget) 把数据从当前通道复制给目标通道
```



#### 3.5.3、应用案例1-本地文件写数据

  实例要求：

1)、使用前面学习后的ByteBuffer(缓冲)和FileChannel(通道)，将"hello，world"写入到file01.txt文件中

2)、文件不存在就创建

3)、代码示例如下

```java
package com.learn.simple.nio;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用ByteBuffer和FileChannel,将数据写入文件中去
 *
 * @ClassName: NioFileChannel01
 * @Description:
 * @Author: lin
 * @Date: 2020/10/12 16:03
 * History:
 * @<version> 1.0
 */
public class NioFileChannel01 {
    public static void main(String[] args) throws Exception {
        String str = "Hello, 世界";
        //创建一个输出流，为什么要创建输出流呢？FileOutputStream 中包裹了Channel。通过这个来获取channel.
        // 因此还是会用到 原生io的知识
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\file01.txt");

        // 通过fileOutputStream 获取对应的FileChannel
        // 这个FileChannel真实的类型是FileChannelImpl
        FileChannel fileChannel = fileOutputStream.getChannel();

        //创建一个ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    // 创建ByteBuffer用来存放 str， 一个中文 按照UTF-8,一个中文对应三个字节
        byteBuffer.put(str.getBytes());

        //当数据存放到 byteBuffer后，这时就需要 反转，将数据从ByteBuffer 写入到channel中
        byteBuffer.flip();

        //将ByteBuffer中的数据，写入到channel中
        fileChannel.write(byteBuffer);
        fileOutputStream.close();
    }
}

```



简单示意图：

![image](image/java-nio-channel-buffer-test-05.png)

可以看到已经将数据输出到文件file01.txt中

![image](image/java-nio-channel-buffer-test-01.png)



一个中文，按照UTF-8来，一个中文对应三个字节，这里数据占用的字节是12, 所以 到了12后面就是0了。

![image](image/java-nio-channel-buffer-test-02.png)

这时 position=13

![image](image/java-nio-channel-buffer-test-03.png)

当进行反转后，这个limit=13, position=0,   将数据从0开始 写道channlee中，并且不超过 limit限制。也是说从buffer中读数据不能超过这个限制。

![image](image/java-nio-channel-buffer-test-04.png)



#### 3.5.4、应用案例2-本地文件读数据

实例要求：

1)、使用前面学习后的ByteBuffer(缓冲)和FileChannel(通道)，将file01.txt文件中数据读入到程序，并显示台屏幕

2)、假定文件已经存在。

3)、代码示例如下

```java
package com.learn.simple.nio;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用ByteBuffer和FileChannel,将文件的数据读取出来
 *
 * @ClassName: NioFileChannel01
 * @Description:
 * @Author: lin
 * @Date: 2020/10/12 16:03
 * History:
 * @<version> 1.0
 */
public class NioFileChannel02 {
    public static void main(String[] args) throws Exception {

       //创建文件的输入流
        File file = new File("d:\\file01.txt");

        // 输入流
        FileInputStream fileInputStream = new FileInputStream(file);

        //获取channel， 通过fileInputStream获取对应的FileeChannel--->实际类型 FileChannelImpl
        FileChannel channel = fileInputStream.getChannel();

        //分配一个缓冲区, 这里根据文件的大小来分配缓冲区大小
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());

        //然后 从通道读取数据并放到缓冲区中
        channel.read(byteBuffer);

        //打印出数据
        System.out.println(new String(byteBuffer.array()));

        //关闭输入流
        fileInputStream.close();

    }
}

```

执行结果如下，可以看到已经将文件中的数据读取出来了。

![image](image/java-nio-channel-read-buffer-01.png)



#### 3.5.5、应用案例3---使用一个Buffer完成文件读取

实例要求：

1)、使用FileChannel(通道)和方法 read, write 完成文件的拷贝

2)、拷贝一个文本文件1.txt，放在目录下即可

3)、代码示例如下

```java
package com.learn.simple.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用一个Buffer完成文件读取
 *
 * @ClassName: NioFileChannel03
 * @Description:
 * @Author: lin
 * @Date: 2020/10/12 16:03
 * History:
 * @<version> 1.0
 */
public class NioFileChannel03 {
    public static void main(String[] args) throws Exception {

      //输入流，这里假定文件已经存在了
      FileInputStream fileInputStream = new FileInputStream("1.txt");
      //获取channel
      FileChannel fileChannel01 = fileInputStream.getChannel();

      //文件的拷贝，从1.txt---->2.txt
      FileOutputStream fileOutputStream = new FileOutputStream("2.txt");
      FileChannel fileChannel02 = fileOutputStream.getChannel();

      //创建一个byteBuffer用来存放数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);

        //循环读取数据，因为不知道文件中有多少数据
      while (true){
          //这里注意 很重要，不要忘记写这个
          //清空byteBuffer。如果不清空byteBuffer，那么在执行最后的写入操作后，
          // position的值和limit值相对，那么就会造成read=-1 不会进入，也不会退出循环
          // 从而造成了死循环

          /**
           * public final Buffer clear() {
           *         position = 0;
           *         limit = capacity;
           *         mark = -1;
           *         return this;
           *     }
           */
          byteBuffer.clear();

          //将通道中的数据读取到 byteBuffer中
          int read = fileChannel01.read(byteBuffer);
          System.out.println("read=:" + read);
          if(read == -1){
              //表示读取完
              break;
          }

          //反转，这里将position的值和limit的值进行处理
          byteBuffer.flip();
          // 然后将buffer中数据写入到另外的一个channel中， fileChannel02---->2.txt中
          fileChannel02.write(byteBuffer);

      }
      // 关闭相关流
        fileOutputStream.close();
        fileInputStream.close();
    }
}

```

可以看到这个文件已经拷贝了

![image](image/java-nio-channel-buffer-copy-file-01.png)



注意：在上述代码中要注意循环中的  byteBuffer.clear();方法 如果忘记了写，就会造成死循环，一直无法退出循环。在执行fileChannel02.write(byteBuffer); 过后 这个position就会更新 为和 limit相同的值。



#### 3.5.6、应用案例4---拷贝文件transferFrom方法

实例要求：

1)、使用FileChannel(通道)和方法transferForm，完成文件的拷贝

2)、拷贝一张图片

3)、代码示例如下

```java
package com.learn.simple.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * 使用一个transferFrom来拷贝文件
 *
 * @ClassName: NioFileChannel04
 * @Description:
 * @Author: lin
 * @Date: 2020/10/12 16:03
 * History:
 * @<version> 1.0
 */
public class NioFileChannel04 {
    public static void main(String[] args) throws Exception {

      //创建相关流
      FileInputStream fileInputStream = new FileInputStream("d:\\a.jpg");
      File file;
      FileOutputStream fileOutputStream = new FileOutputStream("d:\\a2.jpg");

      //获取channel
      FileChannel sourceCh = fileInputStream.getChannel();
      FileChannel destCh = fileOutputStream.getChannel();

      //使用transferForm完成拷贝, 将sourceCh的数据 从0开始拷贝 destCh中
      destCh.transferFrom(sourceCh, 0, sourceCh.size());

      //关闭通道和相关流
      sourceCh.close();
      destCh.close();
      fileInputStream.close();
      fileOutputStream.close();
    }
}

```



#### 3.5.7、关于Buffer和Channel的注意事项和细节

1)、ByteBuffer支持类型化的put和get，put放入的是什么数据类型，get就应该使用相应的数据类型来取出，否则可能有BufferUnderflowException异常。 【示例】

```java
package com.learn.simple.nio;

import java.nio.ByteBuffer;

/**
 * ByteBuffer支持类型化的put和get，put放入的是什么数据类型，get就应该使用相应的数据类型来取出，
 * 否则可能有BufferUnderflowException异常。
 * @ClassName: NioByteBufferPutGet
 * @Description:
 * @Author: lin
 * @Date: 2020/10/12 22:56
 * History:
 * @<version> 1.0
 */
public class NioByteBufferPutGet {
    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(64);
        //类型化方式放入数据
        byteBuffer.putInt(100);
        byteBuffer.putLong(9);
        byteBuffer.putChar('你');
        byteBuffer.putShort((short) 6);

        byteBuffer.flip();

        System.out.println("========");

        //更加放入的类型方式正确的获取
        //System.out.println(byteBuffer.getInt());
        //System.out.println(byteBuffer.getLong());
        //System.out.println(byteBuffer.getChar());
        //System.out.println(byteBuffer.getShort());


        /**
         * 打印结果
         * ========
         * 100
         * 9
         * 你
         * 6
         */


        // 每一个根据方式类型方式，获取数据
        //取数据的时候，没有根据放入的类型方式类，那么就会报错报错。
        System.out.println(byteBuffer.getShort());
        System.out.println(byteBuffer.getInt());
        System.out.println(byteBuffer.getLong());
        System.out.println(byteBuffer.getLong());

        /**
         * 打印结果
         * 就会报 这个错误。
         * Exception in thread "main" java.nio.BufferUnderflowException
         * 	at java.nio.Buffer.nextGetIndex(Buffer.java:506)
         * 	at java.nio.HeapByteBuffer.getLong(HeapByteBuffer.java:415)
         * 	at com.learn.netty.nio.NioByteBufferPutGet.main(NioByteBufferPutGet.java:50)
         */
    }
}

```

2)、可以将一个普通Buffer转成只读Buffer， 【举例说明】

```java
package com.learn.simple.nio;

import java.nio.ByteBuffer;

/**
 * @ClassName: ReadOnlyBuffer
 * @Description: 只读Buffer
 * @Author: lin
 * @Date: 2020/10/12 23:06
 * History:
 * @<version> 1.0
 */
public class ReadOnlyBuffer {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(64);

        int count = 64;
        for (int i = 0; i < count; i++) {
            buffer.put((byte) i);
        }

        //转换
        buffer.flip();

        //得到一个只读的buffer
        ByteBuffer readOnlyBuffer = buffer.asReadOnlyBuffer();
        System.out.println(readOnlyBuffer.getClass());

        while (readOnlyBuffer.hasRemaining()){
            System.out.println(readOnlyBuffer.get());
        }

        //一个只读buffer，不能再往里面写入数据

        //readOnlyBuffer.put((byte) 100);
        /**
         * 再往里面写入数据，就会报错
         * Exception in thread "main" java.nio.ReadOnlyBufferException
         * 	at java.nio.HeapByteBufferR.put(HeapByteBufferR.java:175)
         * 	at com.learn.netty.nio.ReadOnlyBuffer.main(ReadOnlyBuffer.java:35)
         */
    }
}

```



3)、NIO还提供了MappedByteBuffer,可以让文件直接在内存(堆外的内存)中进行修改，而如何同步到文件则有NIO来完成, 【举例说明】

```java
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
         *
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

```

修改后的结果 如下，可以看已经修改了文件

![image](image/java-nio-mapped-buffer-01.png)



注意：上述代码中如果修改的位置超过了规定的位置，那么在修改是就会报错索引越界错误

```
   // 因此这里修改下标为5的 数据，会报索引越界异常
        mappedByteBuffer.put(5, (byte) 'Y');
```

错误信息

```
Exception in thread "main" java.lang.IndexOutOfBoundsException
	at java.nio.Buffer.checkIndex(Buffer.java:540)
	at java.nio.DirectByteBuffer.put(DirectByteBuffer.java:305)
	at com.learn.netty.nio.MappedByteBufferTest.main(MappedByteBufferTest.java:44)

```





4)、前面的读写操作，都是通过一个Buffer完成的，NIO还支持 通过多个Buffer(即Buffer数组)完成读写操作，即Scattering和Gathering (分散和聚集)【举例说明】

 ```java
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

 ```



测试发送6个字节

![image](image/java-scattering-gathering-01.png)

打印结果

```
> Task :ScatteringAndGatheringTest.main()
byteRead=:6
position=5,limit=5
position=1,limit=3

```

发送8个字节

![image](image/java-scattering-gathering-02.png)



输出结果

```
> Task :ScatteringAndGatheringTest.main()
byteRead=:8
position=5,limit=5
position=3,limit=3
byteRead:=8 byteWrite=8,messageLength8
```

通过这个示例，其实表达是将数据写入或者放入到buffer时，可以采用buffer数组，依次写入(分散)，那么在读取的时候可以从 buffer数组中 依次读取(聚和)。 这个的好处是，以后在使用buffer的时候，如果一个buffer不够用那么可以使用buffer数组形式，让其操作更快捷。



### 3.6、Selector(选择器)

#### 3.6.1、基本介绍

1)、Java的NIO，用非阻塞的IO方式。可以用一个线程，处理多个的客户端连接，就会使用到$\textcolor{red}{Selector(选择器)}$

2)、$\textcolor{red}{Selector能够检测多个注册的通道上是否有事件发生(注意：多个Channel以事件的 方式可以注册到同一个Selector)}$，如果有事件发生，便获取事件然后针对每个事件进行相应的处理。这样就可以只用一个单线程去管理多个通道，也就是管理多个连接和请求

3)、只有在 连接/通道 真正有读写事件发生时，才会进行读写，就大大地减少了系统开销，并且不必为每个连接都创建一个线程，不用去维护多个线程

4)、避免了多个线程之间的上下文切换导致的开销

#### 3.6.2、Selector示意图和特点说明

![image](image/java-selector-01.png)

1)、Netty的IO线程NioEventLoop聚合了Selector(选择器，也叫多路复用器)，可以同时并发处理成百上千个客户端连接。

2)、当线程从某客户端Socket通道进行读写数据时，若没有数据可用时，该线程可用进行其它任务。

3)、线程通常将非阻塞IO的空闲时间用于在其它通道上执行IO操作，所以单独的线程可用管理多个输入和输出通道。

4)、由于读写操作都是非阻塞的，这就可用充分提升IO线程的运行效率，避免由于频繁I/O阻塞导致的线程挂起。

5)、一个I/O线程可以并发处理N个客户端连接和读写操作，这从根本上解决了传统阻塞I/O 一连接 一线程模型，架构的性能、弹性伸缩能力和可靠性都得到了极大的提升。

#### 3.6.3、Selector类相关方法

Selector类是一个抽象方法，常用的方法和说明如下：

```
public abstract class Selector implements Closeable{
  public static Selector open(); //得到一个选择器对象
  public int select(long timeout); //监控所有注册的通道，当其中有IO操作可以进行时，将对应的SelectionKey 加入到内部集合中并返回，参数用来设置超时时间
  
  public Set<SelectionKey> selectionKeys();//从内部集合中得到所有的SelectionKey
}
```

selector结构图：

![image](image/java-nio-selector-01.png)

上述的select(long timeout); 方法返回的是 SelectionKey的集合。 这里在实现类SelectiorImpl中可以看到。

![image](image/java-nio-selectorimpl-selectionkey-01.png)

然后进入SelectionKey类中查看, 可以知道这个 类 有一个 channel方法。

```
public abstract class SelectionKey {
/**
     * Constructs an instance of this class.
     */
    protected SelectionKey() { }

    public abstract SelectableChannel channel();

    public abstract Selector selector();
    
    public abstract boolean isValid();
     
    public abstract void cancel(); 
   }
    
```

#####   简单流程是： 

selector(选择器)是和 一个线程关联的， 关联之后selector调用 select方法， 这个方法返回 就会返回一个集合，返回的集合是SelectionKey的集合  这意味着 集合中有很多的SelectionKey。然后这个SelectionKey, 我们可以通过selector看到底是 那种事件发生了。是读操作、还是写操作、还是连接操作发生了呢？ 因为这个selectionKey已经拿到了，所以通过这个key 取到对应的channel在操作。







#### 3.6.4、注意事项

1)、NIO中的ServerSocketChannel功能类似ServerSocket, SocketChannel功能类似Socket

2)、Selector 相关方法说明

```
selector.select()//阻塞 ， 至少有一个事件发生才会返回，否就阻塞
selector.select(1000)//阻塞1000毫秒，在1000毫秒后返回
selector.wakeup()//唤醒selector
selector.selectNow()//不阻塞，立马返还
```



### 3.7、NIO非阻塞网络编程原理分析图

NIO非阻塞 网络编程相关的(Selector、SelectionKey、ServerSocketChannel和 SocketChannel)关系梳理图

![image](image/java-selector-02.png)

$\textcolor{red}{服务器端，也是有Buffer的}$



对上图的说明：

1)、当客户端连接是，会通过ServerSocketChannel得到SocketChannel

2)、Selector进行监听 select方法，返回有事件发生的通道的个数。

3)、将SocketChannel注册到Selector上， 

```
//这个方法在SelectabelChannel类中
public final SelectionKey  register(Selector sel, int ops)

//ops 有下面这四种， 表示关注的事件
public static final int OP_READ = 1 << 0;

public static final int OP_WRITE = 1 << 2;

//这个表示事件已经建成了。 连接已经创建好了
public static final int OP_CONNECT = 1 << 3;
//这个表示一个新的连接来了。
public static final int OP_ACCEPT = 1 << 4;

```

 一个selector 上可以注册多个SocketChannel

4)、注册后返回一个SelectionKey，会和该Selector关联(集合)

5)、进一步得到各个SelectionKey(有事件发生)

6)、通过SelectionKey 反向获取 SocketChannel，方法channel()

7)、可以通过得到channel 完成业务处理



### 3.8、NIO非阻塞 网络编程快速入门

  实例要求：

1)、编写一个NIO入门案例，实现服务器端和客户端之间的数据简单通讯(非阻塞)

2)、目的：理解NIO非阻塞网络编程机制

3)、代码示例如下

 服务端

```java
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

```

客户端示例：

```java
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

```

测试启动服务端 ，然后再启动客户端, 这里启动了两个客户端，可以看到不同的客户端对应的socketChannel不一样。同时可知每次来一个客户端就会产生一个channel。

![image](image/java-nio-selector-03.png)

注意如果在服务端中，循环中    if(key.isAcceptable())  判断中没有将socketChannel设置为非阻塞，就会异常

![image](image/java-nio-socketchannel-01.png)



### 3.9、SelectionKey

1)、SelectionKey，表示Selector和网络通道的注册关系，共四种：

```
int OP_ACCP:有新的网络连接可以 accept, 值为16
int OP_CONNECT:代表连接已经建立, 值为8
int OP_READ:代表读操作, 值为1
int OP_WRITE:代表写操作, 值为

源码中
public static final int OP_READ = 1 << 0;

public static final int OP_WRITE = 1 << 2;

//这个表示事件已经建成了。 连接已经创建好了
public static final int OP_CONNECT = 1 << 3;
//这个表示一个新的连接来了。
public static final int OP_ACCEPT = 1 << 4;
```

2)、SelectionKey相关方法

```java
public abstract class SelectionKey {
    /**
       得到与之关联的selector对象
     */
     public abstract Selector selector();
   
    /**
       得到与之关联的通道
     */
    public abstract SelectableChannel channel();
   
   /**
       设置或改变监听事件
     */  
    public abstract SelectionKey interestOps(int ops);

  
     /**
       是否可读
     */
    public final boolean isReadable() {
        return (readyOps() & OP_READ) != 0;
    }

    /**
       是否可写
     */
    public final boolean isWritable() {
        return (readyOps() & OP_WRITE) != 0;
    }

    
    public final boolean isConnectable() {
        return (readyOps() & OP_CONNECT) != 0;
    }
     /**
       是否可accept
     */
    public final boolean isAcceptable() {
        return (readyOps() & OP_ACCEPT) != 0;
    }

     /**
       得到与之关联的共享数据
     */
    public final Object attachment() {
        return attachment;
    }

}

```

![image](image/java-selectionkey-01.png)



### 3.10、ServerSocketChannel

1)、ServerSocketChannel在服务器端监听新的客户端Socket连接

2)、相关方法如下

```java
public abstract class ServerSocketChannel
    extends AbstractSelectableChannel
    implements NetworkChannel
{
    //得到一个ServerSocketChannel通道
    public static ServerSocketChannel open() throws IOException {
        return SelectorProvider.provider().openServerSocketChannel();
    }
    
    //设置服务器端口号
    public final ServerSocketChannel bind(SocketAddress local)
        throws IOException
    {
        return bind(local, 0);
    }
    
    
    //设置阻塞或非阻塞模式，取值false表示采用非阻塞模式
    public final SelectableChannel configureBlocking(boolean block);
    
    //注册一个选择器并设置监听事件
    public final SelectionKey register(Selector sel, int ops);
}
```



ServerSocketChannel和SocketChannel 同样继承了AbstractSelectableChannel 和实现了 NetworkChannel接口。但是SocketChannel 实现了更多的接口类。

```java
public abstract class ServerSocketChannel extends AbstractSelectableChannel
    implements NetworkChannel{}


public abstract class SocketChannel
    extends AbstractSelectableChannel
    implements ByteChannel, ScatteringByteChannel, GatheringByteChannel, NetworkChannel
{}
```

### 3.11、SocketChannel

1)、 SocketChannel，网络IO通道，具体负责进行读写操作。NIO把缓冲区的数据写入通道，或者把通道里的数据读到缓冲区。

2)、相关方法如下

```java
public abstract class SocketChannel
    extends AbstractSelectableChannel
    implements ByteChannel, ScatteringByteChannel, GatheringByteChannel, NetworkChannel
{
    //得到一个SocketChannel通道
    public static SocketChannel open();
 
    //设置阻塞或非阻塞模式，取值false表示采用非阻塞模式
    public final SelectableChannel configureBlocking(boolean block);
    
    //连接服务器
    public boolean connect(SocketAddress remote);
    //如果上面的方法连接失败，接下来就要通过该方法完成连接操作
    public boolean finishConnect();
    
    //往通道里写数据
    public int write(ByteBuffer src);
    //从通道里读数据
    public int read(ByteBuffer dst);
    
    //注册一个选择器并设置监听，最后一个参数可以设置共享数据
    public final SelectionKey register(Selector sel, int ops, Object att);
    
    //关闭通道
    public final void close();
}
```



### 3.12、NIO网络编程应用实例-群聊系统

实例要求：

1)、编写一个NIO群聊系统，实现服务器端和客户端之间的数据简单通讯(非阻塞)

2)、实现多人群聊

3)、服务器端：可以监测用户上线，离线，并实现消息转发功能

4)、客户端：通过channel可以无阻塞发送消息给其它所有用户，同时可以接受其它用户发送的消息(由服务器转发得到)

5)、目的：进一步理解NIO非阻塞网络编程机制

6)、示意图和代码示例

![image](image/java-selector-02.png)



编写程序步骤：

```
1、先编写服务器端
 1.1、服务器启动并监听6667
 1.2、服务器介绍客户端信息，并实现转发 [处理上线和离线]
2、编写客户端
 2.1、连接服务器
 2.2、发送消息
 2.3、接收服务器消息
```



服务端

```java
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

```

客户端

```java
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
        //  System.in读取标准输入设备数据（从标准输入获取数据，一般是键盘）
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

```



测试, 启动服务端，并启动两个客户端

![image](image/java-nio-selector-groupchat-01.png)

客户端发送消息 ，服务端接收到消息，其它的客户端也接收到了消息。 

![image](image/java-nio-selector-groupchat-client-01.png)

当客户端离线后，服务端接收到了 客户端离线的消息

![image](image/java-nio-selector-groupchat-client-02.png)



$\textcolor{red}{注意，要深刻理解上述代码的流程。}$



### 3.13、零拷贝原理剖析

####  3.13.1、零拷贝基本介绍

 1) 零拷贝是网络编程的关键，很多性能优化都离不开。 

 2) 在 Java 程序中，常用的零拷贝有 mmap(内存映射) 和 sendFile。那么，他们在 OS 里，到底是怎么样的一个 

 的设计？我们分析 mmap 和 sendFile 这两个零拷贝 

 3) 另外我们看下 NIO 中如何使用零拷贝



#### 3.13.2、传统的IO数据读写

Java 传统 IO 和 网络编程的一段代码

```
//创建一个文件
File file = new File("test.txt");
//获取到一个RandomAccessFile对象
RandomAccessFile raf = new RandomAccessFile(file,"rw");

//声明一个byte字节数组
byte[] arr = new byte[(int) file.lenght()];
//将文件 读入到 byte字节数组中
raf.write(arr);

//然后一个serverSocket监听连接，
Socket socket = new ServerSocket(8080).accpet();
//通过socket得到 一个输出流对象，然后将这个字节数组 写入到流对象中
socket.getOutputStream().write(arr);
```





#### 3.13.3、传统的IO模型

那么上面的一个简单 的文件读写过程，一共发生了多少次文件拷贝，以及用户态和内核态状态的切换。首先看一张图

![image](image/java-nio-zero-copy-01.png)

首先把硬件上的数据进行一共DMA拷贝，那么DMA是什么呢？ 

DMA: direct  memory access 直接内存拷贝(不使用cpu来完成的)。

上面的代码使用了一个read方法，

1)、这个方法首先将把硬盘上的数据 通过DMA拷贝 ,拷贝到内核(kernel buffer)。

2)、然后将这个内核buffer(kernel buffer) 使用cpu copy, 拷贝到 用户buffer( user buffer)。那么我们的数据其实是在用户buffer进行修改。

3)、在用户buffer修改完数据后 再使用 cpu copy，拷贝到socket buffer(也就是准备发送的那个buffer)。

4)、然后再用DMA拷贝，将数据拷贝到  protocol engine( 协议引擎 及协议栈)。 



上述传统的IO 一共经过了 4次拷贝，用户态和内核态切换 经过了三次切换。在传统的IO模型中拷贝的次数是非常读的。只是进行了一次 读 和写 然后经过了4次拷贝 和3次状态的切换 ，显然代价是比较高的。



下面文章参考

https://juejin.im/post/6844903949359644680#heading-13

https://www.cnblogs.com/rickiyang/p/13265043.html



#### 3.13.4、mmap 优化

因为传统的IO模型 在进行读写的时候 会经过 4次拷贝 和3次状态的切换 ，代价比较高，所以就有了其它方法的优化。 叫做 mmap(内存映射优化)

1) mmap 通过内存映射，将**文件映射到内核缓冲区**，同时，**用户空间可以共享内核空间的数据**。这样，在进行网 

络传输时，就可以减少内核空间到用户空间的拷贝次数。如下图 

2) mmap 示意图

![image](image/java-nio-zero-copy-mmap-01.png)

那么使用了内存映射优化技术有什么变化呢？

1)、这个方法首先将把硬盘上的数据 通过DMA拷贝 ,拷贝到内核(kernel buffer)。

2)、因为$\textcolor{red}{共享内核空间数据}$，所以$\textcolor{red}{kenel buffer 到 user buffer 就不会进行cpu拷贝了}$。数据就可以在kernel buffer进行修改。

3)、在kernel buffer修改完数据后 再使用 cpu copy，拷贝到socket buffer

4)、然后通过socket buffer 再使用DMA拷贝，将数据拷贝到  protocol engine( 协议引擎 及协议栈)。 



mmap 优化过后，拷贝次数减少了1次，但是状态变化还是3次。 因此mmap不是真正的零拷贝，不过的确减少了拷贝次数。 所以又提出其它的优化技术



#### 3.13.5、sendFile 优化 

1) Linux 2.1 版本 提供了 sendFile 函数，其基本原理如下：数据根本不经过用户态，直接从内核缓冲区进入到 

Socket Buffer，同时，由于和用户态完全无关，就减少了一次上下文切换 

2) 示意图和小结



从而再一次减少了数据拷贝。具体如下图和小结

![image](image/java-nio-zero-copy-sendfile-01.png)

3) 提示：零拷贝从操作系统角度，是没有 cpu 拷贝 





使用sendFile优化

(1)、这个方法首先将把硬盘上的数据 通过DMA拷贝 ,拷贝到内核(kernel buffer)。

(2)、数据根本不经过用户态，直接进入socket buffer，所以减少了拷贝次数，并且减少了状态切换次数

(3)、然后通过socket buffer 再使用DMA拷贝，将数据拷贝到  protocol engine( 协议引擎 及协议栈)。 



sendFile 优化方式比mmap 好在，拷贝次数由4次 减少到了3次拷贝，状态切换由3次 减少到了两次。但是linux 2.1这种方式仍然没有实现零拷贝。 因为里面还是进行一次cpu 拷贝。

$\textcolor{red}{注意：所谓的零拷贝不是指不拷贝，而是没有 Cpu  Copy}$



#### 3.13.7、sendFile优化2

1) Linux 在 2.4 版本中，做了一些修改，避免了从**内核缓冲区**拷贝到 **Socket buffer** 的操作，直接拷贝到协议栈， 

2) 在kernel buffer 到 socket buffer 这里其实还是有 一次 cpu 拷贝 ,

kernel buffer -> socket buffer 但是，拷贝的信息很少(拷贝的是一些描述信息)，比如 lenght , offset , 消耗低，可以忽略

![image](image/java-nio-zero-copy-sendfile-02.png)



拷贝次数从4次减少到了 2次，  数据在kerner buffer 修改后，然后直接通过DMA copy 拷贝到 协议栈中。

状态切换次数 由3次 减少到了 2次。



#### 3.13.8、零拷贝的再次理解

1) 所谓零拷贝，是从**操作系统的角度**来说的。因为内核缓冲区之间，没有数据是重复的（只有 kernel buffer 有 

一份数据, 在运行过程中，内存只有一份buffer，并且不重复）。 

2) 零拷贝不仅仅带来更少的数据复制，还能带来其他的性能优势，例如更少的上下文切换，更少的 CPU 缓存伪 

共享以及无 CPU 校验和计算。 

#### 3.13.9、mmap和 sendFile 区别

1) mmap 适合小数据量读写，sendFile 适合大文件传输。 

2) mmap 需要 4 次上下文切换，3 次数据拷贝；sendFile 需要 3 次上下文切换(这里看怎么计算根据上面的第一个图计算，如果从最初状态开始计算也就是用户态，那么就算4次； 如果只算中间的那么就只有3次切换)，最少 2 次数据拷贝。 

3) sendFile 可以利用 DMA 方式，减少 CPU 拷贝，mmap 则不能（必须从内核拷贝到 Socket 缓冲区）。 

$\textcolor{red}{在这个选择上：rocketMQ 在消费消息时，使用了 mmap。kafka 使用了 sendFile。}$

```
1、输入流（Input  Stream）:
  程序从输入流读取数据源。数据源包括外界(键盘、文件、网络…)，即是将数据源读入到程序的通信通道
2、输出流：
   程序向输出流写入数据。将程序中的数据输出到外界（显示器、打印机、文件、网络…）的通信通道。
```





#### 3.13.10、NIO零拷贝案例

案例要求： 

1) 使用传统的 IO 方法传递一个大文件 

2) 使用 NIO 零拷贝方式传递(transferTo)一个大文件 

3) 看看两种传递方式耗时时间分别是多少

首先看 传统的方式

```java
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

```

传统的客户端

```java
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

```

传统方式测试耗时是：

```
> Task :OldIoClient.main()
发送总字节数： 1007473, 耗时： 6
```







NIO 方式的 服务端

```java
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

```



NIO客户端

```java
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

```

使用NIO方式 传输耗时

```
> Task :NewIoClient.main()
 发 送 的 总 的 字 节 数 =1007473 耗 时 :2
```



### 3.14、Java AIO基本介绍

1)、 JDK 7 引入了 Asynchronous I/O，即 AIO。在进行 I/O 编程中，常用到两种模式：$\textcolor{red}{Reactor(反应器模式) 和 Proactor(主动器模式)}$。Java 的 NIO 就是 Reactor，当有事件触发时，服务器端得到通知，进行相应的处理 

2)、 AIO 即 NIO2.0，叫做异步不阻塞的 IO。AIO 引入异步通道的概念，采用了 Proactor 模式，简化了程序编写。 有效的请求才启动线程，它的特点是先由操作系统完成后才通知服务端程序启动线程去处理，一般适用于连接 数较多且连接时间较长的应用

3) 、目前 AIO 还没有广泛应用，$\textcolor{red}{Netty 也是基于 NIO, 而不是 AIO}$， 因此我们就不详解 AIO 了，有兴趣的同学可 

以 参 考 <<Java 新 一 代 网 络 编 程 模 型 AIO 原 理 及 Linux 系 统 AIO 介 绍 >> 

http://www.52im.net/thread-306-1-1.html 



3.15、BIO、NIO、AIO对比

|          | BIO      | NIO                  | AIO        |
| -------- | -------- | -------------------- | ---------- |
| IO模型   | 同步阻塞 | 同步非阻塞(多路复用) | 异步非阻塞 |
| 编程难度 | 简单     | 复杂                 | 复杂       |
| 可靠性   | 差       | 好                   | 好         |
| 吞吐量   | 低       | 高                   | 高         |

举例说明：

```
1)、同步阻塞：到理发店理发，就一直等待理发师，直到轮到自己理发。
2)、同步非阻塞：到理发店理发，发现前面有其它人在理发，然后给理发师说下，先干其它事情，等会儿过来看是否轮到自己。
3)、异步非阻塞：给理发店打电话，让理发师上门服务，自己干其它事情，理发师到家了来给你理发。
```



### 4、Netty概述

####  4.1、原始NIO存在的问题

1) NIO 的类库和 API 繁杂，使用麻烦：需要熟练掌握 Selector、ServerSocketChannel、SocketChannel、ByteBuffer 等。 

2) 需要具备其他的额外技能：$\textcolor{red}{要熟悉 Java 多线程编程，因为 NIO 编程涉及到 Reactor 模式，你必须对多线程  和网络编程非常熟悉，才能编写出高质量的 NIO 程序。}$ 

3) 开发工作量和难度都非常大：例如客户端面临断连重连、网络闪断、半包读写、失败缓存、网络拥塞和异常流 

的处理等等。 

4) JDK NIO 的 Bug：例如臭名昭著的 Epoll Bug，它会导致 Selector 空轮询，最终导致 CPU 100%。直到 JDK 1.7 

版本该问题仍旧存在，没有被根本解决。 

#### 4.2、 Netty 官网说明

官网：https://netty.io/ 

Netty is an asynchronous event-driven network application framework for rapid development of maintainable high performance protocol servers & clients

![image](image/java-netty-framework-01.png)

分为三个部分： 

a、core核心部分：零拷贝、交互api、可扩展的事件模型

b、支持的协议：HTTP/WebSocket 、 SSL、 zlib\gzip、 protobuf(这个是用来解码和编码的)、大文件传输等等。

c、支持的传输服务：socket 、 HTTP tunnel 、in-vm



Netty官网说明：

(1)、Netty是由jboss提供的一个java开源框架。Netty提供异步的、基于事件驱动的网络应用程序框架，用以快速开发高性能、高可靠性的网络IO程序。

(2)、Netty可以帮助你快速、简单的开发出一个网络应用、相当于简化和流程化了NIO的开发过程

(3)、Netty是目前最流行的NIO框架，Netty在互联网领域、大数据分布式计算领域、游戏行业、通信行业等获得了广泛的应用，知名的Elasticsearch、Dubbo框架内部都采用了Netty。



#### 4.3、Netty的优点

Netty 对 JDK 自带的 NIO 的 API 进行了封装，解决了上述问题。 

1)、 设计优雅：适用于各种传输类型的统一 API 阻塞和非阻塞 Socket；基于灵活且可扩展的事件模型，可以清晰 

地分离关注点；高度可定制的线程模型 - 单线程，一个或多个线程池. 

2)、 使用方便：详细记录的 Javadoc，用户指南和示例；没有其他依赖项，JDK 5（Netty 3.x）或 6（Netty 4.x）就 

足够了。 

3)、 高性能、吞吐量更高：延迟更低；减少资源消耗；最小化不必要的内存复制。 

4)、 安全：完整的 SSL/TLS 和 StartTLS 支持。 

5)、 社区活跃、不断更新：社区活跃，版本迭代周期短，发现的 Bug 可以被及时修复，同时，更多的新功能会被 

加入 

#### 4.5、Netty版本说明

1) netty 版本分为 netty3.x 和 netty4.x、netty5.x 

2) 因为 Netty5 出现重大 bug，已经被官网废弃了，目前推荐使用的是 Netty4.x 的稳定版本 

3) 目前在官网可下载的版本 netty3.x netty4.0.x 和 netty4.1.x 

4) 这里使用的是 Netty4.1.x 版本 

5) netty 下载地址： https://bintray.com/netty/downloads/netty/



### 5、Netty 高性能架构设计

#### 5.1、 线程模型基本介绍

1)、 不同的线程模式，对程序的性能有很大影响，为了搞清 Netty 线程模式，我们来系统的讲解下 各个线程模式，最后看看 Netty 线程模型有什么优越性。

2)、 目前存在的线程模型有： $\textcolor{red}{传统阻塞 I/O 服务模型}$ ， $\textcolor{red}{ Reactor 模式}$ 。

3)、 根据 Reactor 的数量和处理资源池线程的数量不同，有 3 种典型的实现 

$\textcolor{red}{单 Reactor 单线程}$； 

$\textcolor{red}{单 Reactor 多线程}$； 

$\textcolor{red}{主从 Reactor 多线程}$；

4)、 Netty 线程模式($\textcolor{red}{Netty 主要基于主从 Reactor 多线程模型做了一定的改进，其中主从 Reactor 多线程模型有多 个 Reactor}$) 



#### 5.2、 传统阻塞 I/O 服务模型 

##### 5.2.1、工作原理图 

1) 黄色的框表示对象， 蓝色的框表示线程 

2) 有背景颜色的三个的框表示方法(API) 

![image](image/java-traditional-io-01.png)

##### 5.2.2、模型特点 

1) 采用阻塞 IO 模式获取输入的数据 

2) 每个连接都需要独立的线程完成数据的输入，业务处理, 数据返回

##### 5.2.3、问题分析 

1) 当并发数很大，就会创建大量的线程，占用很大系统资源 

2) 连接创建后，如果当前线程暂时没有数据可读，该线程会阻塞在 read 操作，造成线程资源浪费 



#### 5.3、Reactor模式

##### 5.3.1、针对传统阻塞 I/O 服务模型的 2 个缺点，解决方案：

1) 、基于 I/O 复用模型：多个连接共用一个阻塞对象，应用程序只需要在一个阻塞对象等待，无需阻塞等待所有连 接。当某个连接有新的数据可以处理时，操作系统通知应用程序，线程从阻塞状态返回，开始进行业务处理 。

这里的回到就是解决上面图中handler阻塞对象, 因为在客户端没有数据发送过来时 read方法就会阻塞。因此这个handler就是一个阻塞对象。基于这个原因 Reactor 采用了I/O 复用模型来处理。

Reactor 对应的叫法: 

​    a.反应器模式   

​    b.分发者模式(Dispatcher)  

​    c.通知者模式(notifier)

2)、 基于线程池复用线程资源：不必再为每个连接创建线程，将连接完成后的业务处理任务分配给线程进行处理， 一个线程可以处理多个连接的业务。



![image](image/java-reactor-multiplexer-01.png)

这张图只是解释了reactor的基本原理：

 a、serviceHandle只有一个，相比传统的i/o模型，这里使用一个ServiceHandler来接收请求，这样就可以实现复用。不必每次来一个请求就创建一个handler阻塞对象(只有没有数据读取都要阻塞)。

b、基于线程池 复用资源，不必为每个连接创建线程，将连接后的业务处理分配给线程进行处理，一个线程可以处理多个业务。因为是线程池，所以当线程池中有空闲的线程时，serviceHandler就会将 业务处理分配给空闲的线程去处理。 这样就解决了前面 传统IO模型中 每个连接需要独立的线程完成数据的输入，业务处理和数据返回。

就达到了 线程的复用。



##### 5.3.2、I/O 复用结合线程池，就是 Reactor 模式基本设计思想，如图  

说明：

1)、 Reactor 模式，通过一个或多个输入同时传递给服务处理器的模式(基于事件驱动) 

2) 、服务器端程序处理传入的多个请求,并将它们同步分派到相应的处理线程， 因此 Reactor 模式也叫 Dispatcher 模式 

3) 、Reactor 模式使用 IO 复用监听事件, 收到事件后，分发给某个线程(进程), 这点就是网络服务器高并发处理关键

![image](image/java-reactor-01.png)



##### 5.3.3、Reactor 模式中 核心组成：

1) 、Reactor：Reactor 在一个单独的线程中运行，负责监听和分发事件，分发给适当的处理程序来对 IO 事件做出 反应。 它就像公司的电话接线员，它接听来自客户的电话并将线路转移到适当的联系人； 

2) 、Handlers：处理程序执行 I/O 事件要完成的实际事件，类似于客户想要与之交谈的公司中的实际官员。Reactor 通过调度适当的处理程序来响应 I/O 事件，处理程序执行非阻塞操作



##### 5.3.4、模式分类

根据 Reactor 的数量和处理资源池线程的数量不同，有 3 种典型的实现

1)、单 Reactor 单线程 

2)、单 Reactor 多线程 

3)、主从 Reactor 多线程



#### 5.4、单Reactor 单线程

原理图，并使用 NIO 群聊系统验证

![image](image/java-reactor-single-thread-01.png)

对于上述图，在NIO群聊系统中我们可以 知道，NioServer中调用select方法 就是上面Reactor中的select，因此我们可以认为selector是一个选择器，就充当了Reactor的角色。  对于dispacth ，当发现请求是一个连接请求，就调用accept方法。当连接请求完了之后 把得到的socketChannel注册到selector中。那么下一次再去获取一个事件 就应该是一个读的事件。这个读事件 调用的是readData()方法。 对照这个图相当于是调用一个handler的read方法。所以我们在群聊系统中，可以将readData()方法 sendInfoToOtherClients() 方法 封装到一个Handler中去。

那么在listen方法中，就调用handler中的 read方法就可以了。



使用群聊系统测试： 启动群聊系统服务端，然后客户端启动3个。 并添加打印日志，可以看到在监听是时候 线程是main线程，然后进行消息转发的时候，线程同样是main线程。

![image](image/java-reactor-single-thread-02.png)

这种方式在客户端很多的情况下，势必会造成阻塞。



##### 5.4.1、方案说明：

1)、Select 是前面 I/O 复用模型介绍的标准网络编程 API，可以实现应用程序通过一个阻塞对象监听多路连接请求 

2)、Reactor 对象通过 Select 监控客户端请求事件，收到事件后通过 Dispatch 进行分发 

3)、如果是建立连接请求事件，则由 Acceptor 通过 Accept 处理连接请求，然后创建一个 Handler 对象处理连接 

完成后的后续业务处理 

4)、如果不是建立连接事件，则 Reactor 会分发调用连接对应的 Handler 来响应 

5)、Handler 会完成 Read→业务处理→Send 的完整业务流程 

结合实例：$\textcolor{red}{服务器端用一个线程通过多路复用搞定所有的 IO 操作（包括连接，读、写等）}$，编码简单，清晰明了，$\textcolor{red}{ 但是如果客户端连接数量较多，将无法支撑}$，前面的 NIO 群聊案例就属于这种模型。



##### 5.4.2、方案优缺点分析：

1)、优点：模型简单，没有多线程、进程通信、竞争的问题，全部都在一个线程中完成 

2)、缺点：性能问题，只有一个线程，无法完全发挥多核 CPU 的性能。Handler 在处理某个连接上的业务时，整 

个进程无法处理其他连接事件，很容易导致性能瓶颈 

3)、缺点：可靠性问题，线程意外终止，或者进入死循环，会导致整个系统通信模块不可用，不能接收和处理外部 

消息，造成节点故障 

4)、使用场景：客户端的数量有限，业务处理非常快速，比如 Redis 在业务处理的时间复杂度 O(1) 的情况 



#### 5.5、单 Reactor 多线程

##### 5.5.1、原理图

![image](image/java-single-reactor-multithreading-01.png)

##### 5.5.2、对上图的小结

1)、Reactor 对象通过 select 监控客户端请求 事件, 收到事件后，通过 dispatch 进行分发 

2)、如果建立连接请求, 则右 Acceptor 通过 accept 处理连接请求, 然后创建一个 Handler 对象处理完成连接后的各种事件 

3)、如果不是连接请求，则由 reactor 分发调用连接对应的 handler 来处理

4)、handler 只负责响应事件，不做具体的业务处理, 通过 read 读取数据后，会分发给后面的 worker 线程池的某个 线程处理业务 

5)、worker 线程池会分配独立线程完成真正的业务，并将结果返回给 handler 

6)、handler 收到响应后，通过 send 将结果返回给 client 

##### 5.5.3、方案优缺点分析

1)、优点：可以充分的利用多核 cpu 的处理能力 

2)、缺点：多线程数据共享和访问比较复杂， reactor 处理所有的事件的监听和响应，在单线程运行， 在高并发场 景容易出现性能瓶颈



#### 5.6、主从Reactor 多线程

##### 5.6.1、工作原理图

针对单 Reactor 多线程模型中，Reactor 在单线程中运行，高并发场景下容易成为性能瓶颈，可以让 Reactor 在 

多线程中运行

![image](image/java-main-slave-reactor-multithreading-01.png)

##### 5.6.2、上图的方案说明

1)、Reactor 主线程 MainReactor 对象通过 select 监听连接事件, 收到事件后，通过 Acceptor 处理连接事件 

2)、当 Acceptor 处理连接事件后，MainReactor 将连接分配给 SubReactor(subReactor有多个，在分配的时候有可能第一次分配给subReactor，下一次分配给subReactor2)。

3)、subreactor 将连接加入到连接队列进行监听(监听客户端是 read 还是 write操作),并创建 handler 进行各种事件处理 

4)、当有新事件发生时， subreactor 就会调用对应的 handler 处理 

5)、handler 通过 read 读取数据，将数据分发给后面的 worker 线程池来处理 

6)、worker 线程池分配独立的 worker 线程进行业务处理，并返回结果

7)、handler 收到响应的结果后，再通过 send 将结果返回给 client 

8)、$\textcolor{red}{Reactor 主线程}$可以对应$\textcolor{red}{多个 Reactor 子线程}$, 即 MainRecator 可以关联多个 SubReactor

##### 5.6.3、Scalable IO in Java 对 Multiple Reactors 的原理图解

![image](image/java-scalable-io-in-java-multiple-reactors-01.png)

##### 5.6.4、主从Reactor多线程 方案优缺点说明：

1)、优点：父线程与子线程的数据交互简单职责明确，父线程只需要接收新连接，子线程完成后续的业务处理。 

2)、优点：父线程与子线程的数据交互简单，Reactor 主线程只需要把新连接传给子线程，子线程无需返回数据。 

3)、缺点：编程复杂度较高 

4)、结合实例：这种模型在许多项目中广泛使用，包括 Nginx 主从 Reactor 多进程模型，Memcached 主从多线程， Netty 主从多线程模型的支持 

#### 5.7 Reactor 模式小结

##### 5.7.1、3 种模式用生活案例来理解

1)、单 Reactor 单线程，前台接待员和服务员是同一个人，全程为顾客服 

2)、单 Reactor 多线程，1 个前台接待员，多个服务员，接待员只负责接待 

3)、主从 Reactor 多线程，多个前台接待员，多个服务生 

##### 5.7.2 Reactor 模式具有如下的优点：

1)、响应快，不必为单个同步时间所阻塞，虽然 Reactor 本身依然是同步的 

2)、可以最大程度的避免复杂的多线程及同步问题，并且避免了多线程/进程的切换开销 

3)、扩展性好，可以方便的通过增加 Reactor 实例个数来充分利用 CPU 资源 

4)、复用性好，Reactor 模型本身与具体事件处理逻辑无关，具有很高的复用性 



#### 5.8、Netty模型

##### 5.8.1、工作原理示意图 1-简单版

Netty 主要基于主从 Reactors 多线程模型（如图）做了一定的改进，其中主从 Reactor 多线程模型有多个 Reactor

![image](image/java-netty-simple-01.png)



##### 5.8.2、对上图说明 

1)、BossGroup 线程维护 Selector , 只关注 Accecpt 。

2)、当接BossGroup中 收到 Accept 事件，然后获取到对应的 SocketChannel后, 将其封装成 NIOScoketChannel 并注册到 WorkerGroup线程(事件循 环)中 的selector中, 并进行维护 

3)、当 Worker 线程监听到 selector 中通道发生自己感兴趣的事件后，就由handler进行处理完成， 注意 handler会事先加入到通道 中。



##### 5.8.3、工作原理示意图 2-进阶版

![image](image/java-netty-advanced-01.png)

a、BossGroup在实际的netty运行中它可以是多个线程，每个线程对应的是NioEventLoop(事件循环)。

b、每个NioEventLoop会对应一个selector，这个selector循环监听I/O事件。循环到了就处理。

c、处理过后再处理任务队列。

d、BossGroup可以有多个NioEventLoop，当BossGroup监听到连接事件后，创建socket。然后把这个封装好的NiosocketChannel注册到 WorkerGroup中的 某一个selector中，在注册前会先进行selector的选择。

e、WorkerGroup也有多个NioEventLoop，每个事件循环中对应一个线程，也维护了一个selector。selector也在哪里不停的监听事件。



##### 5.8.4、工作原理示意图-详细版

![image](image/java-netty-detailed-01.png)

##### 5.8.5、对上图的说明小结 

1)、Netty 抽象出两组线程池 BossGroup 专门负责接收客户端的连接, WorkerGroup 专门负责网络的读写 

2)、BossGroup 和 WorkerGroup 类型都是 NioEventLoopGroup 

3)、NioEventLoopGroup 相当于一个事件循环组, 这个组中含有多个事件循环 ，每一个事件循环是NioEventLoop 

4)、$\textcolor{red}{NioEventLoop }$表示一个不断循环的执行处理任务的线程， $\textcolor{red}{每个 NioEventLoop 都有一个 selector }$, 用于监听绑 定在其上的 socket 的网络通讯 

5)、NioEventLoopGroup 可以有多个线程, 即可以含有多个 NioEventLoop 

6)、每个 Boss下 NioEventLoop 循环执行的步骤有 3 步

   a. 第一步：轮询 accept 事件 。

   b. 第二步：处理 accept 事件 , 与 client 建立连接 , 生成 NioScocketChannel , 并将其注册到某个 worker NIOEventLoop   上 的 selector 。

  c. 第三步：处理任务队列的任务 ， 即 runAllTasks 。

7)、每个 Worker 下面的 NIOEventLoop 循环执行的步骤 

​    a. 轮询 read, write 事件 

​    b.(对应worker下的 NioEventLoop只关心 read和 wirte事件) 处理 i/o 事件， 即 read , write 事件，在对应 NioScocketChannel 处理 

​    c.处理任务队列的任务 ， 即 runAllTasks 

8)、每个Worker NIOEventLoop 处理业务时，会使用pipeline(管道), pipeline 中包含了 channel , 即通过pipeline 

可以获取到对应通道, 管道中维护了很多的 处理器。然后可以用这个处理器来进行各种数据的处理。



##### 5.8.6、Netty 快速入门实例-TCP 服务

实例要求：使用 IDEA 创建 Netty 项目 

1)、Netty 服务器在 6668 端口监听，客户端能发送消息给服务器 "hello, 服务器~" 

2)、服务器可以回复消息给客户端 "hello, 客户端~" 

3)、目的：对 Netty 线程模型 有一个初步认识, 便于理解 Netty 模型理论 

4)、代码演示



 服务端代码:

```java
package com.learn.simple.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 *  Netty入门实例 ---- 服务端
 * @ClassName: NettyServer
 * @Description: Netty入门实例
 * @Author: lin
 * @Date: 2020/10/16 8:59
 * History:
 * @<version> 1.0
 */
public class NettyServer {
    public static void main(String[] args)throws Exception {

        //创建BossGroup 和 WorkerGroup
        //说明：
        //1、创建两个线程组 bossGroup和WorkerGroup
        //2、bossGroup只处理连接请求，真正的和客户端进行业务处理的，会交给WorkerGroup来完成
        //3、这两个都是无限循环。
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {


        //4、创建服务器端启动的对象，然后去配置参数
        ServerBootstrap bootstrap = new ServerBootstrap();

        //5、使用链式编程来设置配置参数。

        //设置两个线程组
        bootstrap.group(bossGroup, workerGroup)
                 //使用 NioServerSocketChannel 作为服务器的通道实现
                 //针对bossGroup，使用的是NioServerSocketChannel，针对workerGroup使用的是SocketChannel
                 .channel(NioServerSocketChannel.class)
                 //设置线程队列得到连接个数
                 .option(ChannelOption.SO_BACKLOG, 128)
                 //设置整个连接时活动状态
                 .childOption(ChannelOption.SO_KEEPALIVE, true)
                 //创建一个通过初始化对象(匿名对象)，
                 .childHandler(new ChannelInitializer<SocketChannel>() {
                     // 操作整个过程，底层使用的是SocketChannel

                     //这里完成一个任务，任务是 向这个workerGroup关联的pipeline里面增加
                     //一个handler ,也就是 给 pipeline 设置处理器
                     @Override
                     protected void initChannel(SocketChannel ch) throws Exception {
                         //处理器既可以是netty提供的，也可以是自定义的， 然后接收来自客户的消息
                         //addList，将管道最后添加一个处理器
                         // 接收客户端消息和发送消息到客户端，这种事情交由channel关联的pipeline的一个handler来处理。
                         //这里使用自定义的handler来接收客户端的消息，并且将消息返回给客户端
                         ch.pipeline().addLast( new NettyServerHandler());
                     }
                 });// 很关键，给我们的 workerGroup 的 EventLoop 对应的管道设置处理器

        System.out.println(".....服务器 is ready...");

        //绑定一个端口并且同步, 生成了一个 ChannelFuture 对象
        //这里就相当于 启动服务器(并绑定端口)
        ChannelFuture channelFuture = bootstrap.bind(6667).sync();

        //对关闭通道进行监听， 当有关闭通道的消息或者事件时才会进行一个处理，而不是立即去关闭
        channelFuture.channel().closeFuture().sync();
        }finally {
            //这里没有捕获异常，是因为 将异常抛出去了
            //如果再服务器端出现了异常，那么就要关闭。
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

```

服务端自定义处理器

```java
package com.learn.simple.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

/**
 * 自定义服务端处理器，来处理客户端的消息，并且返回消息给客户端
 *
 * 1. 我们自定义一个 Handler 需要继承 netty 规定好的某个 HandlerAdapter(规范)
 * 2. 这时我们自定义一个 Handler , 才能称为一个 handler
 *
 * @ClassName: NettyServerHandler
 * @Description: 自定义服务端处理器，来处理客户端的消息，并且返回消息给客户端
 *      ChannelInboundHandlerAdapter 一个入栈Handler适配器
 * @Author: lin
 * @Date: 2020/10/16 9:49
 * History:
 * @<version> 1.0
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * //读取数据实际(这里我们可以读取客户端发送的消息)，
     *   通过pipeline的调用会把消息传输到pipeline关联的handler上
     * 1. ChannelHandlerContext ctx:上下文对象, 含有 管道 pipeline(业务逻辑处理) , 通道 channel(注重数据的读写), 地址等等
     * 2. Object msg: 就是客户端发送的数据 默认 Object
     * @param ctx 上下文对象
     * @param msg 客户端发送的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server ctx =" + ctx);

        Channel channel = ctx.channel();
        //本质是一个双向链接, 出站入站
        ChannelPipeline pipeline = ctx.pipeline();
         //在Nio中 拿到数据后将其放到缓冲里。
        // 所以这里拿到数据msg后，将 msg 转成一个 ByteBuf进行处理，更好些。
        // ByteBuf 是 Netty 提供的，不是 NIO 的 ByteBuffer。 netty是对Nio的一次再包装
        ByteBuf byteBuf =(ByteBuf) msg;
        System.out.println("客户端发送消息是:" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址:" + channel.remoteAddress());
    }


    /**
     * 数据读取完毕后，返回给客户端回复一个消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // writeAndFlush 方法将数据写入到缓存，并刷新 ，writeAndFlush= write +flush
        // 如果使用write那么就是将数据写缓冲区，但是没有刷新到通道中去。
        // 一般讲，我们对这个发送的数据进行编码。 设置编码并放到 buffer中去
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~瞅你咋地！！！", CharsetUtil.UTF_8));
    }

    /**
     * 处理异常, 如果发生异常 一般是需要关闭通道
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //都可以
//        ctx.channel().closeFuture();
        ctx.close();
    }
}

```

客户端：

```java
package com.learn.simple.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @ClassName: NettyClient
 * @Description: 客户端，测试服务端发生消息。并接收服务端回复的消息
 * @Author: lin
 * @Date: 2020/10/16 10:28
 * History:
 * @<version> 1.0
 */
public class NettyClient {
    public static void main(String[] args)throws  Exception {
        //客户端需要一个事件循环组
        EventLoopGroup group = new NioEventLoopGroup();

        try {
        //创建客户端启动对象，服务端使用的ServerBootstrap, 客户端使用Bootstrap
        Bootstrap bootstrap = new Bootstrap();

        //设置相关参数
        //1.设置线程组, 放入事件循环组，客户端也是处于一个循环的状态, 如果服务端还有消息给客户端，那么就要循环处理
        bootstrap.group(group)
                  // 设置客户端通道的实现类(反射)
                 .channel(NioSocketChannel.class)
                  //客户端也需要handler来处理。
                 .handler(new ChannelInitializer<SocketChannel>() {
                     @Override
                     protected void initChannel(SocketChannel ch) throws Exception {
                         //加入自己的处理器
                         ch.pipeline().addLast(new NettyClientHandler());
                     }
                 });
        System.out.println("客户端 ok..");

        //启动客户端去连接服务器端
        //关于 ChannelFuture 要分析，涉及到 netty 的异步模型
        // 使用sync让这个方法不 阻塞在这里
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6667).sync();

        //给关闭通道进行监听
        //关闭通道不是马上关闭，而是在关闭通道事件发生后，才去关闭并且进行监听。
        channelFuture.channel().closeFuture().sync();
        }finally {
           //关闭
           group.shutdownGracefully();
        }
    }
}

```

客户端处理器

```java
package com.learn.simple.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 客户端自定义 handler 来发送消息和 接收服务端返回下消息。
 * @ClassName: NettyClientHandler
 * @Description: 客户端自定义 handler 同样继承 ChannelInboundHandlerAdapter
 * @Author: lin
 * @Date: 2020/10/16 10:45
 * History:
 * @<version> 1.0
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * 当通道就绪时，就会触发该方法, 给服务端发送消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client " + ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, server: 你瞅啥！！！", CharsetUtil.UTF_8));
    }

    /**
     * 当通道有读取事件时，会触发该方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("服务器回复的消息:" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("服务器的地址： "+ ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //打印异常信息
        cause.printStackTrace();
        //关闭管道
        ctx.close();
    }
}

```

启动服务端和客户端 ,  可以看到客户向服务端发送消息后，服务端接收了消息并打印出了消息和地址。

![image](image/java-netty-server-01.png)

客户端接收服务端回复的消息， 同时也打印了client 的上下文信息。

![image](image/java-netty-client-01.png)



##### 5.8.7、Netty案例源码分析

1)、 bossGroup 和 workerGroup 含有的子线程(NioEventLoop)的个数 ，默认实际 cpu 核数 * 2

我们可以根据源代码来分析, 在创建NioEventLoopGroup对象时，如果没有设置线程个数，那么最终会调用MultithreadEventLoopGroup中的MultithreadEventLoopGroup构造方法来默认设置线程个数。

```java
public abstract class MultithreadEventLoopGroup extends MultithreadEventExecutorGroup implements EventLoopGroup   
   private static final int DEFAULT_EVENT_LOOP_THREADS;

    static {
        DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt(
                "io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));

        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.eventLoopThreads: {}", DEFAULT_EVENT_LOOP_THREADS);
        }
    }

  protected MultithreadEventLoopGroup(int nThreads, Executor executor, Object... args) {
      // 如果线程个数为0，那么就使用默认，这个默认的线程数在上面的 static块中设置，根据cpu核数 * 2来设置
        super(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads, executor, args);
    }
}    
```

本机是16 个逻辑处理器，所以在创建NioEventLoopGroup后，子线程会有32个。 

![image](image/java-netty-boss-group-number-01.png)

还可以知道，bossGroup的类型是NioEventLoopGroup，子线程类型是NioEventLoop。这个和netty工作原理示意图一致。

![image](image/java-netty-boss-group-number-02.png)

同样workerGroup也是一样的。

![image](image/java-netty-worker-group-number-01.png)

并且每个NioEventLoop最终是 用EventExecutor来进行调用或者处理的。

2)、当设置bossGroup 的线程数量为1个，workerGroup使用8个(这里默认的是32，太多了~~~)线程个数。

```
EventLoopGroup bossGroup = new NioEventLoopGroup(1);
EventLoopGroup workerGroup = new NioEventLoopGroup(8);
```

启动8个客户端进行数据的通讯，看看这个worker是如何去分配这8个NioEventLoop的。可以看到

![image](image/java-netty-boss-worker-group-test-01.png)

如果当再启动一个客户端时候，workerGroup会循环的去分配。

![image](image/java-netty-boss-worker-group-test-02.png)



同样可以看到设置的数据个。

![image](image/java-netty-boss-worker-group-test-03.png)





每个NioEventLoop包含selector, 每个selector 有对应的selectionKeys， 还是taskQueue 等等。

![image](image/java-netty-boss-worker-group-test-04.png)





3）、channle和 pipeline的关系 ，以及context 包含什么。

context的实际类型是 DefaultChannelHandlerContext,  并且包含了自定义的handler， pipelined等信息。我们将handler加入到管道，然后通过上下文也可以获取到。

![image](image/java-netty-context-01.png)

pipelie是一个双向链表，通过pipelie可以获取channel， pipeline和channel是一个对应的关系。

![image](image/java-netty-context-pipeline-01.png)

可以看到channel中和 pipeline中是 一个相互包含的关系。

![image](image/java-netty-context-channel-01.png)

而context 将 pipeline和 channel都包含了。

小结：bossGroup 和 workerGroup都是服务端 线程，它们两个处理的事情不同，都存在NioEventLoop事件循环。bossGroup 处理连接请求 ，将其channel注册到selector中。 然后通过workerGroup来处理 客户端的业务。

实际处理业务是通过 channel关联的 pipeline的 handler来处理，

##### 5.8.8、任务队列中的 Task 有 3 种典型使用场景

###### 1)、用户程序自定义的普通任务

2)、用户自定义定时任务 

3)、非当前 Reactor 线程调用 Channel 的各种方法 

例如在推送系统的业务线程里面，根据用户的标识，找到对应的 Channel 引用，然后调用 Write 类方法向该用户推送消息，就会进入到这种场景。最终的 Write 会提交到任务队列中后被异步消费

4)、代码示例

如果一个非常耗时的任务，再服务端需要处理很长事件。那么怎么处理？

```java
package com.learn.simple.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

/**
 * 自定义服务端处理器，来处理客户端的消息，并且返回消息给客户端
 *
 * 1. 我们自定义一个 Handler 需要继承 netty 规定好的某个 HandlerAdapter(规范)
 * 2. 这时我们自定义一个 Handler , 才能称为一个 handler
 *
 * @ClassName: NettyServerHandler
 * @Description: 自定义服务端处理器，来处理客户端的消息，并且返回消息给客户端
 *      ChannelInboundHandlerAdapter 一个入栈Handler适配器
 * @Author: lin
 * @Date: 2020/10/16 9:49
 * History:
 * @<version> 1.0
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * //读取数据实际(这里我们可以读取客户端发送的消息)，
     *   通过pipeline的调用会把消息传输到pipeline关联的handler上
     * 1. ChannelHandlerContext ctx:上下文对象, 含有 管道 pipeline(业务逻辑处理) , 通道 channel(注重数据的读写), 地址等等
     * 2. Object msg: 就是客户端发送的数据 默认 Object
     * @param ctx 上下文对象
     * @param msg 客户端发送的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //比如这里我们有一个非常耗时长的业务-> 异步执行 -> 提交该 channel 对应的
        // NIOEventLoop 的 taskQueue 中
        Thread.sleep(10 * 1000);
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 2", CharsetUtil.UTF_8));

        System.out.println("go on ...");

//        System.out.println("服务器读取线程 " + Thread.currentThread().getName());
//        System.out.println("server ctx =" + ctx);
//        System.out.println("看看 channel 和 pipeline 的关系");
//        Channel channel = ctx.channel();
//        //本质是一个双向链接, 出站入站
//        ChannelPipeline pipeline = ctx.pipeline();
//         //在Nio中 拿到数据后将其放到缓冲里。
//        // 所以这里拿到数据msg后，将 msg 转成一个 ByteBuf进行处理，更好些。
//        // ByteBuf 是 Netty 提供的，不是 NIO 的 ByteBuffer。 netty是对Nio的一次再包装
//        ByteBuf byteBuf =(ByteBuf) msg;
//        System.out.println("客户端发送消息是:" + byteBuf.toString(CharsetUtil.UTF_8));
//        System.out.println("客户端地址:" + channel.remoteAddress());
    }


    /**
     * 数据读取完毕后，返回给客户端回复一个消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // writeAndFlush 方法将数据写入到缓存，并刷新 ，writeAndFlush= write +flush
        // 如果使用write那么就是将数据写缓冲区，但是没有刷新到通道中去。
        // 一般讲，我们对这个发送的数据进行编码。 设置编码并放到 buffer中去
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵 1", CharsetUtil.UTF_8));
    }

    /**
     * 处理异常, 如果发生异常 一般是需要关闭通道
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //都可以
//        ctx.channel().closeFuture();
        ctx.close();
    }
}

```

上面在方法channelRead中模拟设置一个任务 耗时10s。先等待10s后 打印出 喵 2，然后在服务端 打印出go on 之后再打印 “喵 1”。 所以这种情况 ，如果当一个业务处理非常耗时，那么就会使服务器阻塞 。  这样就不能够合理的使用服务器资源。

![image](image/java-netty-task-long-time-01.png)



对这种情况 有几种解决方案 ：

###### a 解决方案1：使用自定义的普通任务，使其异步执行。 

在channelRead方法 中ctx.channel().eventLoop().execute()来创建一个线程，将耗时长的任务 交个这个线程来处理

```java
/**
     * //读取数据实际(这里我们可以读取客户端发送的消息)，
     *   通过pipeline的调用会把消息传输到pipeline关联的handler上
     * 1. ChannelHandlerContext ctx:上下文对象, 含有 管道 pipeline(业务逻辑处理) , 通道 channel(注重数据的读写), 地址等等
     * 2. Object msg: 就是客户端发送的数据 默认 Object
     * @param ctx 上下文对象
     * @param msg 客户端发送的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //比如这里我们有一个非常耗时长的业务-> 异步执行 -> 提交该 channel 对应的
        // NIOEventLoop 的 taskQueue 中

//        Thread.sleep(10 * 1000);
//        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 2 ", CharsetUtil.UTF_8));
//


        //任务处理耗时，解决方案 1 用户程序自定义的普通任务

        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 2 ",
                            CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    System.out.println("发生异常" + e.getMessage());
                }
            }
        });
        System.out.println("go on ...");

//        System.out.println("服务器读取线程 " + Thread.currentThread().getName());
//        System.out.println("server ctx =" + ctx);
//        System.out.println("看看 channel 和 pipeline 的关系");
//        Channel channel = ctx.channel();
//        //本质是一个双向链接, 出站入站
//        ChannelPipeline pipeline = ctx.pipeline();
//         //在Nio中 拿到数据后将其放到缓冲里。
//        // 所以这里拿到数据msg后，将 msg 转成一个 ByteBuf进行处理，更好些。
//        // ByteBuf 是 Netty 提供的，不是 NIO 的 ByteBuffer。 netty是对Nio的一次再包装
//        ByteBuf byteBuf =(ByteBuf) msg;
//        System.out.println("客户端发送消息是:" + byteBuf.toString(CharsetUtil.UTF_8));
//        System.out.println("客户端地址:" + channel.remoteAddress());
    }
```

执行结果，服务端和 客户端启动后， 客户端先打印的是"喵1 "，并且服务端不会阻塞 打印出 “go on” ,在10s后 会打印出"喵2"。

![image](image/java-netty-task-queue-test-02.png)

客户端

![image](image/java-netty-task-queue-test-01.png)

那么这个耗时比较长的任务是放到哪里呢？ 我们通过debug模式来查看 ， 可以看到任务 比加到了 ctx下的 pipeline中的  channel中的 eventLoop中的  taskQueue中。 这个taskQueue中多了一个任务。

![image](image/java-netty-pipeline-channel-eventloop-taskqueue-01.png)

我们可以再添加一个任务 ，来证明任务是加入到了taskQueue中。 

```java
 /**
     * //读取数据实际(这里我们可以读取客户端发送的消息)，
     *   通过pipeline的调用会把消息传输到pipeline关联的handler上
     * 1. ChannelHandlerContext ctx:上下文对象, 含有 管道 pipeline(业务逻辑处理) , 通道 channel(注重数据的读写), 地址等等
     * 2. Object msg: 就是客户端发送的数据 默认 Object
     * @param ctx 上下文对象
     * @param msg 客户端发送的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //比如这里我们有一个非常耗时长的业务-> 异步执行 -> 提交该 channel 对应的
        // NIOEventLoop 的 taskQueue 中

//        Thread.sleep(10 * 1000);
//        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 2 ", CharsetUtil.UTF_8));
//


        //任务处理耗时，解决方案 1 用户程序自定义的普通任务

        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                     System.out.println("线程名称： " + Thread.currentThread().getName());
                    Thread.sleep(10 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 2 ",
                            CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    System.out.println("发生异常" + e.getMessage());
                }
            }
        });

        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                     System.out.println("线程名称： " + Thread.currentThread().getName());
                    Thread.sleep(20 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 3 ",
                            CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    System.out.println("发生异常" + e.getMessage());
                }
            }
        });

        System.out.println("go on ...");

//        System.out.println("服务器读取线程 " + Thread.currentThread().getName());
//        System.out.println("server ctx =" + ctx);
//        System.out.println("看看 channel 和 pipeline 的关系");
//        Channel channel = ctx.channel();
//        //本质是一个双向链接, 出站入站
//        ChannelPipeline pipeline = ctx.pipeline();
//         //在Nio中 拿到数据后将其放到缓冲里。
//        // 所以这里拿到数据msg后，将 msg 转成一个 ByteBuf进行处理，更好些。
//        // ByteBuf 是 Netty 提供的，不是 NIO 的 ByteBuffer。 netty是对Nio的一次再包装
//        ByteBuf byteBuf =(ByteBuf) msg;
//        System.out.println("客户端发送消息是:" + byteBuf.toString(CharsetUtil.UTF_8));
//        System.out.println("客户端地址:" + channel.remoteAddress());
    }

```

在添加了一个任务后，taskQueue中多了一个任务， 这意味这些任务都被提交到taskQueue中，然后异步的去执行。

![image](image/java-netty-pipeline-channel-eventloop-taskqueue-02.png)

注意：上面的两给任务，第一个任务在10s后执行，第二个任务是在 第一个任务的基础上 加20s再执行， 也就是说在 “喵3 ” 需要30s后才会发送回客户端去。 因为这个是同一个线程，所以还需要休眠 20s。 

![image](image/java-netty-pipeline-channel-eventloop-taskqueue-03.png)

###### b.解决方案2：用户自定义定时任务 -- 将任务提交到 scheduleTaskQueue中 。

 下面的代码 在添加一个线程，然后将这些任务提交到schedule中，来执行耗时比较长的任务。

```java
  /**
     * //读取数据实际(这里我们可以读取客户端发送的消息)，
     *   通过pipeline的调用会把消息传输到pipeline关联的handler上
     * 1. ChannelHandlerContext ctx:上下文对象, 含有 管道 pipeline(业务逻辑处理) , 通道 channel(注重数据的读写), 地址等等
     * 2. Object msg: 就是客户端发送的数据 默认 Object
     * @param ctx 上下文对象
     * @param msg 客户端发送的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //比如这里我们有一个非常耗时长的业务-> 异步执行 -> 提交该 channel 对应的
        // NIOEventLoop 的 taskQueue 中

//        Thread.sleep(10 * 1000);
//        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 2 ", CharsetUtil.UTF_8));
//


        //任务处理耗时，解决方案 1 用户程序自定义的普通任务

        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("线程名称： " + Thread.currentThread().getName());
                    Thread.sleep(10 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 2 ",
                            CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    System.out.println("发生异常" + e.getMessage());
                }
            }
        });

        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("线程名称： " + Thread.currentThread().getName());
                    Thread.sleep(20 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 3 ",
                            CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    System.out.println("发生异常" + e.getMessage());
                }
            }
        });


        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("线程名称： " + Thread.currentThread().getName());
                    Thread.sleep(5 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~~(>^ω^<)喵 4 ",
                            CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    System.out.println("发生异常" + e.getMessage());
                }
            }
            // 延迟时间5s。
        }, 5, TimeUnit.SECONDS);


        System.out.println("go on ...");

//        System.out.println("服务器读取线程 " + Thread.currentThread().getName());
//        System.out.println("server ctx =" + ctx);
//        System.out.println("看看 channel 和 pipeline 的关系");
//        Channel channel = ctx.channel();
//        //本质是一个双向链接, 出站入站
//        ChannelPipeline pipeline = ctx.pipeline();
//         //在Nio中 拿到数据后将其放到缓冲里。
//        // 所以这里拿到数据msg后，将 msg 转成一个 ByteBuf进行处理，更好些。
//        // ByteBuf 是 Netty 提供的，不是 NIO 的 ByteBuffer。 netty是对Nio的一次再包装
//        ByteBuf byteBuf =(ByteBuf) msg;
//        System.out.println("客户端发送消息是:" + byteBuf.toString(CharsetUtil.UTF_8));
//        System.out.println("客户端地址:" + channel.remoteAddress());
    }
```

测试结果如下，这个执行都是根据设置的等待时间来执行， schedule中设置的是等待5s钟。同样也是等待前面的执行之后，再过5s 才执行schedule调度任务。

![image](image/java-netty-schedule-queue-test-01.png)



c.对于上述面的第三个问题，如果有一个业务要将消息异步的推送到A、B、C三个不同是管道中。那么就要推送到其任务队列中去。对于这种事情就需要那取到 对应channel，然后获取到eventLoop，再然后获取 任务队列。我们就可以将任务 放到任务队列中去处理业务。 

再NioServer添加一个打印，来查看不同客户端的hashCode。

![image](image/java-netty-socketchannel-different-01.png)





##### 5.8.9、方案再说明 

1)、Netty 抽象出两组线程池，BossGroup 专门负责接收客户端连接，WorkerGroup 专门负责网络读写操作。 

2)、NioEventLoop 表示一个不断循环执行处理任务的线程，每个 NioEventLoop 都有一个 selector，用于监听绑定 在其上的 socket 网络通道。 

3)、NioEventLoop 内部采用串行化设计，从消息的读取->解码->处理->编码->发送，始终由 IO 线程 NioEventLoop 负责 

```
(1)、NioEventLoopGroup 下包含多个 NioEventLoop
  .每个 NioEventLoop 中包含有一个 Selector，一个 taskQueue 
  .每个 NioEventLoop 的 Selector 上可以注册监听多个 NioChannel 
  .每个 NioChannel 只会绑定在唯一的 NioEventLoop 上 
  .每个 NioChannel 都绑定有一个自己的 ChannelPipeline
    Channel和pipeline是相互包含的。 通过channel可以获取到pipeline， 也可以通过pipeline获取到channel
```



#### 5.9、异步模式

##### 5.9.1、基本介绍

1)、$\textcolor{red}{异步的概念和同步相对。当一个异步过程调用发出后，调用者不能立刻得到结果}$。实际处理这个调用的组件在 

完成后，通过状态、通知和回调来通知调用者。 

2)、$\textcolor{red}{Netty 中的 I/O 操作是异步的，包括 Bind、Write、Connect 等操作会简单的返回一个 ChannelFuture。 }$

3) 调用者并不能立刻获得结果，而是通过 Future-Listener 机制，用户可以方便的主动获取或者通过通知机制获得 

IO 操作结果 

4)、Netty 的异步模型是建立在 future 和 callback 的之上的。callback 就是回调。重点说 Future，它的核心思想 

是：假设一个方法 fun，计算过程可能非常耗时，等待 fun 返回显然不合适。那么可以在调用 fun 的时候，立 

马返回一个 Future，后续可以通过 Future 去监控方法 fun 的处理过程 (即 ： Future-Listener 机制)

##### 5.9.2、Future 说明

1)、表示**异步的执行结果**, 可以通过它提供的方法来检测执行是否完成，比如检索计算等等. 

2)、ChannelFuture 是一个接口 ： public interface ChannelFuture extends Future<Void> 

我们可以添**加监听器，当监听的事件发生时，就会通知到监听器**。

##### 5.9.3、工作原理示意图

![image](image/java-netty-asyc-01.png)



![image](image/java-netty-asyc-02.png)

说明：

1)、在使用 Netty 进行编程时，拦截操作和转换出入站数据只需要您提供 callback 或利用 future 即可。这使得链 

式操作简单、高效, 并有利于编写可重用的、通用的代码。 

2)、Netty 框架的目标就是让你的业务逻辑从网络基础应用编码中分离出来、解脱出来

3、在数据处理的环节都会有相应的handler来与之对应。

##### 5.9.4、Future-Listener 机制

1) 当 Future 对象刚刚创建时，处于非完成状态，调用者可以通过返回的 ChannelFuture 来获取操作执行的状态， 

注册监听函数来执行完成后的操作。 

2) 常见有如下操作

  a、通过 isDone 方法来判断当前操作是否完成； 

  b、通过 isSuccess 方法来判断已完成的当前操作是否成功； 

  c、通过 getCause 方法来获取已完成的当前操作失败的原因； 

 d、通过 isCancelled 方法来判断已完成的当前操作是否被取消； 

 e、通过 $\textcolor{Red}{addListener 方法来注册监听器}$，当操作已完成(isDone 方法返回完成)，将会通知指定的监听器；如果 

Future 对象已完成，则通知指定的监听器



举例说明：绑定端口是异步操作，当绑定操作处理完，将会调用相应的监听器处理逻辑

在NettServer 类 中，添加监听

```java

        //绑定一个端口并且同步, 生成了一个 ChannelFuture 对象
        //这里就相当于 启动服务器(并绑定端口)
        ChannelFuture channelFuture = bootstrap.bind(6667).sync();

        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
             if(future.isSuccess()) {
                 System.out.println("监听端口 6668 成功");
             }else {
                 System.out.println("监听端口 6668 失败");
             }
            }
        });
```



小结：相比传统阻塞I/O，执行I/O操作后线程会被阻塞住，直到操作完成；异步处理的好处是不会造成线程阻塞，线程在I/O操作期间可以执行别的程序，在高并发情形下会更稳定和更高的吞吐量。

#### 5.10、快速入门实例-HTTP 服务 

1)、实例要求：使用 IDEA 创建 Netty 项目 

2)、Netty 服务器在 6668 端口监听，浏览器发出请求 "http://localhost:6668/ " 

3)、服务器可以回复消息给客户端 "Hello! 我是服务器 5 " , 并对特定请求资源进行过滤. 

4)、目的：Netty 可以做 Http 服务开发，并且理解 Handler 实例和客户端及其请求的关系.

5)、代码示例

服务端：

```java
package com.learn.simple.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Netty 做 Http 服务开发，并且理解 Handler 实例和客户端及其请求的关系.
 * @ClassName: TestServer
 * @Description: 使用 Netty来进行http 示例的开发
 * @Author: lin
 * @Date: 2020/10/19 8:14
 * History:
 * @<version> 1.0
 */
public class TestServer {
    public static void main(String[] args)throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(workerGroup, workerGroup)
                           .channel(NioServerSocketChannel.class)
                          //这里将原来的 写法，全部提出来单独些，这样方便维护
                           .childHandler(new TestServerInitializer());
            ChannelFuture channelFuture = serverBootstrap.bind(7778).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}

```

Initializer 类

```java
package com.learn.simple.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @ClassName: TestServerInitializer
 * @Description: 创建ChannelInitializer，将原来的写法提出了单独写，这样方便维护
 * @Author: lin
 * @Date: 2020/10/19 8:16
 * History:
 * @<version> 1.0
 */
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
         //向管道里加入处理器

         ChannelPipeline pipeline = ch.pipeline();

         //加入一个netty提供的 httpServerCodec  codec=>[coder--decoder] 这个是编解码器
          //httpServerCodec 作用说明
          //1. httpServerCodec的 netty提供的一个处理Http的编解码器
          pipeline.addLast("MyHttpServerCodec", new HttpServerCodec());
          //增加一个自定义的handler
          pipeline.addLast("MyTestHttpServerHandler", new TestHttpServerHandler());
    }
}

```

handler类

```java
package com.learn.simple.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * 服务端处理器
 * @ClassName: TestHttpServerHandler
 * @Description: 服务端处理器
 *               说明
 *               1： SimpleChannelInboundHandler 是 ChannelInboundHandler的一个子类
 *               2：HttpObject 是客户端和服务器端相互通讯的数据被封装成 HttpObject , 所以重写的方法中 msg对应成了httpObject
 *               这里在继承的时候 指定了客户端和服务器端在进行处理的时候的 数据类型
 * @Author: lin
 * @Date: 2020/10/19 8:16
 * History:
 * @<version> 1.0
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    /**
     *  当有读取事件发生时，就会触发该方法
     *  channelRead0 读取客户端数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        //判断msg是不是 httpRequest请求
        if(msg instanceof HttpRequest){
            System.out.println("msg 类型" + msg.getClass());
            System.out.println("客户端地址" + ctx.channel().remoteAddress());

            //浏览器向服务器请求，发送了一个http请求，这里就可以进行回复信息给浏览器了
            //那么回复信息给浏览器，这个回复信息要做成 http协议的才可以回复。

            ByteBuf content =Unpooled.copiedBuffer("hello, 我是服务器", CharsetUtil.UTF_8);

            //构造一个http的响应，即HttpResponse。
            // 使用默认的http响应，并且指定协议版本和状态
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, content);

            //设置返回头信息，以及长度
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            //将构建好的response返回
            ctx.writeAndFlush(response);

        }
    }
}

```

使用浏览器测试，在浏览器输入localhost:7778 访问,  可以看到 访问的时候会打印两次。出现这种情况的原因是浏览器在请求的时候，第二次是浏览器的图标请求的。

![image](image/java-netty-httpserver-test-01.png)

可以看到有两次请求，第二次请求是浏览器的图标造成的。

![image](image/java-netty-httpserver-test-02.png)



那么怎么解决呢？这就需要在handler处理时候，进行过滤处理。修改handler

```java
package com.learn.simple.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * 服务端处理器
 * @ClassName: TestHttpServerHandler
 * @Description: 服务端处理器
 *               说明
 *               1： SimpleChannelInboundHandler 是 ChannelInboundHandler的一个子类
 *               2：HttpObject 是客户端和服务器端相互通讯的数据被封装成 HttpObject , 所以重写的方法中 msg对应成了httpObject
 *               这里在继承的时候 指定了客户端和服务器端在进行处理的时候的 数据类型
 * @Author: lin
 * @Date: 2020/10/19 8:16
 * History:
 * @<version> 1.0
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    /**
     *  当有读取事件发生时，就会触发该方法
     *  channelRead0 读取客户端数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        //判断msg是不是 httpRequest请求
        if(msg instanceof HttpRequest){
            System.out.println("pipeline hashcode" + ctx.pipeline().hashCode() + " TestHttpServerHandler hash=" + this.hashCode());
            System.out.println("msg 类型" + msg.getClass());
            System.out.println("客户端地址" + ctx.channel().remoteAddress());

            //这里获取到httpRequest请求
            HttpRequest httpRequest = (HttpRequest) msg;
            //获取uri
            URI uri = new URI(httpRequest.uri());
            //获取到uri后 判断uri中的路径中是否有图标路径
            String  icoStr  = "/favicon.ico";
            if(icoStr.equals(uri.getPath())){
                System.out.println("请求了 favicon.ico, 不做响应");
                return;
            }

            //浏览器向服务器请求，发送了一个http请求，这里就可以进行回复信息给浏览器了
            //那么回复信息给浏览器，这个回复信息要做成 http协议的才可以回复。
            ByteBuf content =Unpooled.copiedBuffer("hello, 我是服务器", CharsetUtil.UTF_8);

            //构造一个http的响应，即HttpResponse。
            // 使用默认的http响应，并且指定协议版本和状态
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, content);

            //设置返回头信息，以及长度
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            //将构建好的response返回
            ctx.writeAndFlush(response);

        }
    }
}

```

测试, 客户看到服务器还是打印了两次

![image](image/java-netty-httpserver-test-03.png)

但是浏览器响应后 favicon.ico没有 响应了。

![image](image/java-netty-httpserver-test-04.png)



注意：每次浏览器访问时，都会产生一个handler，也就是独享一个pipeline和 handler, 我们在上的代码中加入打印语句,来看每次的hashCode和pipeline 是否一样。 下面三次请求可以知道每次的 pipeline和 handler都是不同。这就是说明了每个浏览器 请求都会产生一个新的handler，不会共享一个handler。 

![image](image/java-netty-httpserver-test-05.png)

对于Http协议用完了之后就会自动断掉，它不是一个长连接，每次刷新请求产生的handler都是不同的。这时因为http不是长连接，每次用完之后就会断掉。



###  6、Netty 核心模块组件

####   6.1、Bootstrap、ServerBootstrap 

1)、Bootstrap 意思是引导，一个 Netty 应用通常由一个 Bootstrap 开始，主要作用是配置整个 Netty 程序，串联 

各个组件，Netty 中 Bootstrap 类是客户端程序的启动引导类，ServerBootstrap 是服务端启动引导类 

2)、常见的方法有 

```java
public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup)，该方法用于服务器端， 用来设置两个 EventLoop 

public B group(EventLoopGroup group);该方法用于客户端，用来设置一个 EventLoop 

public B channel(Class<? extends C> channelClass);该方法用来设置一个服务器端的通道实现 

public <T> B option(ChannelOption<T> option, T value);用来给 ServerChannel 添加配置 

public <T> ServerBootstrap childOption(ChannelOption<T> childOption, T value)，用来给接收到的通道添加配置 

    
 // 对于handler和childHandler区别：handler对应bossGroup, childHandler对应workerGroup
 // 也就是说，如果要给bossGroup加入handler，那么用的就是handler，如果要在workerGroup中加入
 // handler，用的就是childHandler。

public B handler(ChannelHandler handler) {}    
    
public ServerBootstrap childHandler(ChannelHandler childHandler)，该方法用来设置业务处理类（自定义的 handler）

public ChannelFuture bind(int inetPort) ，该方法用于服务器端，用来设置占用的端口号 

public ChannelFuture connect(String inetHost, int inetPort) ，该方法用于客户端，用来连接服务器端
```

#### 6.2、Future、ChannelFuture 

1)、Netty 中所有的 IO 操作都是异步的，不能立刻得知消息是否被正确处理。但是可以过一会等它执行完成或 

者直接注册一个监听，具体的实现就是通过 Future 和 ChannelFutures，他们可以注册一个监听，当操作执行成功 或失败时监听会自动触发注册的监听事件 

2)、常见的方法有 

```
Channel channel()，返回当前正在进行 IO 操作的通道 

ChannelFuture sync()，等待异步操作执行完毕
```



#### 6.3、Channel 

1)、Netty 网络通信的组件，能够用于执行网络 I/O 操作。 

2)、通过 Channel 可获得当前网络连接的通道的状态 

3)、通过 Channel 可获得 网络连接的配置参数 （例如接收缓冲区大小） 

4)、Channel 提供异步的网络 I/O 操作(如建立连接，读写，绑定端口)，异步调用意味着任何 I/O 调用都将立即返 

回，并且不保证在调用结束时所请求的 I/O 操作已完成 

5)、调用立即返回一个 ChannelFuture 实例，通过注册监听器到 ChannelFuture 上，可以 I/O 操作成功、失败或取 消时回调通知调用方 

6)、支持关联 I/O 操作与对应的处理程序 

7)、$\textcolor{Red}{不同协议、不同的阻塞类型的连接都有不同的 Channel 类型与之对应，常用的 Channel 类型}$

```
NioSocketChannel，异步的客户端 TCP Socket 连接。 
NioServerSocketChannel，异步的服务器端 TCP Socket 连接。 
NioDatagramChannel，异步的 UDP 连接。 
NioSctpChannel，异步的客户端 Sctp 连接。 
NioSctpServerChannel，异步的 Sctp 服务器端连接，这些通道涵盖了 UDP 和 TCP 网络 IO 以及文件 IO。
```

#### 6.4、Selector 

1)、$\textcolor{Red}{Netty 基于 Selector 对象实现 I/O 多路复用，通过 Selector 一个线程可以监听多个连接的 Channel 事件。}$ 

2)、当向一个 Selector 中注册 Channel 后，Selector 内部的机制就可以自动不断地查询(Select) 这些注册的 

Channel 是否有已就绪的 I/O 事件（例如可读，可写，网络连接完成等），这样程序就可以很简单地使用一个 

线程高效地管理多个 Channel

#### 6.5、ChannelHandler 及其实现类

1)、$\textcolor{Red}{ChannelHandler 是一个接口，处理 I/O 事件或拦截 I/O 操作，并将其转发到其 ChannelPipeline(业务处理链) }$中的下一个处理程序。(一个pipeline里面其实有多个handler，当一个handler处理完之后将其交给其它的handler处理)

2)、ChannelHandler 本身并没有提供很多方法，因为这个接口有许多的方法需要实现，方便使用期间，可以继承它 的子类 

3)、ChannelHandler 及其实现类一览图

![image](image/java-netty-channelhandler-01.png)

```
说明：
  a. channelInboundHandler用于处理入站I/O事件。
  b. channelOutboundHandler用于处理出站I/O事件。

适配器： 
  a. channelInboundHandlerAdapter用于处理入站I/O事件
  b. channelOutboundHandlerAdapter用于处理出站I/O事件
  c. channelDuplexHandler 用于处理入站和出站事件。(一般不使用这个，容易混淆)
```



4)、我们经常需要自定义一个 Handler 类去继承 ChannelInboundHandlerAdapter，然后通过重写相应方法实现业务 逻辑，我们接下来看看一般都需要重写哪些方法

```java
public class ChannelInboundHandlerAdapter extends ChannelHandlerAdapter implements ChannelInboundHandler {

    /**
     * Calls {@link ChannelHandlerContext#fireChannelRegistered()} to forward
     * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
     *
     * Sub-classes may override this method to change behavior.
     */
    @Skip
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelRegistered();
    }

    /**
     * Calls {@link ChannelHandlerContext#fireChannelUnregistered()} to forward
     * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
     *
     * Sub-classes may override this method to change behavior.
     */
    @Skip
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelUnregistered();
    }

    /**
     * Calls {@link ChannelHandlerContext#fireChannelActive()} to forward
     * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
     *
     * Sub-classes may override this method to change behavior.
       通道就绪事件
     */
    @Skip
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
    }

    /**
     * Calls {@link ChannelHandlerContext#fireChannelInactive()} to forward
     * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
     *
     * Sub-classes may override this method to change behavior.
     */
    @Skip
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
    }

    /**
     * Calls {@link ChannelHandlerContext#fireChannelRead(Object)} to forward
     * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
     *
     * Sub-classes may override this method to change behavior.
     通道读取数据事件
     */
    @Skip
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.fireChannelRead(msg);
    }

    /**
     * Calls {@link ChannelHandlerContext#fireChannelReadComplete()} to forward
     * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
     *
     * Sub-classes may override this method to change behavior.
     通道读取完毕事件
     */
    @Skip
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }

    /**
     * Calls {@link ChannelHandlerContext#fireUserEventTriggered(Object)} to forward
     * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
     *
     * Sub-classes may override this method to change behavior.
     */
    @Skip
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        ctx.fireUserEventTriggered(evt);
    }
}
```



#### 6.6、Pipeline 和 ChannelPipeline 

$\textcolor{Red}{ChannelPipeline 是一个重点：}$ 

1)、ChannelPipeline 是一个 Handler 的集合，它负责处理和拦截 inbound 或者 outbound 的事件和操作，相当于 一个贯穿 Netty 的链。($\textcolor{Red}{也可以这样理解：ChannelPipeline 是 保存 ChannelHandler 的 List，用于处理或拦截 Channel 的入站事件和出站操作}$) 

2)、ChannelPipeline 实现了一种高级形式的拦截过滤器模式，使用户可以完全控制事件的处理方式，以及 Channel 中各个的 ChannelHandler 如何相互交互 

3)、在 Netty 中每个 Channel 都有且仅有一个 ChannelPipeline 与之对应，它们的组成关系如下

![image](image/java-netty-channel-01.png)

 说明：

 (a)、$\textcolor{Red}{一个channel包含了一个ChannelPipeline}$,  而 $\textcolor{Red}{ChannelPipeline中又维护了一个由ChannelHandlerContext组成的双向链表}$，并且每个ChannelHandlerContext中又关联着一个ChannelHandler

 (b)、入站事件和出站事件在一个双向链表中，入站事件会从链表head往后传递到最后一个入站的handler，出站事件会从链表tail往前传递到最前一个出站的handler，两种类型的handler互补干扰。

   事件的整体流动，一般将head 头这边认为是 server， tail一边认为是client。所以当从客户端到服务端认为是出站事件，因为是从尾部往 头部传递。head到tail 认为是入站事件



通过Debug来看ChannelPipeline包含哪些， 从debug方式可以看到， 这个pipeline的实际类型是DefaultChannelPipeline，包含head和tail。 对head中的 next 是一个 DefaultChannelHandlerContext类型，

第一个handler实际是自定义的 TestServerInitializer， 继续 下一个next 也是DefaultChannelHandlerContext类型 第二个handler是 HttpServerCodec。  继续往下 第三个handler是 TestHttpServerHandler， 再往下next类型

是DefaultChannePieple$TailContext@1587 ，然后下一个next没有了。

![image](image/java-netty-channelpipeline-01.png)

我们反过来看 ， 第一个handler是 TestHttpServerHandler， 第二个是HttpServerCodec，第三个是TestServerInitializer

![image](image/java-netty-channelpipeline-02.png)



最后确认 ctx的实际类型是 ： ctx 类型是= class io.netty.channel.DefaultChannelHandlerContext

![image](image/java-netty-channelhandlercontext-01.png)





4)、常用方法 

```
ChannelPipeline addFirst(ChannelHandler... handlers)，把一个业务处理类（handler）添加到链中的第一个位置 

ChannelPipeline addLast(ChannelHandler... handlers)，把一个业务处理类（handler）添加到链中的最后一个位置
```



#### 6.7、ChannelHandlerContext

1)、保存 Channel 相关的所有上下文信息，同时关联一个 ChannelHandler 对象 

2)、即 ChannelHandlerContext 中 包 含 一 个 具 体 的 事 件 处 理 器 ChannelHandler ， 同 时 

ChannelHandlerContext 中也绑定了对应的 pipeline 和 Channel 的信息，方便对 ChannelHandler 进行调用. 

3)、常用方法

```
.ChannelFuture close()；关闭通道
.ChannelOutboundInvoker flush().刷新
.ChannelFuture writeAndFlush(Object msg); 将数据写到channleFuture中当前channelHandler的下一个
channelHandler开始处理(出站)。
```



在TestHttpServerHandler类中的channelRead0方法中 添加打印日志, 来查询 channel ， pipeline等类型

```java
   System.out.println("对应的channel=" + ctx.channel() + " pipeline=" + ctx.pipeline() +
                "通过pipeline获取channel" +ctx.pipeline().channel());

        System.out.println("当前的ctx的handler" + ctx.handler());

```

启动打印结果如下 

![image](image/java-netty-channelhandlercontext-02.png)

可以看到 通过ctx 获取到的 channel的 id 是0x559348af 和 通过pipeline获取channel 获取的id=0x559348af 相同。  同时也可以获取当前的 handler是 TestHttpServerHandler。

```
对应的channel=[id: 0x559348af, L:/127.0.0.1:7778 - R:/127.0.0.1:13392] pipeline=DefaultChannelPipeline{(MyHttpServerCodec = io.netty.handler.codec.http.HttpServerCodec), (MyTestHttpServerHandler = com.learn.simple.netty.http.TestHttpServerHandler)}  
通过pipeline获取channel[id: 0x559348af, L:/127.0.0.1:7778 - R:/127.0.0.1:13392]

当前的ctx的handler=com.learn.simple.netty.http.TestHttpServerHandler@b2293b2
```







#### 6.8、ChannelOption 

1)、Netty 在创建 Channel 实例后,一般都需要设置 ChannelOption 参数。 

2)、ChannelOption 参数如下: 

```
ChannelOption.SO_BACKLOG: 对应TCP/IP协议listen函数中的backlog参数，用来初始化服务器可连接队列大小。服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接。多个客户端来的时候，服务端将不能处理客户端连接请求放在队列中等待出来，backlog参数指定了队列的大小。
ChannelOption.SO_KEEPALIVE: 一直保持连接活动状态
```



#### 6.9、EventLoopGroup 和其实现类 NioEventLoopGroup 

1)、EventLoopGroup 是一组 EventLoop 的抽象，Netty 为了更好的利用多核 CPU 资源，一般会有多个 EventLoop 同时工作，每个 EventLoop 维护着一个 Selector 实例。   EventLoopGroup 继承了EventExecutorGroup， 所以EventLoopGroup在执行的时候和 执行器有关系 。 这个接口继承关系

![image](image/java-netty-evntloopgroup-01.png)

2)、EventLoopGroup 提供 next 接口，可以从组里面按照一定规则获取其中一个 EventLoop 来处理任务。在 Netty 服 务 器 端 编 程 中 ， 我 们 一 般 都 需 要 提 供 两 个 EventLoopGroup ， 例 如 ： BossEventLoopGroup 和 WorkerEventLoopGroup。 

3)、通常一个服务端口即一个 ServerSocketChannel 对应一个 Selector 和一个 EventLoop 线程。BossEventLoop 负责 接收客户端的连接并将 SocketChannel 交给 WorkerEventLoopGroup， 然后选择其中的一个EventLoop 来进行 IO 处理，如下图所示

![image](image/java-netty-evntloop-01.png)

说明：

```
1.BossEventLoopGroup通常是一个单线程的EventLoop，EventLoop维护着一个注册了ServerSocketChannel的
Selector实例BossEventLoop不断轮询Selector将事件分离出来。
2.通常是OP_ACCEPT事件，然后将接收到的SocketChannel交给 WorkerEventLoopGroup。
3.WorkerEventLoopGroup会由next选择其中一个EventLoop来将这个SocketChannel注册其维护的Selector并对其后续的IO事件进行处理。
```



我们可以通过添加打印来验证 WorkerEventLoopGroup在进行选择的时候 多个EventLoop的规则是怎样的。是不是一个EventLoop对应多个客户端。



 使用 NettyServer来 添加打印日志，并且设置 WorkerEventLoopGroup 为8个线程，下面的打印的结果，可以看到每一次channel的id都是不一样的。 当8个线程做完之后 下一个 就又 轮询到第一个。 这里虽然是 nioEventLoopGroup-3-1 ，但是 channel的id 和 第一次 nioEventLoopGroup-3-1 的channd的id 是不一样的。 

不一样的原因是客户端不一样。所以 这里就可以证明上面的图，EventLoopk可以为多个channel服务。而且着机制next是按照 这个规则进行轮询的。 

![image](image/java-netty-evntloop-02.png)



![image](image/java-netty-evntloop-03.png)



在接口中 有next方法，这个方法就是决定 下一个是选哪一个EventLoop来进行服务。

```java

public interface EventLoopGroup extends EventExecutorGroup {
      /**
     * Return the next {@link EventLoop} to use
     */
    @Override
    EventLoop next();
 }
```



4)、常用方法 

```java
public NioEventLoopGroup()，构造方法 

public Future<?> shutdownGracefully(); 这个方法两个任务 1、断开连接， 2、关闭线程 
```



#### 6.10、Unpooled 类

1)、Netty 提供一个专门用来操作缓冲区(即 Netty 的数据容器)的工具类 

2)、常用方法如下所示 

```java
//通过给定的数据和字符编码返回一个ByteBuf对象(类似于NIO的ByteBuffer但有区别)
public static ByteBuf copiedBuffer(CharSequence String, Charset CharSet);
```

3) 举例说明 Unpooled 获取 Netty 的数据容器 ByteBuf 的基本使用

示例1：

```java
package com.learn.simple.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @ClassName: NettyButeBufOne
 * @Description: Unpooled 获取 Netty 的数据容器 ByteBuf 的基本使用
 * @Author: lin
 * @Date: 2020/10/19 17:20
 * History:
 * @<version> 1.0
 */
public class NettyByteBufOne {
    public static void main(String[] args) {

        //创建一个ByteBuf
        //说明
        //1. 创建对象，该对象包含一个数组arr，是一个byte[10]，可以存放10个字节的byte数组
        //2. 在Netty的buf中，不需要使用flip进行反转，
        //  因为底层维护了readerIndex和 writerIndex
        //3. 通过readerIndex和 writerIndex 以及capacity，将buffer分成三个区域
        // 0-----readerIndex: 表示已读取的区域
        // readerIndex----writerIndex: 表示可读的区域
        // writerIndex---capacity：表示可写的区域
        ByteBuf byteBuf = Unpooled.buffer(10);

        int count = 10;

        for (int i = 0; i <count ; i++) {
             byteBuf.writeByte(i);
        }

        System.out.println("capacity =:" + byteBuf.capacity());

        for (int i = 0; i <byteBuf.capacity() ; i++) {
            //这个方法是根据索引来获取，所以readIndex不会发生变化
            System.out.println(byteBuf.getByte(i));
        }

        for (int i = 0; i < byteBuf.capacity() ; i++) {
            //这个方法可以看到readIndex的变化
            System.out.println(byteBuf.readByte());
        }
        System.out.println("执行完毕.....");

    }
}

```



使用byteBuf.getByte() 根据索引来获取，不会是readerIndex发生变化。

![image](image/java-netty-unpooled-buffer-01.png )

如果使用byteBuf.readByte() ，可以看到这个方法使readerIndex发生了变化

![image](image/java-netty-unpooled-buffer-02.png)



注意：这个netty的 ByteBuf和  NIO的区别。 前面在使用NIO的ByteBuffer时，需要使用flip来进行反转。而Netty中的ByteBuf不需要，因为其底层维护了readerIndex和 writerIndex。














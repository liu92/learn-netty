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
//          byteBuffer.clear();

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

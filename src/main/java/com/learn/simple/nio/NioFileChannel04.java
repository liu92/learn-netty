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

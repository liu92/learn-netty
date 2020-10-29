package com.learn.simple.netty.protocoltcp;

/**
 * 协议包
 * @ClassName: MessageProtocol
 * @Description: 自定义发送消息协议
 * @Author: lin
 * @Date: 2020/10/21 22:05
 * History:
 * @<version> 1.0
 */
public class MessageProtocol {
    /**
     * 长度
     */
    private int length;
    /**
     * 内容
     */
    private byte[] content;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}

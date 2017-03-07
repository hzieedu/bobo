package com.dudu.bobo.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 *
 * @author liangy43
 *
 */
public class StreamReader {

    private final SocketChannel channel;

    private final int readBufferSize = 65536;
    private final ByteBuffer readBuffer = ByteBuffer.allocate(readBufferSize);
    private boolean corrupted = false;
    private int msgLen = 0;

    public StreamReader(SocketChannel channel) {
        this.channel = channel;
    }

    public Message read() {
        try {
            int count = channel.read(readBuffer);
            if (count > 0) {
                int len = 0;
                // 确定消息长度
                if (corrupted == false) {
                    // 不足4字节?
                    if (readBuffer.limit() - readBuffer.position() < 4) {
                        return null;
                    }
                    // 读取消息长度
                    len = readBuffer.getInt();
                } else {
                    len = msgLen;
                }

                // 如果读入的消息不完整, 则结束该连接的本次读取, 待下次读取
                if (readBuffer.remaining() < len) {
                    msgLen = len;
                    corrupted = true;
                    return null;
                } else {
                    msgLen = 0;
                    corrupted = false;
                }

                // 读取消息
                byte[] bytes = new byte[len];
                readBuffer.get(bytes);

                // 反序列为对象
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bais);
                Message message = (Message) ois.readObject();
                return message;
            }
        } catch (IOException ioex) {

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}

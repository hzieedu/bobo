/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dudu.bobo.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author liangy43
 *
 */
public class ChannelWrapper {

    private volatile boolean status = false;

    public boolean connected() {
        return this.status;
    }

    public void connected(boolean status) {
        this.status = status;
    }

    private final Node peer;       // 对端节点标识
    private final SocketChannel channel;
    private final StreamReader reader;
    private final Queue<Message> sendQueue = new LinkedList<Message>();      // 发送队列
    private final int writeBufferSize = 65536;
    private final ByteBuffer writeBuffer;

    public ChannelWrapper(Node peer, SocketChannel channel) {
        this.channel = channel;
        this.peer = peer;
        this.reader = new StreamReader(channel);
        this.writeBuffer = ByteBuffer.allocate(writeBufferSize);
    }

    public ChannelWrapper(SocketChannel channel) throws IOException {
        this.channel = channel;
        this.reader = new StreamReader(channel);
        this.peer = new NodeImpl((InetSocketAddress) channel.getRemoteAddress());
        this.writeBuffer = ByteBuffer.allocate(writeBufferSize);
    }

    public ChannelWrapper(SocketChannel channel, ByteBuffer writeBuffer) throws IOException {
        this.channel = channel;
        reader = new StreamReader(channel);
        peer = new NodeImpl((InetSocketAddress) channel.getRemoteAddress());
        this.writeBuffer = writeBuffer;
    }

    public Message read() throws DisconnectException {
        return reader.read();
    }

    public Node getPeer() {
        return this.peer;
    }

    public void sendMessage(Message message) {
        this.sendQueue.add(message);
    }

    public Message getSendMessage() {
        return this.sendQueue.poll();
    }

    public boolean hasMessageToSend() {
        return this.sendQueue.peek() != null;
    }

    public SocketChannel getChannel() {
        return this.channel;
    }

    public void write() {
        try {
            writeBuffer.clear();
            // 一次性发送
            for (Message message = sendQueue.poll(); message != null;) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(message);
                byte[] bytes = baos.toByteArray();
                writeBuffer.putInt(bytes.length);   // 消息长度作为报文的先导码, 消息长度不包含记录该长度的4字节
                writeBuffer.put(bytes);
                message = sendQueue.poll();
            }
            writeBuffer.flip();
            channel.write(writeBuffer);
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }
}

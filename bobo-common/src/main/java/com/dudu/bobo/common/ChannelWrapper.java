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

    SocketChannel channel;
    StreamReader reader;
    Queue<Message> sendQueue = new LinkedList<Message>();
    Node peer;
    private final int writeBufferSize = 65536;
    ByteBuffer writeBuffer = ByteBuffer.allocate(writeBufferSize);

    public ChannelWrapper(SocketChannel channel) throws IOException {
        this.channel = channel;
        reader = new StreamReader(channel);
        peer = new NodeImpl((InetSocketAddress) channel.getRemoteAddress());
        writeBuffer = ByteBuffer.allocate(65536);
    }

    public ChannelWrapper(SocketChannel channel, ByteBuffer writeBuffer) throws IOException {
        this.channel = channel;
        reader = new StreamReader(channel);
        peer = new NodeImpl((InetSocketAddress) channel.getRemoteAddress());
        this.writeBuffer = writeBuffer;
    }

    public Message read() {
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
                writeBuffer.putInt(bytes.length);
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


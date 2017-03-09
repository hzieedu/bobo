package com.dudu.bobo.client.support;

import java.nio.channels.SocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.dudu.bobo.client.ClientConnector;
import com.dudu.bobo.common.ChannelWrapper;
import com.dudu.bobo.common.DisconnectException;
import com.dudu.bobo.common.Message;
import com.dudu.bobo.common.Node;

/**
 * 通信客户端, 实现IO多路复用以及请求-应答的匹配
 *
 * @author liangy43
 *
 */
public class NioClientConnector implements ClientConnector, Runnable {

    private static volatile NioClientConnector instance = null;

    private NioClientConnector() {

    }

    public static NioClientConnector getNioClientConnector() {
        /*
         * double check lock
         */
        if (instance == null) {
            synchronized (NioClientConnector.class) {
                NioClientConnector client = new NioClientConnector();
                instance = client;
            }
        }

        return instance;
    }

    private final Map<Node, ChannelWrapper> channelMap
        = new HashMap<Node, ChannelWrapper>();

    // 用于请求应答匹配的future容器
    Map<Long, FutureImpl<Message>> pendingQueue
        = new ConcurrentHashMap<Long, FutureImpl<Message>>();

    private Selector selector = null;

    public void start() throws IOException {
        try {
            // 初始化selector对象
            selector = Selector.open();

            // 启动通信线程
            Thread t = new Thread(this);
            t.start();
        } catch (IOException e) {
            throw e;
        }
    }

    public int connect(Node node) throws Exception {
        try {
            synchronized (channelMap) {
                if (channelMap.containsKey(node)) {
                    return 0;
                }
                // 打开
                SocketChannel channel = SocketChannel.open();
                ChannelWrapper channelWrapper = new ChannelWrapper(node, channel);
                channelMap.put(node, channelWrapper);

                // 设置非阻塞
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_CONNECT, channelWrapper);
                // 连接
                channel.connect(node.getAddress());
            }
            
            return 0;
        } catch (ClosedChannelException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }        
    }

    /**
     *
     * @return
     */
    @Override
    public Future<Message> send(Node target, Message request) throws Exception {
        ChannelWrapper channel = channelMap.get(target);
        if (channel != null) {
            FutureImpl<Message> future = new FutureImpl<Message>();
            pendingQueue.put(request.getMessageId(), future);
            channel.sendMessage(request);
            return future;
        } else {
            return null;
        }
    }

    /**
     *
     */
    @Override
    public Message sendAndReceive(Node target, Message request) throws Exception {
        try {
            return sendAndReceive(target, request, Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Message sendAndReceive(Node target, Message request, long timeout) throws Exception {
        Future<Message> future = send(target, request);
        if (future != null) {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } else {
            return null;
        }
    }

    /**
     * IO多路复用处理线程
     */
    @Override
    public void run() {
        while (true) {
            try {
                // 不能无限等待, 因为存在没有可读事件但需要发送数据的情况
                selector.select(1000);

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    if (selectionKey.isConnectable()) {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        ChannelWrapper serverChannel = (ChannelWrapper) selectionKey.attachment();
                        System.out.println(String.format("server[%s] connected", channel.getRemoteAddress()));
                        serverChannel.setConnected(ChannelWrapper.CONNECTED);
                        channel.finishConnect();
                        channel.register(selector, SelectionKey.OP_READ, serverChannel);
                    } else if (selectionKey.isReadable()) {
                        ChannelWrapper serverChannel = (ChannelWrapper) selectionKey.attachment();
                        try {
                            Message response = serverChannel.read();
                            // 获取消息标识
                            long msgId = response.getMessageId();
                            FutureImpl<Message> future = pendingQueue.get(msgId);
                            // 从未决队列删除
                            pendingQueue.remove(msgId);
                            // 通知
                            future.signal(response);
                        } catch (DisconnectException de) {
                            System.out.println(String.format("server [%s] close the connection", serverChannel.getPeer()));
                            synchronized (channelMap) {
                                serverChannel.getChannel().close();
                                channelMap.remove(serverChannel.getPeer());
                            }
                        }
                    } else if (selectionKey.isWritable()) {
                        ChannelWrapper serverChannel = (ChannelWrapper) selectionKey.attachment();
                        serverChannel.write();
                        serverChannel.getChannel().register(selector, SelectionKey.OP_READ, serverChannel);
                    }
                }

                synchronized (channelMap) {
                    for (ChannelWrapper channelWrapper : channelMap.values()) {
                        // 连接建立后才关注写事件, 否则会写失败
                        if (channelWrapper.hasMessageToSend() == true
                            && channelWrapper.getConnected() == ChannelWrapper.CONNECTED) {
                            channelWrapper.getChannel().register(selector,
                                SelectionKey.OP_WRITE | SelectionKey.OP_READ, channelWrapper);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

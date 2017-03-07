package com.dudu.bobo.server.support;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.dudu.bobo.common.ChannelWrapper;
import com.dudu.bobo.common.Message;
import com.dudu.bobo.common.Node;
import com.dudu.bobo.server.Executor;
import com.dudu.bobo.server.ServingConnector;

/**
 *
 * @author liangy43
 *
 */
public class ServingConnectorImpl implements ServingConnector, Runnable {

    private static volatile ServingConnector instance = null;

    public static ServingConnector getServingConnector() {
        /*
         * double check lock
         */
        if (instance == null) {
            synchronized (ServingConnectorImpl.class) {
                ServingConnectorImpl server = new ServingConnectorImpl();
                server.init();
                instance = server;
            }
        }

        return instance;
    }

    private Executor executor;
    private Selector selector = null;

    private Node serverHost;

    Map<Node, ChannelWrapper> channelMap = new ConcurrentHashMap<Node, ChannelWrapper>();

    private void init() {
        try {
            // 初始化服务地址

            ServerSocketChannel servingChannel = ServerSocketChannel.open();
            servingChannel.configureBlocking(false);
            servingChannel.bind(serverHost.getAddress());

            // 通过open()方法找到Selector
            selector = Selector.open();
            // 注册到selector，等待连接  
            servingChannel.register(selector, SelectionKey.OP_ACCEPT);

            // 启动通信线程
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void response(Node target, Message message) {
        channelMap.get(target).sendMessage(message);
    }

    public void run() {
        while (true) {
            try {
                // 不能无限等待, 因为存在没有可读事件但需要发送数据的情况
                selector.select(100);

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();

                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);

                        ChannelWrapper clientChannel = new ChannelWrapper(client);
                        channelMap.put(clientChannel.getPeer(), clientChannel);

                        client.register(selector, SelectionKey.OP_READ, clientChannel);
                    } else if (selectionKey.isReadable()) {
                        ChannelWrapper clientChannel = (ChannelWrapper) selectionKey.attachment();
                        for (Message message = clientChannel.read();
                            message != null; message = clientChannel.read()) {
                            executor.dispatch(message, clientChannel.getPeer());
                        }
                    } else if (selectionKey.isWritable()) {
                        ChannelWrapper clientChannel = (ChannelWrapper) selectionKey.attachment();
                        clientChannel.write();
                        clientChannel.getChannel().register(selector, SelectionKey.OP_READ, clientChannel);
                    }
                }

                for (ChannelWrapper channelWrapper : channelMap.values()) {
                    if (channelWrapper.hasMessageToSend() == true) {
                        channelWrapper.getChannel().register(selector,
                            SelectionKey.OP_WRITE | SelectionKey.OP_READ, channelWrapper);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

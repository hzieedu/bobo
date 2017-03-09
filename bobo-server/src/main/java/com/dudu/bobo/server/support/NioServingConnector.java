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
import com.dudu.bobo.common.DisconnectException;
import com.dudu.bobo.server.Executor;
import com.dudu.bobo.server.ServerConnector;

/**
 *
 * @author liangy43
 *
 */
public class NioServingConnector implements ServerConnector, Runnable {

    private static volatile NioServingConnector instance = null;

    public static NioServingConnector getServingConnector() {
        /*
         * double check lock
         */
        if (instance == null) {
            synchronized (NioServingConnector.class) {
                NioServingConnector server = null;
                try {
                    server = new NioServingConnector();   
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                }
                instance = server;
                return instance;
            }
        }

        return instance;
    }

    private Executor executor;
    
    private final Selector selector;

    private final Map<Node, ChannelWrapper> channelMap
                                = new ConcurrentHashMap<Node, ChannelWrapper>();
    
    private Node servingAddress;

    private NioServingConnector() throws IOException {
        this.selector = Selector.open();
    }
    
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setServingHost(Node host) {
        this.servingAddress = host;
    }
    
    /**
     * 
     */
    public void start() {
        try {
            // 初始化服务地址
            ServerSocketChannel servingChannel = ServerSocketChannel.open();
            servingChannel.bind(servingAddress.getAddress());
            servingChannel.configureBlocking(false);


            // 注册到selector，等待连接  
            servingChannel.register(selector, SelectionKey.OP_ACCEPT);

            // 启动通信线程
            Thread t = new Thread(this);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void response(Node target, Message message) {
        channelMap.get(target).sendMessage(message);
    }

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
                    if (selectionKey.isAcceptable()) {
                        System.out.println("it seems that some one connected!");
                        ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel client = server.accept();
                        if (client != null) {
                            client.configureBlocking(false);

                            ChannelWrapper clientChannel = new ChannelWrapper(client);
                            channelMap.put(clientChannel.getPeer(), clientChannel);
                            clientChannel.setConnected(ChannelWrapper.CONNECTED);
                            System.out.println(String.format("accept connection from [%s]", clientChannel.getPeer()));
                            client.register(selector, SelectionKey.OP_READ, clientChannel);
                        }
                    } else if (selectionKey.isReadable()) {
                        ChannelWrapper clientChannel = (ChannelWrapper) selectionKey.attachment();
                        try {
                            Message message = clientChannel.read();
                        //    System.out.println(String.format("received message: %s", message));
                            executor.dispatch(message, clientChannel.getPeer());
                        } catch (DisconnectException de) {
                            System.out.println(String.format("client[%s] disconnect from server", clientChannel.getPeer()));
                            clientChannel.getChannel().close();
                            channelMap.remove(clientChannel.getPeer());
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

package com.dudu.bobo.client.support;

import java.nio.channels.SocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

import com.dudu.bobo.client.ClientConnector;
import com.dudu.bobo.common.ChannelWrapper;
import com.dudu.bobo.common.Message;
import com.dudu.bobo.common.Node;


/**
 * 通信客户端, 实现IO多路复用以及请求-应答的匹配
 * 
 * @author liangy43
 *
 */
public class ClientConnectorImpl implements ClientConnector, Runnable {
	
	private static volatile ClientConnector instance = null;
	
	public static ClientConnector getClient() {
		/*
		 * double check lock
		 */
		if (instance == null) {
			synchronized(ClientConnectorImpl.class) {
				ClientConnectorImpl client = new ClientConnectorImpl();
				client.init();
				instance = client;
			}
		}

		return instance;
	}

	private Map<Node, ChannelWrapper> channelMap
						= new ConcurrentHashMap<Node, ChannelWrapper>();

	// 用于请求应答匹配的future容器
	Map<Long, FutureImpl<Message>> pendingQueue
						= new ConcurrentHashMap<Long, FutureImpl<Message>>();
	
	private Selector selector = null;
	
	private void init() {
		try {
			// 初始化selector对象
			selector = Selector.open();
			
			// 启动通信线程
			Thread t = new Thread(this);
			t.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isConnected(Node node) {
		return channelMap.containsKey(node);
	}

	public int open(Node node) {
		try {
			// 打开
			SocketChannel channel = SocketChannel.open();
			// 设置非阻塞
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_CONNECT);
			// 连接
			channel.connect(node.getAddress());
		} catch (ClosedChannelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public Future<Message> send(Node target, Message request) {
		FutureImpl<Message> future = new FutureImpl<Message>();
		pendingQueue.put(request.getMessageId(), future);
		channelMap.get(target).sendMessage(request);
		return future;
	}
	
	/**
	 * 
	 */
	@Override
	public Message sendAndReceive(Node target, Message request) {
		try {
			return sendAndReceive(target, request, -1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Message sendAndReceive(Node target, Message request, long timeout) throws Exception {
		Future<Message> future = send(target, request);
		return future.get(timeout, TimeUnit.MILLISECONDS);
	}

	/**
	 * IO多路复用处理线程
	 */
	@Override
	public void run() {
		while (true) {
			try {
				// 不能无限等待, 因为存在没有可读事件但需要发送数据的情况
				selector.select(100);

				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey>iterator = selectionKeys.iterator();  
	            while (iterator.hasNext()) {
	            	SelectionKey selectionKey = iterator.next();

	            	if (selectionKey.isConnectable()) {
	                	SocketChannel channel = (SocketChannel) selectionKey.channel();
	                	ChannelWrapper channelWrapper = new ChannelWrapper(channel);
	                	channelMap.put(channelWrapper.getPeer(), channelWrapper);
	                	channel.register(selector, SelectionKey.OP_READ, channelWrapper);
	            	} else if (selectionKey.isReadable()) {
	            		ChannelWrapper serverChannel = (ChannelWrapper) selectionKey.attachment();
	            		for (Message response = serverChannel.read();
	            				response != null; response = serverChannel.read()) {
		                    // 获取消息标识
		                    long msgId = response.getMessageId();
		                    FutureImpl<Message> future = pendingQueue.get(msgId);       	
		                    // 从未决队列删除
		                    pendingQueue.remove(msgId);
		                    // 通知
		                    future.signal(response);		                    
	            		}
	                } else if (selectionKey.isWritable()) {
	                	ChannelWrapper serverChannel = (ChannelWrapper) selectionKey.attachment();
	                	serverChannel.write();
	                	serverChannel.getChannel().register(selector, SelectionKey.OP_READ, serverChannel);
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

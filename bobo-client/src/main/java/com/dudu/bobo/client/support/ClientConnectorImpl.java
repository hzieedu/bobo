package com.dudu.bobo.client.support;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.dudu.bobo.client.ClientConnector;
import com.dudu.bobo.common.Message;
import com.dudu.bobo.common.Node;

/**
 * 通信客户端, 实现通信以及请求-应答的匹配
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
	
	class MessageWithFuture {

		private Message message;

		transient FutureImpl future = new FutureImpl();
		
		public MessageWithFuture(long andIncrement, Object request) {
			message = new Message(andIncrement, request);
		}

		public Message getMessage() {
			return this.message;
		}
		
		public Long getMessageId() {
			return this.message.getMessageId();
		}
		
		FutureImpl getFuture() {
			return future;
		}
	}
	
	class ChannelWrapper {
		SocketChannel channel;
		List<MessageWithFuture> sendQueue = new LinkedList<MessageWithFuture>();
		Map<Long, MessageWithFuture> pendingQueue = new HashMap<Long, MessageWithFuture>();
	
		ChannelWrapper(SocketChannel channel) {
			this.channel = channel;
		}

		List<MessageWithFuture> getSendQueue() {
			return sendQueue;
		}

		Map<Long, MessageWithFuture> getPendingQueue() {
			return this.pendingQueue;
		}
		
		SocketChannel getChannel() {
			return channel;
		}
	}
	
	// 用于请求-应答匹配, 不能重复且存在竞争条件, 故而使用原子类型
	private AtomicLong reqId = new AtomicLong(0);
	
	private Map<Node, ChannelWrapper> channelMap
				= new ConcurrentHashMap<Node, ChannelWrapper>();
	
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
			channel.connect(((NodeImpl)node).getAddr());
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
	public Future<?> send(Node target, Object request) {
		MessageWithFuture req = new MessageWithFuture(reqId.getAndIncrement(), request);
		channelMap.get(target).getSendQueue().add(req);
		return req.getFuture();
	}
	
	/**
	 * 
	 */
	@Override
	public Object sendAndReceive(Node target, Object request) {
		try {
			return sendAndReceive(target, request, -1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Object sendAndReceive(Node target, Object request, long timeout) throws Exception {
		Future<?> future = send(target, request);
		return future.get(timeout, TimeUnit.MILLISECONDS);
	}

	/**
	 * IO多路复用处理线程
	 */
	@Override
	public void run() {
		ByteBuffer readBuffer = ByteBuffer.allocate(65536);
		boolean interruptd = false;
		int     msgLen = 0;
		ByteBuffer writeBuffer = ByteBuffer.allocate(65536);
		
		try {
			while (true) {
				// 不能无限等待, 因为存在没有可读事件但需要发送数据的情况
				selector.select(100);
				
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey>iterator = selectionKeys.iterator();  
	            while (iterator.hasNext()) {
	            	SelectionKey selectionKey = iterator.next();

	            	if (selectionKey.isConnectable()) {
	                	SocketChannel channel = (SocketChannel) selectionKey.channel();
	                	ChannelWrapper channelWrapper = new ChannelWrapper(channel);
	                	channelMap.put(new NodeImpl((InetSocketAddress)channel.getRemoteAddress()),
	                			channelWrapper);
	                	channel.register(selector, SelectionKey.OP_READ, channelWrapper);
	            	} else if (selectionKey.isReadable()) {
	                	SocketChannel channel = (SocketChannel) selectionKey.channel();

	            		int count = channel.read(readBuffer);
	                    if (count > 0) {
	                    	int len = 0;
	                    	// 确定消息长度
	                    	if (interruptd == false) {
	                    		// 不足4字节?
		                    	if (readBuffer.limit() - readBuffer.position() < 4) {
		                    		continue;
		                    	}
		                    	// 读取消息长度
		                    	len = readBuffer.getInt();	
	                    	} else {
	                    		len = msgLen;
	                    	}
	                    	
	                    	// 如果读入的消息不完整, 则结束该连接的本次读取, 待下次读取
	                    	if (readBuffer.remaining() < len) {
	                    		msgLen = len;
	                    		interruptd = true;
	                    		continue;
	                    	} else {
	                    		msgLen = 0;
	                    		interruptd = false;
	                    	}
	                    	
	                    	// 读取消息
	                    	byte[] bytes = new byte[len];
	                    	readBuffer.get(bytes);
	                    	
	                        // 反序列为对象
	                    	ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
	                        ObjectInputStream ois = new ObjectInputStream(bais);
	                        Object obj = null;
							try {
								obj = (Message)ois.readObject();
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                        Message response = (Message)obj;
	                        
	                    	// 获取对象标识
	                        long msgId = response.getMessageId();
	                    	
	                    	// 获取future
	                    	ChannelWrapper queuedChannel = (ChannelWrapper)selectionKey.attachment();
	                    	Map<Long, MessageWithFuture> pendingQueue = queuedChannel.getPendingQueue();
	                    	MessageWithFuture m = pendingQueue.get(msgId);
	                    	FutureImpl future = m.getFuture();
	    	                    	
	                    	// 从未决队列删除
	                    	pendingQueue.remove(m);

	    	                // 通知
	    	                future.signal(response.getMessageBody());
  	                    }
	                } else if (selectionKey.isWritable()) {
	                	ChannelWrapper channelWrapper = (ChannelWrapper)selectionKey.attachment();

	                	writeBuffer.clear();
	                	
	                	// 发送队列里的消息一次发送
	                	List<MessageWithFuture> sendQueue = channelWrapper.getSendQueue();
	                	for (MessageWithFuture req : sendQueue) {
	                		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                		ObjectOutputStream oos = new ObjectOutputStream(baos);
	                		oos.writeObject(req.getMessage());
	                		byte[] bytes = baos.toByteArray();
		                    writeBuffer.putInt(bytes.length);
		                    writeBuffer.put(bytes);
		                    System.out.println("客户端向服务器端发送数据--："+ req);
		                    // 从发送队列删除
		                    sendQueue.remove(req);
		                    // 添加到未决队列
		                    channelWrapper.getPendingQueue().put(req.getMessageId(), req);
	                	}
	                	writeBuffer.flip();
	                	channelWrapper.getChannel().write(writeBuffer);
	                	channelWrapper.getChannel().register(selector, SelectionKey.OP_READ, channelWrapper);
	                }
	            }

	            for (ChannelWrapper channelWrapper : channelMap.values()) {
	            	if (channelWrapper.getSendQueue().isEmpty() == false) {
	            		channelWrapper.getChannel().register(selector,
	            				SelectionKey.OP_WRITE | SelectionKey.OP_READ, channelWrapper);
	            	}
	            }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

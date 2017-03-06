package com.dudu.bobo.server.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dudu.bobo.common.Message;
import com.dudu.bobo.common.Node;
import com.dudu.bobo.common.RpcRequest;
import com.dudu.bobo.common.RpcResponse;
import com.dudu.bobo.server.ServingConnector;

/**
 * 
 * @author liangy43
 *
 */
public class ServingConnectorImpl implements ServingConnector, Runnable {

	private static volatile ServingConnector instance = null;
	
	public static ServingConnector getServer() {
		/*
		 * double check lock
		 */
		if (instance == null) {
			synchronized(ServingConnectorImpl.class) {
				ServingConnectorImpl server = new ServingConnectorImpl();
				server.init();
				instance = server;
			}
		}

		return instance;
	}

	private Selector selector = null;
	
	private InetSocketAddress servingAddress;

	private void init() {
		try {
			// 初始化服务地址
			
			ServerSocketChannel servingChannel = ServerSocketChannel.open();  
			servingChannel.configureBlocking(false);
			servingChannel.bind(servingAddress);

	        // 通过open()方法找到Selector
	        selector = Selector.open();  
	        // 注册到selector，等待连接  
	        servingChannel.register(selector, SelectionKey.OP_ACCEPT);  
	        
			// 启动通信线程
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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

	            	if (selectionKey.isAcceptable()) {
	            		ServerSocketChannel  server = (ServerSocketChannel) selectionKey.channel();
	            		SocketChannel client = server.accept();
	            		client.configureBlocking(false);
	            		
	            		channelMap.put(new NodeImpl((InetSocketAddress)client.getRemoteAddress()),
	                			channelWrapper);
	            		
	            		client.register(selector, SelectionKey.OP_READ, client);
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
	                        Message message = (Message)obj;
	                        
	                        RpcRequest request = (RpcRequest)message.getMessageBody();
	                        
	                    	// 处理请求

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

	public void response(Node target, RpcResponse response) {
		// TODO Auto-generated method stub
		
	}

}

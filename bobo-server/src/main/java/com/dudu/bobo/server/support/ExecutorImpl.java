package com.dudu.bobo.server.support;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.dudu.bobo.common.Message;
import com.dudu.bobo.common.Node;
import com.dudu.bobo.common.RpcRequest;
import com.dudu.bobo.common.RpcResponse;
import com.dudu.bobo.server.Executor;
import com.dudu.bobo.server.RpcSkeleton;
import com.dudu.bobo.server.ServingConnector;

/**
 * 处理线程池
 * 
 * @author liangy43
 *
 */
public class ExecutorImpl implements Executor {
	private static volatile ExecutorImpl instance = null;
	
	public static ExecutorImpl getExecutor(ServingConnector connector) {
		/*
		 * double check lock
		 */
		if (instance == null) {
			synchronized(ExecutorImpl.class) {
				ExecutorImpl executor = new ExecutorImpl(connector);
				executor.start();
				instance = executor;
			}
		}

		return instance;
	}
	
	private final ServingConnector		connector;
		
	BlockingQueue<MessageWithSource>	queue = new LinkedBlockingQueue<MessageWithSource>();

	RpcSkeletonContainer	serviceConainer;

	class MessageWithSource {
		Message		message;
		Node		node;
		public MessageWithSource(Message message, Node src) {
			this.message = message;
			this.node = src;
		}
	
		Message getMessage() {
			return this.message;
		}
		
		Node getNode() {
			return this.node;
		}
	}
	
	public ExecutorImpl(ServingConnector connector) {
		this.connector = connector;
	}

	class ProcessProcedure implements Runnable {
		public void run() {
			while (true) {
				try {
					MessageWithSource req = queue.poll(10, TimeUnit.MILLISECONDS);
					if (req == null) {
						continue;
					}
					Object messageBody = req.getMessage().getMessageBody();
					if (messageBody instanceof RpcRequest == false) {
						continue;
					}
					RpcRequest request = (RpcRequest)messageBody;
					RpcSkeleton skeleton = serviceConainer.getRpcSkeleton(request.getClassName());
					RpcResponse response = skeleton.handle(request);
					connector.response(req.getNode(), new Message(req.getMessage().getMessageId(), response));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
	}

	public void start() {
		for (int i = 0; i < 10; i++) {
			Thread t = new Thread(new ProcessProcedure());
			t.start();
		}
	}
	
	public void dispatch(Message message, Node src) throws IOException {
		queue.add(new MessageWithSource(message, src));
	}
}

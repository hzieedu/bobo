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
		
	BlockingQueue<Message>	queue = new LinkedBlockingQueue<Message>();

	RpcSkeletonContainer	serviceConainer;
	
	public ExecutorImpl(ServingConnector connector) {
		this.connector = connector;
	}

	class ProcessProcedure implements Runnable {
		public void run() {
			while (true) {
				try {
					Message req = queue.poll(10, TimeUnit.MILLISECONDS);
					if (req == null) {
						continue;
					}
					Object messageBody = req.getMessageBody();
					if (messageBody instanceof RpcRequest == false) {
						continue;
					}
					RpcRequest request = (RpcRequest)messageBody;
					RpcSkeleton skeleton = serviceConainer.getRpcSkeleton(request.getClassName());
					RpcResponse response = skeleton.handle(request);
					connector.response((Node)req.getAttachment(), new Message(req.getMessageId(), response));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
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
		message.setAttachment(src);
		queue.add(message);
	}
}

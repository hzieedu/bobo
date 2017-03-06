package com.dudu.bobo.server.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
	
	public static ExecutorImpl getExecutor() {
		/*
		 * double check lock
		 */
		if (instance == null) {
			synchronized(ExecutorImpl.class) {
				ExecutorImpl executor = new ExecutorImpl();
				executor.start();
				instance = executor;
			}
		}

		return instance;
	}
	
	ServingConnector			server = ServingConnectorImpl.getServer();
		
	BlockingQueue<RpcContext>	queue = new LinkedBlockingQueue<RpcContext>();

	RpcSkeletonContainer		serviceConainer;

	class RpcContext {
		RpcRequest	rpcRequest;
		Node		node;
		public RpcContext(RpcRequest rpcRequest, Node src) {
			this.rpcRequest = rpcRequest;
			this.node = src;
		}
	
		RpcRequest getRpcRequest() {
			return this.rpcRequest;
		}
		
		Node getNode() {
			return this.node;
		}
	}
	
	class ProcessProcedure implements Runnable {
		public void run() {
			while (true) {
				try {
					RpcContext r = queue.poll(10, TimeUnit.MILLISECONDS);
					if (r == null) {
						continue;
					}
					RpcRequest request = r.getRpcRequest();
					RpcSkeleton skeleton = serviceConainer.getRpcSkeleton(request.getClassName());
					RpcResponse response = skeleton.handle(request);
					server.response(r.getNode(), response);
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
	
	public void dispatch(RpcRequest rpcRequest, Node src) throws Exception {
		queue.add(new RpcContext(rpcRequest, src));
	}
}

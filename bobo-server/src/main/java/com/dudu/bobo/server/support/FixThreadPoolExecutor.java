package com.dudu.bobo.server.support;

import com.dudu.bobo.server.RpcSkeletonContainer;
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
import com.dudu.bobo.server.ServerConnector;

/**
 * 处理线程池
 * 
 * @author liangy43
 *
 */
public class FixThreadPoolExecutor implements Executor {
    
	private static volatile FixThreadPoolExecutor instance = null;
	
	public static FixThreadPoolExecutor getExecutor() {
		/*
		 * double check lock
		 */
		if (instance == null) {
			synchronized(FixThreadPoolExecutor.class) {
				FixThreadPoolExecutor executor = new FixThreadPoolExecutor();
				executor.start();
				instance = executor;
			}
		}

		return instance;
	}
	
	private ServerConnector                 connector;
		
	private final BlockingQueue<Message>    queue = new LinkedBlockingQueue<Message>();

	RpcSkeletonContainer                    serviceConainer;
	
	private FixThreadPoolExecutor() {
	}

    public void setServerConnector(ServerConnector connector) {
        this.connector = connector;
    }

    public void setRpcSkeletonContainer(RpcSkeletonContainer container) {
        this.serviceConainer = container;
    }
    
    /**
     * 
     */
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
                        System.out.println("received a non-rpcrequest message: " + messageBody.getClass().getSimpleName());
						continue;
					}
					RpcRequest request = (RpcRequest)messageBody;
					RpcSkeleton skeleton = serviceConainer.getRpcSkeleton(request.getClassName());
                    if (skeleton != null) {
                        RpcResponse response = skeleton.handle(request);
                        connector.response((Node)req.getAttachment(), new Message(req.getMessageId(), response));
                    } else {
                        
                    }
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

    @Override
	public void dispatch(Message message, Node src) throws IOException {
		message.setAttachment(src);
		queue.add(message);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}

package com.dudu.bobo.client.support;

import java.io.IOException;

import com.dudu.bobo.client.ProxyFactory;

/**
 * 
 * @author liangy43
 *
 */
public class RpcContext {

	private static volatile RpcContext instance = null;

    private RpcContext() {
    	
    }

    public static RpcContext getRpcContext() {
        /*
         * double check lock
         */
        if (instance == null) {
            synchronized (RpcContext.class) {
            	RpcContext context = new RpcContext();
                instance = context;
            }
        }

        return instance;
    }

	private NioClientConnector		clientConnector = NioClientConnector.getNioClientConnector();
	
	public void start() {
		try {
			clientConnector.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    public ProxyFactory getProxyFactory() {
        return new ProxyFactoryImpl();
    }
}

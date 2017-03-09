package com.dudu.bobo.client.support;

import java.io.IOException;

import com.dudu.bobo.client.ProxyFactory;
import com.dudu.bobo.common.Node;

/**
 * 
 * @author liangy43
 *
 */
public class BoboClient implements ProxyFactory {

	private static volatile BoboClient instance = null;

    private BoboClient() {
    	
    }

    public static BoboClient getBoboClient() {
        /*
         * double check lock
         */
        if (instance == null) {
            synchronized (BoboClient.class) {
            	BoboClient context = new BoboClient();
                instance = context;
            }
        }

        return instance;
    }

	private final NioClientConnector		clientConnector = NioClientConnector.getNioClientConnector();
	
	public void start() {
		try {
			clientConnector.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    public <T> T refer(Class<T> interfaceClass) throws Exception {
        return ProxyFactoryImpl.refer(interfaceClass);
    }
    
    public <T> T referBypass(Class<T> interfaceClass, Node server) throws Exception {
        return ProxyFactoryImpl.referBypass(interfaceClass, server);
    }
}

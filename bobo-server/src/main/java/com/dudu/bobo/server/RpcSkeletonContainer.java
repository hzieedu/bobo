package com.dudu.bobo.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dudu.bobo.server.RpcSkeleton;

/**
 * 
 * @author liangy43
 *
 */
public class RpcSkeletonContainer {

	private static volatile RpcSkeletonContainer instance = null;
	
	public static RpcSkeletonContainer getRpcSkeletonContainer() {
		/*
		 * double check lock
		 */
		if (instance == null) {
			synchronized(RpcSkeletonContainer.class) {
				RpcSkeletonContainer container = new RpcSkeletonContainer();
				// TODO 初始化
				instance = container;
			}
		}

		return instance;
	}

	private final Map<String, RpcSkeleton>	skeletonMap = new ConcurrentHashMap<String, RpcSkeleton>();

    private RpcSkeletonContainer() {
        
    }
    
	/**
	 *
     * @param rpcSkeleton
	 */
	public void registerRpcSkeleton(RpcSkeleton rpcSkeleton) {
        System.out.println("register service interface" + rpcSkeleton.getInterfaceClass().getName());
		skeletonMap.put(rpcSkeleton.getInterfaceClass().getName(), rpcSkeleton);
	}

	/**
	 * 
     * @param className
     * @return 
	 */
	public RpcSkeleton getRpcSkeleton(String className) {
		return skeletonMap.get(className);
	}
}

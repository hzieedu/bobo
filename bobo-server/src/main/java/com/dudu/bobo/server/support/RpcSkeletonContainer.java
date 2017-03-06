package com.dudu.bobo.server.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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
				// TODO ≥ı ºªØ
				instance = container;
			}
		}

		return instance;
	}

	Map<String, RpcSkeleton>	skeletonMap = new ConcurrentHashMap<String, RpcSkeleton>();

	/**
	 *
	 */
	public void registerRpcSkeleton(RpcSkeleton rpcSkeleton) {
		skeletonMap.put(rpcSkeleton.getClass().getName(), rpcSkeleton);
	}

	/**
	 * 
	 */
	public RpcSkeleton getRpcSkeleton(String className) {
		return skeletonMap.get(className);
	}
}

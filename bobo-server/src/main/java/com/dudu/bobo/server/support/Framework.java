package com.dudu.bobo.server.support;

import com.dudu.bobo.server.RpcSkeleton;

/**
 * 
 * @author liangy43
 *
 */
public class Framework {

	/**
	 * 
	 * @param service
	 * @param clazz
	 */
	public void export(Object service, Class<?> clazz) {
		if (clazz.isInstance(service) == false) {
			return;
		}
	
		// 添加skeleton
		RpcSkeleton rpcSkeleton = new RpcSkeletonImpl(service, clazz);
		
		// 发布
	}
	
	/**
	 * 
	 */
	public void main() {
		// 通信模块
		
		// 处理模块
		
		//
	}
}

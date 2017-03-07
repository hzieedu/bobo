package com.dudu.bobo.server;

import com.dudu.bobo.common.RpcRequest;
import com.dudu.bobo.common.RpcResponse;

/**
 * 
 * @author liangy43
 *
 */
public interface RpcSkeleton {
	
	/**
	 * 
	 * @return
	 */
	Class<?> getInterfaceClass();
	
	/**
	 * 
	 * @throws Exception
	 */
	RpcResponse handle(RpcRequest request) throws Exception;
}
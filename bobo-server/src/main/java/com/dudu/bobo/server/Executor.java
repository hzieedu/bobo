package com.dudu.bobo.server;

import com.dudu.bobo.common.Node;
import com.dudu.bobo.common.RpcRequest;

/**
 * 
 * @author liangy43
 *
 */
public interface Executor {

	/**
	 * 
	 * @param rpcRequest
	 * @param src
	 * @throws Exception
	 */
	void dispatch(RpcRequest rpcRequest, Node src) throws Exception;
}

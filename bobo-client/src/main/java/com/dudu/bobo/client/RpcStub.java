package com.dudu.bobo.client;

import com.dudu.bobo.common.RpcRequest;
import com.dudu.bobo.common.RpcResponse;

/**
 * Ô¶¶Ë¹ý³Ì´æ¸ù
 * 
 * @author liangy43
 *
 */
public interface RpcStub {

	/**
	 * 
	 */
	RpcResponse call(RpcRequest request) throws Exception;
}

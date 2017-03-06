package com.dudu.bobo.client;

import java.util.concurrent.Future;

import com.dudu.bobo.common.Node;

/**
 * 
 * @author liangy43
 *
 */
public interface ClientConnector {

	/**
	 * 
	 * @param target
	 * @param request
	 * @return
	 */
	Future<?> send(Node target, Object request);

	/**
	 * 
	 * @return
	 */
	Object sendAndReceive(Node target, Object request) throws Exception;
	
	/**
	 * 
	 * @return
	 */
	Object sendAndReceive(Node target, Object request, long timeout) throws Exception;	
}

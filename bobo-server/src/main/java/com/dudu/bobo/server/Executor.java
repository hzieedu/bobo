package com.dudu.bobo.server;

import com.dudu.bobo.common.Node;

import java.io.IOException;

import com.dudu.bobo.common.Message;

/**
 * 
 * @author liangy43
 *
 */
public interface Executor {

	/**
	 * 
	 * @param message
	 * @param src
	 * @throws Exception
	 */
	void dispatch(Message message, Node src) throws IOException;
}

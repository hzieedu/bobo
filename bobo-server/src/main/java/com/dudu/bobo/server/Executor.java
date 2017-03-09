package com.dudu.bobo.server;

import java.io.IOException;

import com.dudu.bobo.common.Node;
import com.dudu.bobo.common.Message;
import com.dudu.bobo.common.Lifecycle;

/**
 * 
 * @author liangy43
 *
 */
public interface Executor extends Lifecycle {
	/**
	 * 
	 * @param message
	 * @param src
	 * @throws IOException
	 */
	void dispatch(Message message, Node src) throws IOException;
}

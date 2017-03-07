package com.dudu.bobo.server;

import com.dudu.bobo.common.Node;
import com.dudu.bobo.common.Message;

/***
 * 
 * @author liangy43
 *
 */
public interface ServingConnector {

	/**
	 * 
	 */
	void response(Node target, Message message);
}

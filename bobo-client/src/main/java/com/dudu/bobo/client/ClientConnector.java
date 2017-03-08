package com.dudu.bobo.client;

import java.util.concurrent.Future;

import com.dudu.bobo.common.Message;
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
	 * @return
	 */
	int connect(Node target);
	
    /**
     *
     * @param target
     * @param request
     * @return
     */
    Future<?> send(Node target, Message request);

    /**
     *
     * @return
     */
    Message sendAndReceive(Node target, Message request) throws Exception;

    /**
     *
     * @return
     */
    Message sendAndReceive(Node target, Message request, long timeout) throws Exception;
}

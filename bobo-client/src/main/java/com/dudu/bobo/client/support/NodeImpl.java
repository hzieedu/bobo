package com.dudu.bobo.client.support;

import java.net.InetSocketAddress;

import com.dudu.bobo.common.Node;

/**
 * 
 * @author liangy43
 *
 */
public class NodeImpl implements Node {

	private InetSocketAddress addr;

	public NodeImpl(InetSocketAddress addr) {
		this.addr = addr;
	}
	
	public InetSocketAddress getAddr() {
		return this.addr;
	}
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	@Override
	public boolean equals(Object node) {
		return this.toString().equals(node.toString());
	}
	
	@Override
	public String toString() {
		return addr.toString();
	}
}

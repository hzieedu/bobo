package com.dudu.bobo.common;

import java.net.InetSocketAddress;

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
	
	@Override
	public InetSocketAddress getAddress() {
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

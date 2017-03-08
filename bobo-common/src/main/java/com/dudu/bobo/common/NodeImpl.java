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

    public NodeImpl(String hostname, int port) {
        this.addr = new InetSocketAddress(hostname, port);
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
    
    /**
     * 地址需要深复制
     * @return
     * @throws CloneNotSupportedException 
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        NodeImpl clone = (NodeImpl) super.clone();
        clone.addr = new InetSocketAddress(this.addr.getAddress(), this.addr.getPort());
        return clone;
    }
}

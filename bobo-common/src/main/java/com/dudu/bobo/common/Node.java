package com.dudu.bobo.common;

import java.net.InetSocketAddress;

/**
 *
 * @author liangy43
 *
 */
public interface Node extends Cloneable {

    /**
     *
     * @return
     */
    InetSocketAddress getAddress();
}

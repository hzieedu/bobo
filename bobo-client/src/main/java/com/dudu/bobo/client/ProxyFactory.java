package com.dudu.bobo.client;

import com.dudu.bobo.common.Node;

/**
 *
 * @author liangy43
 *
 */
public interface ProxyFactory {

    /**
     *
     * @param interfaceClass
     * @return
     * @throws Exception
     */
    <T> T refer(Class<T> interfaceClass) throws Exception;
    
    /**
     * 
     * @param interfaceClass
     * @param server
     * @return
     * @throws Exception
     */
    <T> T referBypass(Class<T> interfaceClass, Node server) throws Exception;
}

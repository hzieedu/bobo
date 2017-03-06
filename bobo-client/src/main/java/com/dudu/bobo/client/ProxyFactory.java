package com.dudu.bobo.client;

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
}

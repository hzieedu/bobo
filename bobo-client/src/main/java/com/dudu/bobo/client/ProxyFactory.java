package com.dudu.bobo.client;

/**
 * 
 * @author liangy43
 *
 */
public interface ProxyFactory<T> {
	/**
	 * 
	 * @param interfaceClass
	 * @return
	 * @throws Exception
	 */
    T refer(Class<T> interfaceClass) throws Exception;
}

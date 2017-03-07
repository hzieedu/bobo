package com.dudu.bobo.spring;

import org.springframework.beans.factory.InitializingBean;

/**
 * 
 * @author liangy43
 *
 * @param <T>
 */
public class ServiceBean<T> implements InitializingBean {

    private String			interfaceName;
    
    private Class<?>		interfaceClass;

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}

}

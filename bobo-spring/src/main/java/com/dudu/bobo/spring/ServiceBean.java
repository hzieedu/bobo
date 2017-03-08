package com.dudu.bobo.spring;

import org.springframework.beans.factory.InitializingBean;

import com.dudu.bobo.server.support.Framework;

/**
 *
 * @author liangy43
 *
 * @param <T>
 */
public class ServiceBean<T> implements InitializingBean {

    private String      interfaceName;

    private Class<?>    interfaceClass;
    
    private Framework   framework;
    
    private T           ref;

    /**
     * 通过该方法导出服务
     * 
     * @throws Exception 
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        framework.export(this.ref, interfaceClass);
    }

}

package com.dudu.bobo.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.dudu.bobo.client.ProxyFactory;
import com.dudu.bobo.client.support.ProxyFactoryImpl;

/**
 *
 * @author liangy43
 *
 * @param <T>
 */
public class ReferenceBean<T> implements FactoryBean, InitializingBean {

    private String id;

    private String interfaceName;

    private Class<?> interfaceClass;

    private ProxyFactory proxyFactory = new ProxyFactoryImpl();

    private volatile T proxy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getObject() throws Exception {
        if (proxy != null) {
            return proxy;
        }

        if (interfaceClass == null) {
            throw new Exception("no such interface");
        }

        proxy = (T) proxyFactory.refer(interfaceClass);
        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    void setProxyFactory(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        interfaceClass = Class.forName(interfaceName);
        System.out.println(String.format("代理[%s]引用的接口[%s]找到了!", id, interfaceName));
    }
}

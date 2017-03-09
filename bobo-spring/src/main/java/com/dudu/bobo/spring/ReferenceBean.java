package com.dudu.bobo.spring;

import java.util.Map;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.dudu.bobo.client.support.RpcContext;
import com.dudu.bobo.client.ProxyFactory;
import com.dudu.bobo.common.InetNode;

/**
 *
 * @author liangy43
 *
 * @param <T>
 */
public class ReferenceBean<T> implements FactoryBean, InitializingBean, ApplicationContextAware {

    private String          id;

    private String          interfaceName;

    private Class<?>        interfaceClass;
    
    private String          bypass;
    
    private int             timeout = -1;

    private ApplicationContext  applicationContext;
    
    private ProxyFactory    proxyFactory;

    private volatile T      proxy;

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

    public void setBypass(String bypass) {
        this.bypass = bypass;
    }
    
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException {
        this.applicationContext = applicationContext;
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

        if (bypass.isEmpty() == true) {
            proxy = (T) proxyFactory.refer(interfaceClass);
        } else {
            String address = bypass.trim();
            String[] addr = address.split(":");
            if (addr.length == 2) {
                InetNode target = new InetNode(addr[0], Integer.parseInt(addr[1]));
                proxy = (T) proxyFactory.referBypass(interfaceClass, target);
            } else {
                throw new IllegalArgumentException();
            }
        }
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

    @Override
    public void afterPropertiesSet() throws Exception {
        interfaceClass = Class.forName(interfaceName);
        System.out.println(String.format("代理[%s]引用的接口[%s]找到了!", id, interfaceName));
        
        Map map = applicationContext.getBeansOfType(RpcContext.class);
        if (map.values().isEmpty() == false) {
            RpcContext rpcContext = (RpcContext)map.values().iterator().next();
            proxyFactory = rpcContext.getProxyFactory();
        }
    }
}

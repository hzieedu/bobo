package com.dudu.bobo.spring;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.dudu.bobo.server.support.BoboServer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author liangy43
 *
 * @param <T>
 */
public class ServiceBean<T> implements InitializingBean, ApplicationContextAware {

    private String              interfaceName;

    private Class<T>            interfaceClass;
    
    private ApplicationContext  applicationContext;
    
    private String              implementationBean;
    
    private T                   ref;

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public void setImplementationBean(String implementationBean) {
        this.implementationBean = implementationBean;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 通过该方法导出服务
     * 
     * @throws Exception 
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Map map = applicationContext.getBeansOfType(BoboServer.class);
        if (map.values().isEmpty() == false) {
            BoboServer framework = (BoboServer)map.values().iterator().next();            
            ref = (T)applicationContext.getBean(implementationBean);
            interfaceClass = (Class<T>) Class.forName(interfaceName);
            framework.export(this.ref, interfaceClass);
        }
    }

}

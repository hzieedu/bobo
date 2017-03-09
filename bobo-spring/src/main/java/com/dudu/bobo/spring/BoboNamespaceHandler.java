package com.dudu.bobo.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 *
 * @author liangy43
 *
 */
public class BoboNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("client", new ClientBeanDefinitionParser());
        registerBeanDefinitionParser("reference", new ReferenceBeanDefinitionParser(ReferenceBean.class));
        registerBeanDefinitionParser("server", new ServerBeanDefinitionParser());
        registerBeanDefinitionParser("service", new ServiceBeanDefinitionParser(ServiceBean.class));
    }
}

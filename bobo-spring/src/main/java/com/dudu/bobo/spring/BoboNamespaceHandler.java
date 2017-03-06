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
        registerBeanDefinitionParser("reference", new ReferenceBeanDefinitionParser(ReferenceBean.class));
        registerBeanDefinitionParser("service", new ServiceBeanDefinitionParser(ServiceBean.class));
	}

}

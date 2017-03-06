package com.dudu.bobo.server.support.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 
 * @author liangy43
 *
 */
public class BoboNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
        registerBeanDefinitionParser("service", new BoboBeanDefinitionParser(BoboServiceBean.class));
	}

}

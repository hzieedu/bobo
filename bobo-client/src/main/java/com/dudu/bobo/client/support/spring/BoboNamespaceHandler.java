package com.dudu.bobo.client.support.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 
 * @author liangy43
 *
 */
public class BoboNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
        registerBeanDefinitionParser("reference", new BoboBeanDefinitionParser(BoboReferenceBean.class));	
	}

}

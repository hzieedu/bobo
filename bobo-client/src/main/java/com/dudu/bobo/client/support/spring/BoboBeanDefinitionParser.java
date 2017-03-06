package com.dudu.bobo.client.support.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * 
 * @author liangy43
 *
 */
public class BoboBeanDefinitionParser implements BeanDefinitionParser {

	private final Class<?> beanClass;
	
	public BoboBeanDefinitionParser(Class<?> beanClass) {
		this.beanClass = beanClass;
	}
	
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		//
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		
		//
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setLazyInit(false);
        
        //
        String id = element.getAttribute("id");
        beanDefinition.getPropertyValues().addPropertyValue("id", id);
        beanDefinition.getPropertyValues().addPropertyValue("interfaceName", element.getAttribute("interface"));
        parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);

        return beanDefinition;
	}

}

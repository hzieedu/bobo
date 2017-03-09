package com.dudu.bobo.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * 
 * @author liangy43
 */
public class ServiceBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;

    public ServiceBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        //
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.getPropertyValues().addPropertyValue("interfaceName", element.getAttribute("interface"));
        beanDefinition.getPropertyValues().addPropertyValue("implementationBean", element.getAttribute("implement"));
        parserContext.getRegistry().registerBeanDefinition("server-" + new java.util.Random().nextLong(), beanDefinition);

        return beanDefinition;
    }

}

package com.dudu.bobo.spring;

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
public class ReferenceBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;

    public ReferenceBeanDefinitionParser(Class<?> beanClass) {
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
        try {
            int timeout = Integer.parseInt(element.getAttribute("timout"));
            beanDefinition.getPropertyValues().addPropertyValue("timeout", timeout);
        } catch (NumberFormatException e) {
            
        }        
        beanDefinition.getPropertyValues().addPropertyValue("bypass", element.getAttribute("bypass"));
        parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);

        return beanDefinition;
    }

}

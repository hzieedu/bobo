/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dudu.bobo.spring;

import java.util.Random;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.dudu.bobo.client.support.RpcContext;

/**
 *
 * @author liangy43
 */
public class ClientBeanDefinitionParser implements BeanDefinitionParser {
    
    private final Class<?> beanClass = RpcContext.class;

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setLazyInit(false);
        parserContext.getRegistry().registerBeanDefinition("boboClient-" + new Random().nextLong(), beanDefinition);
        beanDefinition.setFactoryMethodName("getRpcContext");
        beanDefinition.setInitMethodName("start");
        return beanDefinition;
    }
}

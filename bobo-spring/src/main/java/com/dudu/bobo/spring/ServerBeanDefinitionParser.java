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

import com.dudu.bobo.server.support.BoboServer;

/**
 *
 * @author liangy43
 */
public class ServerBeanDefinitionParser implements BeanDefinitionParser {
    private final Class<?> beanClass = BoboServer.class;
    
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setLazyInit(false);
        parserContext.getRegistry().registerBeanDefinition("boboServer-" + new Random().nextLong(), beanDefinition);
        beanDefinition.setFactoryMethodName("getBoboServer");
        beanDefinition.setInitMethodName("startServing");
        return beanDefinition;
    }    
}

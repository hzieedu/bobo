package com.dudu.boboserverdemo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author liangy43
 */
public class BoboServerDemo {

	public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context
            = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
        context.start();
	}
}

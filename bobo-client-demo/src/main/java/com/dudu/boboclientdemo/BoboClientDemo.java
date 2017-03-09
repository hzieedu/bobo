package com.dudu.boboclientdemo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dudu.bobo.demo.intf.DemoService;

public class BoboClientDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context
            = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
        context.start();

        DemoService demoService = (DemoService) context.getBean("demoService");
        DemoService demoService2 = (DemoService) context.getBean("demoService");
        
        try {
            for (int i = 0; i < 5; i++) {
        		System.out.println(demoService.hello("liangy43@chinaunicom.cn"));
        		System.out.println(demoService2.hello("yongyong"));
        		Thread.sleep(1000);
            }
        } catch (InterruptedException ie) {
        
        }
    }
}

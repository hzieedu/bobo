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
        try {
            demoService.hello("liangy43");
        } catch (Exception ex) {
            System.out.println(ex);
            //	ex.printStackTrace();
        }

        DemoService demoService2 = (DemoService) context.getBean("demoService");
        try {
            demoService2.hello("yongyong");
        } catch (Exception ex) {
            System.out.println(ex);
            //	ex.printStackTrace();
        }
    }
}

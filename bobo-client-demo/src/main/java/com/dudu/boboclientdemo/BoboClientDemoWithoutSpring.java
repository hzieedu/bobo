package com.dudu.boboclientdemo;

import com.dudu.bobo.client.support.ProxyFactoryImpl;
import com.dudu.bobo.client.support.RpcContext;
import com.dudu.bobo.common.Node;
import com.dudu.bobo.common.InetNode;
import com.dudu.bobo.demo.intf.DemoService;

/**
 * 
 * @author liangy43
 *
 */
public class BoboClientDemoWithoutSpring {

	public static void main(String[] args) {
        try {
            RpcContext context = RpcContext.getRpcContext();
            context.start();
    		
    		ProxyFactoryImpl factory = new ProxyFactoryImpl();
    		Node server = new InetNode("10.37.241.81", 28811);
    		
    		Thread.sleep(1000);
    
        	DemoService demoService = (DemoService) factory.referBypass(DemoService.class, server);
            DemoService demoService2 = (DemoService) factory.referBypass(DemoService.class, server);   
        	for (int i = 0; i < 5; i++) {
        		System.out.println(demoService.hello("liangy43"));
        		System.out.println(demoService2.hello("yongyong"));
        		Thread.sleep(1000);
        	}            
            
            System.in.read();
        } catch(InterruptedException ie) {
        	
        } catch (Exception ex) {
            System.out.println(ex);
            //	ex.printStackTrace();
        }		
	}
}

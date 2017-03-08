package com.dudu.boboserverdemo;

import com.dudu.bobo.server.support.Framework;

public class BoboServerDemo {

	public static void main(String[] args) throws Exception {
		Framework framework = Framework.getFramework();
		
		framework.startServing();
		
        framework.export(new DemoServiceImpl(), com.dudu.bobo.demo.intf.DemoService.class);
        
		System.in.read();
	}
}

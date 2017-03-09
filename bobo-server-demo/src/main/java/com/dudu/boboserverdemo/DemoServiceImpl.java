package com.dudu.boboserverdemo;

import com.dudu.bobo.demo.intf.DemoService;

public class DemoServiceImpl implements DemoService {

	/**
	 * 
	 */
	public String hello(String name) {
        System.out.println("Hi, li mei, my name is " + name);
		return String.format("Hi, li mei, my name is %s", name);
	}

}

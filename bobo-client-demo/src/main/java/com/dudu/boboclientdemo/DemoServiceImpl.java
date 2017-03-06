package com.dudu.boboclientdemo;

public class DemoServiceImpl implements DemoService {

	/**
	 * 
	 */
	public String hello(String name) {
		return String.format("Hi, li mei, my name is %s", name);
	}

}

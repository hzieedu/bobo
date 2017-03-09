/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dudu.boboserverdemo;

import com.dudu.bobo.server.support.BoboServer;
import com.dudu.bobo.demo.intf.DemoService;

/**
 *
 * @author liangy43
 */
public class BoboServerDemoWithoutSpring {
    
    public static void main(String[] args) throws Exception {
		BoboServer server = BoboServer.getBoboServer();
		
		server.startServing();
		
        server.export(new DemoServiceImpl(), DemoService.class);
        
		System.in.read();
    }
}

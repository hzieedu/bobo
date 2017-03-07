package com.dudu.bobo.client.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dudu.bobo.client.RpcStub;
import com.dudu.bobo.client.ServiceDiscovery;
import com.dudu.bobo.client.ServiceEvent;
import com.dudu.bobo.common.ServiceInfo;

/**
 * 
 * @author liangy43
 *
 */
public class RpcStubContainer implements ServiceDiscovery{

	private static volatile RpcStubContainer instance = null;

	public static RpcStubContainer getStubHandler() {
		/*
		 * double check lock
		 */
		if (instance == null) {
			synchronized(RpcStubContainer.class) {
				RpcStubContainer container= new RpcStubContainer();
				container.init();
				instance = container;
			}
		}

		return instance;
	}
	
	public Map<String, RpcStub> map = new ConcurrentHashMap<String, RpcStub>();

	public RpcStub getRpcStub(Class<?> clazz) {
		return map.get(clazz.getName());
	}

	public void init() {
		List<ServiceInfo> serviceinfo = query();
		for (ServiceInfo info : serviceinfo) {
			BalanceableRpcStub stub = new BalanceableRpcStub();
			stub.init(info);
			map.put(info.getInterfaceName(), stub);
		}
	}

	void run() {
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<ServiceInfo> query() {
		List<ServiceInfo> list = new ArrayList<ServiceInfo>();
		return list;
	}

	@Override
	public void onServiceListener(ServiceEvent event) {
		
	}
}

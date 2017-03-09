package com.dudu.bobo.client.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dudu.bobo.client.RpcStub;
import com.dudu.bobo.client.ServiceDiscovery;
import com.dudu.bobo.client.ServiceEvent;
import com.dudu.bobo.common.Node;
import com.dudu.bobo.common.ServiceInfo;

/**
 *
 * @author liangy43
 *
 */
public class RpcStubContainer implements ServiceDiscovery {

    private static volatile RpcStubContainer instance = null;

    private RpcStubContainer() {
    	
    }

    public static RpcStubContainer getRpcStubContainer() {
        /*
         * double check lock
         */
        if (instance == null) {
            synchronized (RpcStubContainer.class) {
                RpcStubContainer container = new RpcStubContainer();
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

    public RpcStub getRpcStubBypass(Class<?> clazz, Node server) {
        try {
            RpcStub stub = new RpcStubImpl(server);
            return stub;
        } catch (Exception e) {
            return null;
        }
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

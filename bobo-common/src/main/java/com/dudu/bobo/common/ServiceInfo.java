package com.dudu.bobo.common;

import java.util.List;

/**
 * 
 * @author liangy43
 */
public class ServiceInfo {

    private String interfaceName;

    private List<Node> serviceNodes;

    public String getInterfaceName() {
        return this.interfaceName;
    }

    public List<Node> getServiceNodes() {
        return this.serviceNodes;
    }
}

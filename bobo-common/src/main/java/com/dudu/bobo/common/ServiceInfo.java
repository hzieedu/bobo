package com.dudu.bobo.common;

import java.util.List;

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

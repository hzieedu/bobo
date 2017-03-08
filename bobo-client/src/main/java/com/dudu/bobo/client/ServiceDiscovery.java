package com.dudu.bobo.client;

import java.util.List;

import com.dudu.bobo.common.ServiceInfo;

/**
 * 服务发现
 *
 * @author liangy43
 *
 */
public interface ServiceDiscovery {

    /**
     *
     */
    List<ServiceInfo> query();

    /**
     *
     */
    void onServiceListener(ServiceEvent event);
}

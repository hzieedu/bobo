package com.dudu.bobo.server;

/**
 * 服务注册
 * 
 * @author liangy43
 *
 */
public interface ServiceRegister {

    /**
     * 
     * @param clazz 
     */
    void publishService(Class<?> clazz);
}

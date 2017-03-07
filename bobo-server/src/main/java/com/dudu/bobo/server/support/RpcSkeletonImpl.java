package com.dudu.bobo.server.support;

import java.lang.reflect.Method;

import com.dudu.bobo.common.RpcRequest;
import com.dudu.bobo.common.RpcResponse;
import com.dudu.bobo.server.RpcSkeleton;

/**
 * 
 * @author liangy43
 *
 */
public class RpcSkeletonImpl implements RpcSkeleton{
	
	private final Class<?>	clazz;
	
	private final Object 	service;
	
	public RpcSkeletonImpl(Object service, Class<?> clazz) {
		this.service = service;
		this.clazz = clazz;
	}

	public Class<?> getInterfaceClass() {
		return this.clazz;
	}
	
	/**
	 * 
	 */
	public RpcResponse handle(RpcRequest request) throws Exception {  
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] arguments = request.getArguments();

        Method method = clazz.getMethod(methodName, parameterTypes);
        Object result = method.invoke(service, arguments);
        RpcResponse response = new RpcResponse();
        response.setResult(result);
        return response;
    } 
}

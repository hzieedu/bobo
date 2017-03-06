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
	
	private Class<?>	clazz;
	
	private Object 		service;
	
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

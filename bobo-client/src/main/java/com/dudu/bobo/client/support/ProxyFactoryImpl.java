package com.dudu.bobo.client.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import com.dudu.bobo.client.ProxyFactory;
import com.dudu.bobo.client.RpcStub;
import com.dudu.bobo.common.RpcRequest;
import com.dudu.bobo.common.RpcResponse;

public class ProxyFactoryImpl implements ProxyFactory {

	private RpcStubContainer stubHandler = RpcStubContainer.getStubHandler();
	@Override
    @SuppressWarnings("unchecked")
	public <T> T refer(Class<T> interfaceClass) throws Exception {
        System.out.println("creating remote service stub" + interfaceClass.getName());  
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
        									new Class<?>[] { interfaceClass },
        									new RpcHandler(interfaceClass));
    }

	
	
	class RpcHandler implements InvocationHandler {

		private RpcStub stub;
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        	
			try {
				if (stub == null) {
					throw new NoSuchServiceException();
				}
	
	        	// 构造RpcRequest
	        	RpcRequest request = new RpcRequest(method, args);
	        	
	        	// 调用rpc stub
	        	RpcResponse response = stub.call(request);
	
	            // 解析应答
	            Object result = response.getResult();
	            	
	    		System.out.println("Call remote procedure: " + method + ", " + Arrays.asList(args) + ", result: " + result);
	    			
	            return result;
			} catch (NoSuchServiceException ex) {
				throw ex.getCause();
			}
		}
		
		public RpcHandler(Class<?> interfaceClass) {
        	// 查找相应rpc stub
			stub = stubHandler.getRpcStub(interfaceClass);
		}

	}
}

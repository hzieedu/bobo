package com.dudu.bobo.client.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.dudu.bobo.client.RpcStub;
import com.dudu.bobo.common.Node;
import com.dudu.bobo.common.RpcRequest;
import com.dudu.bobo.common.RpcResponse;

public class ProxyFactoryImpl {

    @SuppressWarnings("unchecked")
    public static <T> T refer(Class<T> interfaceClass) throws Exception {
        System.out.println("creating remote service stub" + interfaceClass.getName());
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
            new Class<?>[]{interfaceClass},
            new RpcHandleWrapper(interfaceClass));
    }

    @SuppressWarnings("unchecked")
    public static <T> T referBypass(Class<T> interfaceClass, Node server) throws Exception {
        System.out.println("creating remote service stub" + interfaceClass.getName());
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
            new Class<?>[]{interfaceClass},
            new RpcHandleWrapper(interfaceClass, server));
    }

    static class RpcHandleWrapper implements InvocationHandler {
        private RpcStub stub;

        public RpcHandleWrapper(Class<?> interfaceClass) {
            stub = RpcStubContainer.getRpcStubContainer().getRpcStub(interfaceClass);
        }

        public RpcHandleWrapper(Class<?> interfaceClass, Node server) {
            stub = RpcStubContainer.getRpcStubContainer().getRpcStubBypass(interfaceClass, server);
        }
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                if (stub == null) {
                    throw new NoSuchServiceException();
                }

                RpcRequest request = new RpcRequest(method, args);
                RpcResponse response = stub.call(request);
                Object result = response.getResult();

                return result;
            } catch (NoSuchServiceException ex) {
                throw ex;
            }
        }
    }
}

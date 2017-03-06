package com.dudu.bobo.common;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 
 * @author liangy43
 *
 */
public class RpcRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String		className;
	
	private String		methodName;
	
	private Class<?>[]	parameterTypes;
	
	private Object[]	arguments;
	
	public RpcRequest(Method method, Object[] arguments) {
		this.className = method.getDeclaringClass().getName();
		this.methodName = method.getName();
		this.parameterTypes = method.getParameterTypes();
		this.arguments = arguments == null ? new Object[0] : arguments;
	}

	public String getClassName() {
		return this.className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes == null ? new Class<?>[0] : parameterTypes;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments == null ? new Object[0] : arguments;
    }
}

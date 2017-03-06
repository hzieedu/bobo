package com.dudu.bobo.common;

import java.io.Serializable;

/**
 * 
 * @author liangy43
 *
 */
public class RpcResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Object	result;
	
	public Object getResult() {
		return this.result;
	}

    public void setResult(Object result) {
        this.result = result;
    }
}

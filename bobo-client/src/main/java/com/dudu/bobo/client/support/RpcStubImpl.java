package com.dudu.bobo.client.support;

import com.dudu.bobo.client.ClientConnector;
import com.dudu.bobo.client.RpcStub;
import com.dudu.bobo.common.Node;
import com.dudu.bobo.common.RpcRequest;
import com.dudu.bobo.common.RpcResponse;

public class RpcStubImpl implements RpcStub {
	
	private Node	target;
	
	private ClientConnector	client;

	public RpcStubImpl(Node node) {
		this.target = node;
	}

	public int init() {
		return 0;
	}

	@Override
	public RpcResponse call(RpcRequest request) {
		try {
			return (RpcResponse)client.sendAndReceive(target, request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}

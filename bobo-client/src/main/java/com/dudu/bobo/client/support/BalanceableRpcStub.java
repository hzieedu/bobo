package com.dudu.bobo.client.support;

import java.util.List;

import com.dudu.bobo.client.BalancePolicy;
import com.dudu.bobo.client.RpcStub;
import com.dudu.bobo.common.Node;
import com.dudu.bobo.common.RpcRequest;
import com.dudu.bobo.common.RpcResponse;
import com.dudu.bobo.common.ServiceInfo;

public class BalanceableRpcStub implements RpcStub {

	String 			interfaceName;
	BalancePolicy	policy = new SimpleBalancePolicy();
	
	public void init(ServiceInfo info) {
		List<Node> list = info.getServiceNodes();
		for (Node node: list) {
			RpcStubImpl stub = new RpcStubImpl(node);
			// TODO: 如果此时初始化失败, 又当如何?
			if (stub.init() == 0) {
				policy.join(stub);
			}
		}
	}
	
	@Override
	public RpcResponse call(RpcRequest request) throws Exception {
		System.out.println("自动负载客户端生效!");
	
		RpcStub stub = policy.select();
		if (stub == null) {
			System.out.println("没有提供者?");
			throw new Exception("没有提供者");
		}
		RpcResponse response;
		try {
			response = stub.call(request);
			return response;
		} catch (Exception ex) {
			policy.doubt(stub);
			return null;
		}
	}

}

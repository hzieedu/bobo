package com.dudu.bobo.client.support;

import java.util.List;
import java.util.ArrayList;

import com.dudu.bobo.client.BalancePolicy;
import com.dudu.bobo.client.RpcStub;

enum StubStatus {
	AVAILABLE,
	DOUBTFUL,
}

class StubWrapper {
	StubStatus	status = StubStatus.AVAILABLE;
	RpcStub		stub;
	
	public StubWrapper(RpcStub stub) {
		this.stub = stub;
	}
}

public class SimpleBalancePolicy implements BalancePolicy {

	private List<StubWrapper>	list = new ArrayList<StubWrapper>();
	
	private volatile int		index = 0;
	
	@Override
	public synchronized void join(RpcStub stub) {
		list.add(new StubWrapper(stub));		
	}

	@Override
	public synchronized RpcStub select() {
		if (list.size() == 0) {
			return null;
		} else {
			if (++index < list.size()) {
				return list.get(index).stub;
			} else {
				index = 0;
				return list.get(index).stub;
			}
		}
	}

	@Override
	public synchronized void remove(RpcStub stub) {
		
	}

	@Override
	public synchronized void doubt(RpcStub stub) {

	}

	@Override
	public synchronized void unDoubt(RpcStub stub) {
		
	}

}

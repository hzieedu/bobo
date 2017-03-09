package com.dudu.bobo.client.support;

import java.util.List;

import com.dudu.bobo.client.BalancePolicy;
import com.dudu.bobo.client.RpcStub;
import com.dudu.bobo.common.Node;
import com.dudu.bobo.common.RpcRequest;
import com.dudu.bobo.common.RpcResponse;
import com.dudu.bobo.common.ServiceInfo;

public class BalanceableRpcStub implements RpcStub {

    private String              interfaceName;
    private final BalancePolicy policy = new SimpleBalancePolicy();

    public void init(ServiceInfo info) {
        List<Node> list = info.getServiceNodes();
        for (Node node : list) {
            try {
                RpcStubImpl stub = new RpcStubImpl(node);
                // TODO: 如果此时初始化失败, 又当如何?
                if (stub.init() == 0) {
                    policy.join(stub);
                }
            } catch (Exception e) {
                // TODO: 这样是否合适?
                continue;
            }
        }
    }

    @Override
    public RpcResponse call(RpcRequest request) throws Exception {
        System.out.println("自动负载客户端生效!");

        RpcStub stub = policy.select();
        if (stub == null) {
            System.out.println("没有提供者?");
            throw new Exception("no service provider");
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

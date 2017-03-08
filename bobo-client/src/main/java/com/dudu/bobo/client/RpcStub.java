package com.dudu.bobo.client;

import com.dudu.bobo.common.RpcRequest;
import com.dudu.bobo.common.RpcResponse;

/**
 * 远端过程存根
 *
 * @author liangy43
 *
 */
public interface RpcStub {

    /**
     *
     */
    RpcResponse call(RpcRequest request) throws Exception;
}

package com.dudu.bobo.client;

/**
 * 负载均衡策略
 *
 * @author liangy43
 *
 */
public interface BalancePolicy {

    /**
     * 将存根对象加入集群
     */
    void join(RpcStub stub);

    /**
     *
     */
    void remove(RpcStub stub);

    /**
     *
     */
    RpcStub select();

    /**
     * 置疑指定存根
     *
     */
    void doubt(RpcStub stub);

    /**
     * 取消指定存根置疑
     *
     */
    void unDoubt(RpcStub stub);
}

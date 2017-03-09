package com.dudu.bobo.server.support;

import  java.util.Properties;    
import  java.io.InputStream;    
import  java.io.IOException;

import com.dudu.bobo.common.Node;
import com.dudu.bobo.common.InetNode;
import com.dudu.bobo.server.RpcSkeletonContainer;
import com.dudu.bobo.server.RpcSkeleton;
import com.dudu.bobo.server.ServiceRegister;

/**
 *
 * @author liangy43
 *
 */
public class BoboServer {

    private static volatile BoboServer instance = null;

    private BoboServer() {    
    }

    public static BoboServer getBoboServer() {
        /*
         * double check lock
         */
        if (instance == null) {
            synchronized (NioServingConnector.class) {
                BoboServer server = new BoboServer();   
                instance = server;
                return instance;
            }
        }

        return instance;
    }

    // 全局模块
    private ServiceRegister         serviceRegister;
    private RpcSkeletonContainer    skeletonContainer;
    private FixThreadPoolExecutor   executor;
    private NioServingConnector     serverConnector;

    private static final Node             servingHost;

    // 从properties配置文件读取服务地址及端口
    static {
        Properties prop = new Properties();
        String hostName = "0.0.0.0";
        String port = "28800";
        try  {
        	InputStream in = BoboServer.class.getResourceAsStream("/server.properties");
        	if (in != null) {
	            prop.load(in);
	            hostName = prop.getProperty("ip").trim();
	            port = prop.getProperty("port").trim();
        	}
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	System.out.println(String.format("服务地址: [%s:%s]", hostName, port));
            servingHost = new InetNode(hostName, Integer.parseInt(port));
        }
    }
    /**
     * 导出并发布服务
     * 
     * @param service
     * @param clazz
     */
    public void export(Object service, Class<?> clazz) {
        if (clazz.isInstance(service) == false) {
            return;
        }

        // 添加skeleton
        RpcSkeleton rpcSkeleton = new RpcSkeletonImpl(service, clazz);
        skeletonContainer.registerRpcSkeleton(rpcSkeleton);

        // 发布
    //    serviceRegister.publishService(rpcSkeleton.getClass());
    }

    /**
     * 导出服务
     * 
     * @param service
     * @param clazz
     */
    public void exportWithoutPublish(Object service, Class<?> clazz) {
        if (clazz.isInstance(service) == false) {
            return;
        }

        // 添加skeleton
        RpcSkeleton rpcSkeleton = new RpcSkeletonImpl(service, clazz);
        skeletonContainer.registerRpcSkeleton(rpcSkeleton);
    }    

    /**
     * 实例化, 装配及启动
     */
    public void startServing() {
        executor = FixThreadPoolExecutor.getExecutor();
        serverConnector = NioServingConnector.getServingConnector();
        skeletonContainer = RpcSkeletonContainer.getRpcSkeletonContainer();
        
        executor.setRpcSkeletonContainer(skeletonContainer);
        executor.setServerConnector(serverConnector);
        serverConnector.setExecutor(executor);
        serverConnector.setServingHost(servingHost);

        executor.start();
        serverConnector.start();
    }
}

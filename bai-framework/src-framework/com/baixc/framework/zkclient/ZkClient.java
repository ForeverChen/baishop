package com.baixc.framework.zkclient;

import org.I0Itec.zkclient.exception.ZkException;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.zookeeper.CreateMode;

public class ZkClient extends org.I0Itec.zkclient.ZkClient {
	
    public ZkClient(String serverstring) {
    	super(serverstring);
    }

    public ZkClient(String zkServers, int connectionTimeout) {
    	super(zkServers, connectionTimeout);
    }

    public ZkClient(String zkServers, int sessionTimeout, int connectionTimeout) {
    	super(zkServers, sessionTimeout, connectionTimeout);
    }

    public ZkClient(String zkServers, int sessionTimeout, int connectionTimeout, ZkSerializer zkSerializer) {
    	super(zkServers, sessionTimeout, connectionTimeout, zkSerializer);
    }
    

    /**
     * 创建节点
     * @param path 路径
     * @param mode 模式
     * @param createParents 是否递归创建父节点
     */
    public void create(final String path, final CreateMode mode, final boolean createParents) throws ZkInterruptedException, IllegalArgumentException, ZkException, RuntimeException {
        try {
            super.create(path, null, mode);
        } catch (ZkNodeExistsException e) {
            if (!createParents) {
                throw e;
            }
        } catch (ZkNoNodeException e) {
            if (!createParents) {
                throw e;
            }
            String parentDir = path.substring(0, path.lastIndexOf('/'));
            this.create(parentDir, mode, createParents);
            this.create(path, mode, createParents);
        }
    }
    
    /**
     * 当znode不存在时返回空
     */
    public <T extends Object> T readData(String path, T defaultValue) {
    	if(this.exists(path)){
    		return super.readData(path);
    	}else{
    		return defaultValue;
    	}
    }
        
    /**
     * 向节点中写数据，当znode不存在时创建，如果存在则等同于{@link ZkClient#writeData(String, Object)}
     */
    public void writeData(final String path, Object data, final CreateMode mode) {
    	try{
    		super.writeData(path, data);
    	} catch (ZkNoNodeException e) {
    		this.create(path, mode, true);
    		super.writeData(path, data);
        }
    }

    
    
}

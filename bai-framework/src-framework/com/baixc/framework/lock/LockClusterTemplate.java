package com.baixc.framework.lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 集群锁模版类
 * 
 * @author Linpn
 */
public class LockClusterTemplate {

	protected final Log logger = LogFactory.getLog(getClass());

	// zookeeper连接串
	private String connectString = "127.0.0.1:2181";
	// 超时时间，单位：毫秒
	private int timeout = 40 * 1000;
	// 是否启用集群锁，如果false则集群锁无效
	private boolean enable = true;

	public LockClusterTemplate() {
	}

	public LockClusterTemplate(String connectString) {
		this.connectString = connectString;
	}

	public LockClusterTemplate(String connectString, int timeout) {
		this.connectString = connectString;
		this.timeout = timeout;
	}
	
	

	/**
	 * 执行集群同步锁，排队形式
	 * @param key 集群锁的键，标识是哪个集群锁，任意值(不允许'/'字符)，建议使用类的限定名，但要保证唯一
	 * @param action 回调方法
	 * @return 返回是否执行成功
	 */
	public synchronized void lock(String key, LockClusterCallback action) {		
		try {
			//不启动集群锁
			if(!enable){
				action.doInLockCluster();
				return;
			}
			
			
			//组装zookeeper的path
			String path = "/LockCluster-" + key;
			
			// 创建集群锁对象
			LockCluster lockCluster = new LockCluster(connectString, path, timeout);

			try {
				// 上锁
				lockCluster.lock();
				logger.info("lock path : " + path);

				// 执行过程
				action.doInLockCluster();

			} catch (Exception e) {
				throw e;
			} finally {
				// 解锁
				lockCluster.unLock();
				logger.info("unlock path : " + path);

				// 关闭连接
				lockCluster.close();
				logger.info("close ZooKeeper");
			}

		} catch (Exception e) {
			throw new LockClusterException(e);
		}
	}

	
	/**
	 * zookeeper连接串
	 * @return
	 */
	public String getConnectString() {
		return connectString;
	}

	/**
	 * zookeeper连接串
	 * @param connectString
	 */
	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	/**
	 * 超时时间，单位：毫秒
	 * @return
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * 超时时间，单位：毫秒
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * 是否启用集群锁，如果false则集群锁无效
	 * @return
	 */
	public boolean isEnable() {
		return enable;
	}

	/**
	 * 是否启用集群锁，如果false则集群锁无效
	 * @param enable
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

}

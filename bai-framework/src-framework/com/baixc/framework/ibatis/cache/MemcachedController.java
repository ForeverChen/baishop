package com.baixc.framework.ibatis.cache;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baixc.framework.utils.MD5Encryptor;
import com.ibatis.sqlmap.engine.cache.CacheController;
import com.ibatis.sqlmap.engine.cache.CacheKey;
import com.ibatis.sqlmap.engine.cache.CacheModel;

/**
 * Cache implementation for using Memcached with iBATIS
 */
public class MemcachedController implements CacheController {

	protected final Log logger = LogFactory.getLog(getClass());
	
	private static MemcachedClient memcached;	
	private static final String IBATIS_MEMCACHED_KEYS = "IBATIS_MEMCACHED_KEYS";
	private static final String IBATIS_MEMCACHED_DATA = "IBATIS_MEMCACHED_DATA";
	public static final Object NULL_OBJECT = new String("SERIALIZABLE_NULL_OBJECT");


	@Override
	public void setProperties(Properties props) {
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public void flush(CacheModel cacheModel) {
		try{
			String memkeys = IBATIS_MEMCACHED_KEYS +"|" + cacheModel.getId();
			Set<String> keySet = (Set<String>)memcached.get(memkeys);
			
			if(keySet!=null){
				int len = keySet.size();

				//如果Mencached删除成功，则移除key
				for(Iterator<String> it=keySet.iterator();it.hasNext();){
					String key = it.next();
					
					//删除不成功，重试3次
					for(int i=0;i<3;i++){
						if(memcached.delete(key)){
							//成功则移除key
							it.remove();
							break;
						}else{
							//不成功,判断缓存是否为null
							Object value = memcached.get(key);
							if(value==null){
								//如果null，则移除key
								it.remove();
								break;
							}else{
								//如果不为null，则暂停500毫秒再重试
								logger.error("ibatis memcached 键为"+ key +"的缓存删除不成功，正在重试 "+ i +"...");
								Thread.sleep(500);
								continue;
							}
						}
					}
				}
				if(len != keySet.size()){
					memcached.set(memkeys, 0, keySet);
				}
			}
			
		}catch(Exception e){
			logger.error("ibatis memcached 清除缓存异常，为保证数据的完整性，系统将清除(flushAll)所有缓存。 异常信息如下，请及时排查", e);
			
			//清除所有异常缓存
			try {
				memcached.flushAll();
			} catch (Exception e1) {
				logger.error("清除所有异常缓存异常", e1);
			}
		}
	}

	@Override
	public Object getObject(CacheModel cacheModel, Object cacheKey) {
		String key = this.getKey(cacheModel, cacheKey);

		try {
			Object value = memcached.get(key);
			if(NULL_OBJECT.equals(value))
				value = null;
			return value;
			
        } catch (Exception e) {
			logger.error("ibatis memcached 获取键为"+ key +"的缓存失败，系统将直接查询数据。 异常信息如下，请及时排查", e);
        }
		
        return null;  
	}

	@Override
	public void putObject(CacheModel cacheModel, Object cacheKey, Object object) {
		String key = this.getKey(cacheModel, cacheKey);

        try {
        	memcached.set(key, (int)(cacheModel.getFlushInterval()/1000), object);
        	this.putKey(cacheModel, key);
        	
        } catch (Exception e) {
			logger.error("ibatis memcached 插入键为"+ key +"的缓存失败。 异常信息如下，请及时排查", e);
        }  
	}

	@Override
	public Object removeObject(CacheModel cacheModel, Object cacheKey) {
		String key = this.getKey(cacheModel, cacheKey);

        try {
            Object value = memcached.get(key);
            
            //删除不成功，重试3次
            for(int i=0;i<3;i++){
				if(memcached.delete(key)){
					//成功则移除key
		            this.removeKey(cacheModel, key);
					break;
				}else{
					//不成功,判断缓存是否为null
					if(value==null){
						//如果null，则移除key
			            this.removeKey(cacheModel, key);
						break;
					}else{
						//如果不为null，则暂停500毫秒再重试
						logger.error("ibatis memcached 键为"+ key +"的缓存删除不成功，正在重试 "+ i +"...");
						Thread.sleep(500);
						continue;
					}
				}
			}

            return value;
            
        } catch (Exception e) {
			logger.error("ibatis memcached 删除键为"+ key +"的缓存失败。 异常信息如下，请及时排查", e);
        }  
  
        return null; 
	}
	
	

	/**
	 * 获取转化后的key， 如果参数cacheKey为已经转化过的key，则直接返回
	 */
	private String getKey(CacheModel cacheModel, Object cacheKey) {
		if(cacheKey instanceof CacheKey){
			String ckey = cacheKey.toString();		
			ckey = ckey.replaceAll("^([\\-\\d]+\\|)([\\-\\d]+\\|)", "");
			ckey = ckey.replaceAll("^(.+\\|)*(\\w+\\.\\w+\\|)(\\d+\\|)(.*)", "$1$2$4");
			
			StringBuffer key = new StringBuffer(IBATIS_MEMCACHED_DATA +"|");
	        key.append(cacheModel.getId() + "|");
	        key.append(MD5Encryptor.encryptHex(ckey));
	        
			//if (logger.isDebugEnabled()){
			//	logger.debug("++++ ibatis缓存原始KEY:"+ cacheKey +" +++");
			//	logger.debug("++++ ibatis缓存去本地化的KEY:"+ ckey +" +++");
			//}
	        
	        return key.toString();
		}

		return cacheKey.toString();
    }	
	
	private void putKey(CacheModel cacheModel, Object cacheKey) throws TimeoutException, InterruptedException, MemcachedException{
		String memkeys = IBATIS_MEMCACHED_KEYS +"|" + cacheModel.getId();
		
		Set<Object> keySet = memcached.get(memkeys);
		if(keySet==null){
			keySet = new HashSet<Object>();
		}		

		String key = this.getKey(cacheModel, cacheKey);
		keySet.add(key);
		memcached.set(memkeys, 0, keySet);
	}
	
	private void removeKey(CacheModel cacheModel, Object cacheKey) throws TimeoutException, InterruptedException, MemcachedException{
		String memkeys = IBATIS_MEMCACHED_KEYS +"|" + cacheModel.getId();
		Set<Object> keySet = memcached.get(memkeys);
		
		if(keySet!=null){
			String key = this.getKey(cacheModel, cacheKey);
			for(Object _key : keySet){
				if(_key.equals(key)){
					keySet.remove(_key);
					memcached.set(memkeys, 0, keySet);
				}
			}
		}
	}
	
	
		
	public static MemcachedClient getMemcached() {
		return MemcachedController.memcached;
	}

	public void setMemcached(MemcachedClient memcached) {
		MemcachedController.memcached = memcached;
	}
	
}

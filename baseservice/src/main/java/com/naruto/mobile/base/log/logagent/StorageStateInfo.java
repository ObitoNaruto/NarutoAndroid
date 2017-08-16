package com.naruto.mobile.base.log.logagent;

import java.util.HashMap;

/**
 *	存储当前埋点信息,作为埋点的临时存储
 */
public class StorageStateInfo {
	private static StorageStateInfo storageStateInfo;
	private HashMap<String, String> currentState = new HashMap<String, String>();
	
	private StorageStateInfo(){};
	
	public static synchronized StorageStateInfo getInstance(){
		if(storageStateInfo == null){
			storageStateInfo = new StorageStateInfo();
		}
		return storageStateInfo;
	}
	
	/**
	 * 客户端初始时注册客户端信息
	 * @param productID
	 * @param productVersion
	 * @param clientID
	 * @param alipayId
	 * @param uuId
	 * @param modelVersion
	 */
	public void registClient(String alipayId,String productVersion,String clientID,String uuId,String modelVersion){
		currentState.put(Constants.STORAGE_PRODUCTID, alipayId);
		currentState.put(Constants.STORAGE_PRODUCTVERSION, productVersion);
		currentState.put(Constants.STORAGE_MODELVERSION, modelVersion);
		currentState.put(Constants.STORAGE_CLIENTID, clientID);
		currentState.put(Constants.STORAGE_UUID, uuId);
	}
	
	/**
	 * 清除客户端注册的信息
	 */
	public void unRegistClient(){
		currentState.clear();
	}
	
	public void putValue(String key,String value){
		currentState.put(key, value);
	}
	
	public String getValue(String key){
		String value = currentState.get(key);
		return value == null ? "" : value;
	}
	
	/**
	 * 删除客户端注册的指定信息
	 * @param key 信息Key
	 */
	public void removeValue(String key){
		currentState.remove(key);
	}
	
	/**
	 * 清除除用户ID和产品ID以外的数据
	 */
	public void clearValue(){
		String productID = getValue(Constants.STORAGE_PRODUCTID);
		String productVersion = getValue(Constants.STORAGE_PRODUCTVERSION);
		String clientID = getValue(Constants.STORAGE_CLIENTID);
		String uuId = getValue(Constants.STORAGE_UUID);
		String modelVersion = getValue(Constants.STORAGE_MODELVERSION);
		currentState.clear();
		currentState.put(Constants.STORAGE_PRODUCTID, productID);
		currentState.put(Constants.STORAGE_PRODUCTVERSION, productVersion);
		currentState.put(Constants.STORAGE_CLIENTID, clientID);
		currentState.put(Constants.STORAGE_UUID, uuId);
		currentState.put(Constants.STORAGE_MODELVERSION, modelVersion);
	}
	
	/**
	 * 是否已经注册
	 * @return true 已经注册 | false 还未注册
	 */
	public boolean isRegisted() {
        return !currentState.isEmpty();
    }
	
}

package com.naruto.mobile.base.log.logagent;

import java.util.UUID;

import android.content.Context;

//import com.naruto.mobile.framework.rpc.myhttp.common.info.AppInfo;
//import com.naruto.mobile.framework.rpc.myhttp.common.info.DeviceInfo;


public class AlipayLogAgent {
	

	/**
	 * @author fukang.lfk
	 *
	 */
	public final static class AlipayLogInfo{
		/**
		 * {@link BehaviourIdEnum} 行为ID
		 */
		public BehaviourIdEnum behaviourIdEnum;	//行为ID
		
		/**
		 * {@link String} 行为状态
		 */
		public String behaviourStatus;			//行为状态
		
		/**
		 * {@link String} 状态消息
		 */
		public String statusMessage;			//状态消息
		
		/**
		 * {@link String} 应用ID
		 */
		public String appID;					//应用ID
		
		/**
		 * {@link String} 应用版本号
		 */
		public String appVersion;				//应用版本号
		
		/**
		 * {@link String} 当前视图ID
		 */
		public String viewID;					//当前视图ID
		
		/**
		 * {@link String} 上一个视图ID
		 */
		public String refViewID;				//上一个视图ID
		
		/**
		 * {@link String} 埋点ID
		 */
		public String seed;						//埋点ID
		
		/**
		 * {@link String} URL地址
		 */
		public String url;						//URL地址
		
		/**
		 * {@link String} 行为分类(默认是u)
		 */
		public String behaviourPro;				//行为分类(默认是u)
		
		/**
		 * {@link String} 日志分类(默认是c)
		 */
		public String logPro;					//日志分类(默认是c)
		
		/**
		 * {@link String} 扩展参数
		 */
		public String[] extendParams;			//扩展参数
	}
	/*
	 * 异常记录
	 */
	public static void onError(Context context, String errorStr, String ViewID, String exceptionType){
		try{
			   if(context==null||!Constants.LOG_SWITCH){
				   return;
			   }
			   new StorageManager(context, errorStr, exceptionType ,ViewID, Constants.STORAGE_TYPE_ERROR).writeLog();
	   }catch(Exception e){
//			   LogCatLog.printStackTraceAndMore(e);
	   }
	}
	
	/*
	 * 上传日志至服务器
	 */
	public static void uploadLog(Context context){
		try{
			   if(context==null||!Constants.LOG_SWITCH){
				   return;
			   }
			   LogSendManager.uploadLog(context);
	   }catch(Exception e){
//			   LogCatLog.printStackTraceAndMore(e);
	   }
	}
	
	/**
	 * 自动埋点采集接口
	 * @param context
	 * @param xmlVersion
	 * @param behaviourIdEnum
	 * @param autoMessage
	 */
	public static void writeLog(Context context, String xmlVersion,
			BehaviourIdEnum behaviourIdEnum, String autoMessage) {
		try {
			if (context == null || !Constants.LOG_SWITCH) {
				return;
			}
			new StorageManager(context, xmlVersion, behaviourIdEnum,
					autoMessage).writeLog();
		} catch (Exception e) {
//			LogCatLog.printStackTraceAndMore(e);
		}
	}
	
	/**
	 * @param BehaviourIdEnum 行为ID 为monitor时，需要在行为状态指定日志类型如：D-VM,e,used...
	 * @param appID 应用ID
	 * @param viewID 当前视图ID
	 * @param refViewID 跳转之前的视图ID
	 * @param seed 种子
	 */
	public static void writeLog(Context context, BehaviourIdEnum behaviourIdEnum,String appID,String viewID, String refViewID, String seed){
		try {
			if (context == null || !Constants.LOG_SWITCH) {
				return;
			}
			new StorageManager(context, behaviourIdEnum,null,null, appID,null, viewID, refViewID, seed,null,null,null,new String[]{"","","","",""})
			.writeLog();
		} catch (Exception e) {
//			LogCatLog.printStackTraceAndMore(e);
		}
	}
	
	/**
	 * @param BehaviourIdEnum 行为ID 为monitor时，需要在行为状态指定日志类型如：D-VM,e,used...
	 * @param behaviourStatus 行为状态
	 * @param statusMessage 状态消息
	 * @param appID 应用ID
	 * @param appVersion 应用版本
	 * @param viewID 当前视图ID
	 * @param refViewID 跳转之前的视图ID
	 * @param seed 种子ID
	 * @param url 当前所在界面url
	 * @param behaviourPro 行为产生者
	 * @param logPro 日志产生者
	 * @param extendParams 扩展字段
	 */
	public static void writeLog(Context context, BehaviourIdEnum behaviourIdEnum, String behaviourStatus, String statusMessage, 
			String appID,String appVersion, String viewID, String refViewID, String seed){
		try {
			if (context == null || !Constants.LOG_SWITCH) {
				return;
			}
			new StorageManager(context, behaviourIdEnum, behaviourStatus, statusMessage, appID, appVersion, viewID, refViewID, seed,null,null,null,new String[]{"","","","",""})
					.writeLog();
		} catch (Exception e) {
//			LogCatLog.printStackTraceAndMore(e);
		}
	}
	
	/**
	 * @param BehaviourIdEnum 行为ID 为monitor时，需要在行为状态指定日志类型如：D-VM,e,used...
	 * @param behaviourStatus 行为状态
	 * @param statusMessage 状态消息
	 * @param appID 应用ID
	 * @param appVersion 应用版本
	 * @param viewID 当前视图ID
	 * @param refViewID 跳转之前的视图ID
	 * @param seed 种子ID
	 * @param url 当前所在界面url
	 * @param behaviourPro 行为产生者
	 * @param logPro 日志产生者
	 * @param extendParams 扩展字段
	 */
	public static void writeLog(Context context, BehaviourIdEnum behaviourIdEnum, String behaviourStatus, String statusMessage, 
			String appID,String appVersion, String viewID, String refViewID, String seed, String url,String behaviourPro,String logPro,String... extendParams){
		try {
			if (context == null || !Constants.LOG_SWITCH) {
				return;
			}
			new StorageManager(context, behaviourIdEnum, behaviourStatus, statusMessage, appID, appVersion, viewID, refViewID, seed,url,behaviourPro,logPro,extendParams)
					.writeLog();
		} catch (Exception e) {
//			LogCatLog.printStackTraceAndMore(e);
		}
	}
	
	public static void writeLog(Context context, AlipayLogInfo logInfo){
		try {
			if (context == null || !Constants.LOG_SWITCH) {
				return;
			}
			new StorageManager(context.getApplicationContext(), 
					logInfo.behaviourIdEnum, 
					logInfo.behaviourStatus, 
					logInfo.statusMessage, 
					logInfo.appID, 
					logInfo.appVersion, 
					logInfo.viewID, 
					logInfo.refViewID, 
					logInfo.seed,
					logInfo.url,
					logInfo.behaviourPro,
					logInfo.logPro,
					logInfo.extendParams)
					.writeLog();
		} catch (Exception e) {
//			LogCatLog.printStackTraceAndMore(e);
		}
	}
	
	/*public static void writeLog(Context context, BehaviourIdEnum BehaviourIdEnum,String appID,String appVersion,String viewID, String refViewID, String seed){
		try {
			if (context == null || !Constants.LOG_SWITCH) {
				return;
			}
			new StorageManager(context, BehaviourIdEnum,null,null, appID,appVersion, viewID, refViewID, seed,null,null,null,null)
			.start();
		} catch (Exception e) {
			LogCatLog.printStackTraceAndMore(e);
		}
	}
	
	public static void writeLog(Context context, BehaviourIdEnum BehaviourIdEnum, String behaviourStatus, String statusMessage, 
			String appID,String appVersion, String viewID, String refViewID, 
			String seed, String url, String behaviourPro, String logPro,String... extendParams){
		try {
			if (context == null || !Constants.LOG_SWITCH) {
				return;
			}
			new StorageManager(context,BehaviourIdEnum, behaviourStatus, statusMessage, appID, appVersion, viewID, refViewID, seed, url,
					behaviourPro, logPro, extendParams).start();
		} catch (Exception e) {
			LogCatLog.printStackTraceAndMore(e);
		}
	}*/
	
	/**
	 * 行为ID，行为状态，状态消息，子应用ID，子应用版本，视图ID，ref视图ID , 种子, URL, 行为发起方类型,  日志生产者类型
	 */
	public static void writeLog(Context context, String behaviourId,String behaviourStatus,String statusMessage,String appID,String appVersion,String[] extendParams){
		try {
			if (context == null || !Constants.LOG_SWITCH) {
				return;
			}
			
			BehaviourIdEnum behavID = null;
			if(behaviourId.equals(BehaviourIdEnum.CLICKED.getDes())){
				behavID = BehaviourIdEnum.CLICKED;
			}else if(behaviourId.equals(BehaviourIdEnum.SUBMITED.getDes())){
				behavID = BehaviourIdEnum.SUBMITED;
			}else if(behaviourId.equals(BehaviourIdEnum.BIZLAUNCHED.getDes())){
				behavID = BehaviourIdEnum.BIZLAUNCHED;
			}else if(behaviourId.equals(BehaviourIdEnum.ERROR.getDes())){
				behavID = BehaviourIdEnum.ERROR;
			}else if(behaviourId.equals(BehaviourIdEnum.EXCEPTION.getDes())){
				behavID = BehaviourIdEnum.EXCEPTION;
			}else if(behaviourId.equals(BehaviourIdEnum.MONITOR.getDes())){
				behavID = BehaviourIdEnum.MONITOR;
			}else {
				behavID = BehaviourIdEnum.NONE;
			}
			
			new StorageManager(context, behavID,behaviourStatus,statusMessage,appID,appVersion, extendParams).writeLog();
		} catch (Exception e) {
//			LogCatLog.printStackTraceAndMore(e);
		}
	}
	
	/**
	 * 注册客户端常用信息
	 * @param alipayId productID
	 * @param productVersion 
	 * @param clientID
	 * @param uuId
	 * @param modelVersion 1
	 */
	public static void initClient(String alipayId,String productVersion,String clientID,String uuId,String modelVersion){
		StorageStateInfo stateInfos = StorageStateInfo.getInstance();
		stateInfos.registClient(alipayId, productVersion, clientID, uuId, modelVersion);
	}
	
	/**
	 * 初始化一些客户端的值
	 * @param context
	 */
	public static void initClient(Context context){
//	    DeviceInfo deviceInfo = DeviceInfo.createInstance(context);
//	    AppInfo appInfo = AppInfo.getInstance();
//	    AlipayLogAgent.initClient(appInfo.getProductID(), appInfo.getmProductVersion(), deviceInfo.getClientId(), UUID.randomUUID().toString(), "");
    }
	
	
	
	/**
	 * 清除注册信息
	 */
	public static void unInitClient(){
		StorageStateInfo stateInfos = StorageStateInfo.getInstance();
		stateInfos.unRegistClient();
	}

}
package com.naruto.mobile.base.log.logagent;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import android.content.Context;

import com.naruto.mobile.base.log.transport.http.legacy.HttpClient;


public class LogSendManager{
	private static final String TAG = "LogSendManager";
	
	public static void checkAndSend(Context context){
		synchronized (Constants.lock) {
			Constants.LOG_ACCOUNT++;
			
			long duration = System.currentTimeMillis() - Constants.LAST_SEND_TIME;
//			LogCatLog.d(TAG, "logSwitch:"+Constants.LOG_SWITCH+
//					" log count:"+Constants.LOG_ACCOUNT+" send log duration:" + duration);
			if(!Constants.LOG_SWITCH || Constants.LOG_ACCOUNT < Constants.LOG_MAX_ACCOUNT 
					|| duration < 60000){
			}else{
//				uploadLog(context);
				sendLog(context);
			}
		}
	}
	
	public static void uploadLog(Context context){
		new UploadLogThread(context).start();
	}
	
	private static class UploadLogThread extends Thread{
		private Context context;
		public UploadLogThread(Context context){
			this.context = context;
		}
		@Override
		public void run() {
			sendLog(context);
		}
	}
	/*
	 * 发送日志
	 */
	private static void sendLog(Context context){	
//		String logStr = "";
//		synchronized(Constants.lock){
		String logStr = LogBaseHelper.readFile(context, Constants.LOGFILE_PATH + Constants.LOGFILE_NAME);
//		}
			
		try{
			HttpClient hc = new HttpClient(LogUtil.getStatisticsUrl(context), context);
			HttpResponse httpResponse = hc.sendGZipSynchronousRequest(logStr);
			synchronized(Constants.lock){
				if(httpResponse!=null){
					HttpEntity he = httpResponse.getEntity();
					String response = EntityUtils.toString(he);
//					LogCatLog.d(TAG, "logsend response ==> " + response);
					int id = response.indexOf("logSwitch=");
					if(id>0){
						String logswitch = response.substring(id+10);
						if(logswitch.compareTo("false")==0){
							Constants.LOG_SWITCH = false;
						}else{
							Constants.LOG_SWITCH = true;
						}
						LogBaseHelper.fileClean(context, Constants.LOGFILE_PATH+Constants.LOGFILE_NAME);
						Constants.LOG_ACCOUNT = 0;
						Constants.LAST_SEND_TIME = System.currentTimeMillis();
					}
				}
			}
		}catch(Exception e){
//			LogCatLog.printStackTraceAndMore(e);
		}
	}
}

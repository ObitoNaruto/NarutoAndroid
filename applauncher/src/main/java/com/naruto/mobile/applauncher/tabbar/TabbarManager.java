package com.naruto.mobile.applauncher.tabbar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//import com.alipay.mobile.base.commonbiz.R;
//import com.alipay.mobile.base.config.ConfigService;
//import com.alipay.mobile.common.logging.api.LoggerFactory;
//import com.alipay.mobile.common.transport.Request;
//import com.alipay.mobile.common.transport.Response;
//import com.alipay.mobile.common.transport.TransportCallback;
//import com.alipay.mobile.common.transport.download.DownloadRequest;
//import com.alipay.mobile.framework.LauncherApplicationAgent;
//import com.alipay.mobile.framework.MicroApplicationContext;
//import com.alipay.mobile.framework.service.common.DownloadService;
//import com.alipay.mobile.framework.service.common.TaskScheduleService;
//import com.alipay.mobile.framework.service.common.TimeService;
//import com.alipay.mobile.framework.service.ext.security.AuthService;
//import com.j256.ormlite.stmt.query.NeedsFutureClause;

public class TabbarManager {
	public static final String TAG = TabbarManager.class.getSimpleName();

	private static HashMap<String, TabbarConfigModel> tabbarConfigMap;//给业务用
	private static HashMap<String, TabbarConfigData> configDataFromConfigService;//自己内存用configservice数据

	private static HashMap<String, String> downloadMap; //下载任务列表

	private static String tabbarConfig;

	private static boolean needRefresh;

	/*private static Context context = LauncherApplicationAgent
		.getInstance().getMicroApplicationContext()
		.getApplicationContext();*/

	public synchronized static void setTabbarConfig(String config){
		tabbarConfig = config;
	}

	public static boolean getRefreshFlag(){
		return needRefresh;
	}

	public static void setRefreshFlag(boolean fresh){
		needRefresh = fresh;
	}

	public synchronized static String getTabbarConfig(){
		return tabbarConfig;
	}

	private synchronized static boolean shouldBeShow(String fromDate, String toDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//24小时制
        TimeService timeService = LauncherApplicationAgent.getInstance()
	    	.getMicroApplicationContext().findServiceByInterface(TimeService.class.getName());
	    long time = System.currentTimeMillis();
		if(timeService != null){
	    	time = timeService.getServerTime();
	    	LoggerFactory.getTraceLogger().debug(TAG, "timeService = " + time);
	    }
        Date d = new Date(time);
        try {
            Date dateFrom = sdf.parse(fromDate);
            Date dateTo = sdf.parse(toDate);
            long dTo = dateTo.getTime() + 86400000;
            Date nextTo = new Date(dTo);
            LoggerFactory.getTraceLogger().debug(TAG, "time: " + time + " dateFrom: "
        			+ dateFrom.getTime() + " dateTo: " + dateTo.getTime());
            if (d.before(nextTo) && d.after(dateFrom)) {
            	LoggerFactory.getTraceLogger().debug(TAG, "show");
                return true;
            }
        } catch (Exception e) {
        	return false;
        }
        LoggerFactory.getTraceLogger().debug(TAG, "not show");
        return false;
    }

	public synchronized static boolean isConfigExistInMem(String key){
		LoggerFactory.getTraceLogger().debug(TAG, "clear key");
		if(tabbarConfigMap == null){
			return false;
		}
		if(tabbarConfigMap.containsKey(key)){
			return true;
		}else{
			return false;
		}
	}

	public synchronized static void initConfig(){
		ConfigService configService = LauncherApplicationAgent
			.getInstance().getMicroApplicationContext()
			.findServiceByInterface(ConfigService.class.getName());
        String content = configService.getConfig(TabbarConstant.TABBAR_CONFIG_PATH);
        LoggerFactory.getTraceLogger().info(TAG, "content : " + content);
        initData(content);
	}

	public synchronized static void initData(String content){
		if(content == null || content.length() <= 0){
        	setDefaultConfig();
        	return;
        }
        try {
        	JSONObject jsonObject = JSONObject.parseObject(content);
        	if(jsonObject == null){
        		LoggerFactory.getTraceLogger().debug(TAG, "jsonObject = null, return.");
        		return;
        	}
        	String head = jsonObject.getString("hd");
        	if(TextUtils.isEmpty(head)){
        		LoggerFactory.getTraceLogger().debug(TAG, "head is empty, return.");
        		return;
        	}
        	String tabConfig = jsonObject.getString("cfg");
        	if(TextUtils.isEmpty(tabConfig)){
        		LoggerFactory.getTraceLogger().debug(TAG, "tabConfig is empty, return.");
        		return;
        	}
        	JSONArray configArray = JSONArray.parseArray(tabConfig);
        	if(configArray == null || configArray.size() == 0){
        		LoggerFactory.getTraceLogger().debug(TAG, "configArray = null, return.");
        		return;
        	}
        	LoggerFactory.getTraceLogger().debug(TAG, "configArray size = " + configArray.size());
            for(int i = 0; i < configArray.size(); i++){
            	TabbarConfigData tabbarConfigData = new TabbarConfigData();
            	JSONObject config = JSONObject.parseObject(configArray.getString(i));
            	String flag = config.getString("f");
            	String tabIndex = config.getString("id");
        		String tabName = config.getString("tn");
        		String selectedImage = config.getString("si");
        		String defaultImage = config.getString("di");
        		String selectTitleColor = config.getString("sc");
        		String defaultTitleColor = config.getString("dc");
        		String fromDate = config.getString("fd");
        		String toDate = config.getString("td");
        		tabbarConfigData.setFlag(flag);
        		tabbarConfigData.setIndex(tabIndex);
        		tabbarConfigData.setTabName(tabName);
        		tabbarConfigData.setSelectedImage(head + selectedImage);
        		tabbarConfigData.setDefaultImage(head + defaultImage);
        		tabbarConfigData.setSelectTitleColor(selectTitleColor);
        		tabbarConfigData.setDefaultTitleColor(defaultTitleColor);
        		tabbarConfigData.setFromDate(fromDate);
        		tabbarConfigData.setToDate(toDate);

        		//此处判断是否过期，过期了不加到里面去
        		if(shouldBeShow(fromDate, toDate)){
        			LoggerFactory.getTraceLogger().debug(TAG, "shouldBeShow : " + tabName);
        			addConfigToCfgMem(tabIndex, tabbarConfigData);
        		}else{
        			if(isConfigExistInCfgMem(tabIndex)){
        				LoggerFactory.getTraceLogger().debug(TAG, "expire should not be Show : " + tabName);
        				removeConfigExistFromCfgMem(tabIndex);
        			}
        		}
            }
          //根据列表加载默认图片
        } catch (Exception e) {
        	LoggerFactory.getTraceLogger().error(TAG, e.getMessage());
        }
	}

	public synchronized static void clearConfig(String key){
		LoggerFactory.getTraceLogger().debug(TAG, "clear key");
		if(tabbarConfigMap == null){
			return;
		}
		if(tabbarConfigMap.containsKey(key)){
			tabbarConfigMap.remove(key);
		}
	}

	public synchronized static void addConfigToCfgMem(String tabIndex, TabbarConfigData configData){
		if(configDataFromConfigService == null){
			configDataFromConfigService = new HashMap<String, TabbarConfigData>();
		}
		configDataFromConfigService.put(tabIndex, configData);
		LoggerFactory.getTraceLogger().debug(TAG, "add : " + tabIndex);
	}

	public synchronized static boolean isConfigExistInCfgMem(String tabIndex){
		if(configDataFromConfigService == null){
			return false;
		}
		if(configDataFromConfigService.containsKey(tabIndex)){
			return true;
		}else{
			return false;
		}
	}

	public synchronized static void removeConfigExistFromCfgMem(String tabIndex){
		if(configDataFromConfigService == null){
			return;
		}
		if(configDataFromConfigService.containsKey(tabIndex)){
			configDataFromConfigService.remove(tabIndex);
		}
	}

	public synchronized static void addDownloadTask(String url, String storePath){
		if(downloadMap == null){
			downloadMap = new HashMap<String, String>();
		}
		downloadMap.put(url, storePath);
	}

	public synchronized static void setDownloadTask(){
		Context context = LauncherApplicationAgent.getInstance().getMicroApplicationContext()
			.getApplicationContext();
		//此处能拿到config,拿不到就是没开开关或者开关内容为空
		HashMap<String, TabbarConfigData> configList = TabbarManager.getConfigFromCfgService();
        if(configList == null){
        	setDefaultConfig();
        	return;
        }
        for(Map.Entry<String, TabbarConfigData>  entry : configList.entrySet()){
			TabbarConfigData value = entry.getValue();
			String index = value.getIndex();
			String selectImage = value.getSelectedImage();
			String defaultImage = value.getDefaultImage();
			String selectImagePath = getPrivatePath(context) + index + "_" + selectImage;
			File selectImagefile = new File(selectImagePath);
			if(selectImagefile != null && selectImagefile.exists()){
				LoggerFactory.getTraceLogger().debug(TAG, "selectImagefile exist : " + selectImagePath);
			}else{
				LoggerFactory.getTraceLogger().debug(TAG, "selectImagefile not exist");
				TabbarManager.addDownloadTask(selectImage, selectImagePath);
				//加入下载队列
			}
			String defaultImagePath = getPrivatePath(context) + index + "_" + defaultImage;
			File defaultImageFile = new File(defaultImagePath);
			if(defaultImageFile != null && defaultImageFile.exists()){
				LoggerFactory.getTraceLogger().debug(TAG, "selectImagefile exist : " + defaultImagePath);
			}else{
				//加入下载队列
				TabbarManager.addDownloadTask(defaultImage, defaultImagePath);
				LoggerFactory.getTraceLogger().debug(TAG, "defaultImageFile not exist");
			}
		}
	}

	public synchronized static HashMap<String, String> getDownloadTask(){
		return downloadMap;
	}

	public synchronized static void removeDownloadTask(String url){
		if(downloadMap == null){
			return;
		}
		if(downloadMap.containsKey(url)){
			downloadMap.remove(url);
		}
	}

	public synchronized static HashMap<String, TabbarConfigData> getConfigFromCfgService(){
		if(configDataFromConfigService == null || configDataFromConfigService.size() == 0){
			return null;
		}else{
			LoggerFactory.getTraceLogger().debug(TAG, "configDataFromConfigService size : " + configDataFromConfigService.size());
			return configDataFromConfigService;
		}
	}

	public synchronized static void initTabConfig(){
		Context context = LauncherApplicationAgent.getInstance().getMicroApplicationContext()
			.getApplicationContext();
		HashMap<String, TabbarConfigData> resourceList = getConfigFromCfgService();
        if(resourceList == null || resourceList.size() == 0){
        	setDefaultConfig();
        	return;
        }
        HashSet<String> hashset = new HashSet<String>();
        for(Map.Entry<String, TabbarConfigData>  entry : resourceList.entrySet()){
        	TabbarConfigModel tabbarConfigModel = new TabbarConfigModel();
			TabbarConfigData value = entry.getValue();
			String index = value.getIndex();
			String fromTime = value.getFromDate();
			String toTime = value.getToDate();
			String selectImage = value.getSelectedImage();
			String defaultImage = value.getDefaultImage();
			String selectTitleColor = value.getSelectTitleColor();
			String defaultTitleColor = value.getDefaultTitleColor();
			String selectImagePath = getPrivatePath(context) + index + "_" + selectImage;
			Drawable selectDrawable = Drawable.createFromPath(selectImagePath);
			tabbarConfigModel.setSelectedImage(selectDrawable);
			LoggerFactory.getTraceLogger().debug(TAG, "selectImagefile exist : " + selectImagePath);
			String defaultImagePath = getPrivatePath(context) + index + "_" + defaultImage;
			Drawable defaultDrawable = Drawable.createFromPath(defaultImagePath);
			tabbarConfigModel.setDefaultImage(defaultDrawable);
			LoggerFactory.getTraceLogger().debug(TAG, "selectDefaultImagefile exist : " + defaultImagePath);
			tabbarConfigModel.setSelectTitleColor(selectTitleColor);
			tabbarConfigModel.setDefaultTitleColor(defaultTitleColor);
			tabbarConfigModel.success = true;
			if(shouldBeShow(fromTime, toTime)){
				TabbarManager.addConfig(index, tabbarConfigModel);
				hashset.add(index);
			}

		}
        LoggerFactory.getTraceLogger().debug(TAG, "hashset.size() = " + hashset.size());
        if(hashset != null){
        	if(!hashset.contains("0")){
        		if(needShow()){
        			setAlipayDefaultConfig(context);
        		}else{
        			TabbarConfigModel alipayTabbarConfigModel = new TabbarConfigModel();
        			alipayTabbarConfigModel.success = false;
        			TabbarManager.addConfig("0", alipayTabbarConfigModel);
        		}
        	}
        	if(!hashset.contains("1")){
        		if(needShow()){
        			setO2ODefaultConfig(context);
        		}else{
        			TabbarConfigModel alipayTabbarConfigModel = new TabbarConfigModel();
        			alipayTabbarConfigModel.success = false;
        			TabbarManager.addConfig("1", alipayTabbarConfigModel);
        		}
        	}
        	if(!hashset.contains("2")){
        		if(needShow()){
        			setContactDefaultConfig(context);
        		}else{
        			TabbarConfigModel alipayTabbarConfigModel = new TabbarConfigModel();
        			alipayTabbarConfigModel.success = false;
        			TabbarManager.addConfig("2", alipayTabbarConfigModel);
        		}
        	}
        	if(!hashset.contains("3")){
        		if(needShow()){
        			setWealthDefaultConfig(context);
        		}else{
        			TabbarConfigModel alipayTabbarConfigModel = new TabbarConfigModel();
        			alipayTabbarConfigModel.success = false;
        			TabbarManager.addConfig("3", alipayTabbarConfigModel);
        		}
        	}
        }else{
        	setDefaultConfig();
        }
        //在这加入剩余的默认图片
	}

	public synchronized static void clearConfig(){
		LoggerFactory.getTraceLogger().debug(TAG, "clear user map");
		tabbarConfigMap = null;
	}

	private synchronized static void setDefaultConfig(){
		String fromDate = "20160207";
		String toDate = "20160214";
		Context context = LauncherApplicationAgent
		.getInstance().getMicroApplicationContext()
		.getApplicationContext();
		boolean isShow = shouldBeShow(fromDate, toDate);
		if(isShow){
			setAlipayDefaultConfig(context);
			setO2ODefaultConfig(context);
			setContactDefaultConfig(context);
			setWealthDefaultConfig(context);
		}else{
			clearConfig();
		}
	}

	/**
	 * 开关内有几条配置，这个函数就要同步几次  否则tabbarConfigMap = null
	 * @param tabIndex tab索引
	 * @param tabbarConfigModel tab配置
	 */
	public synchronized static void addConfig(String tabIndex, TabbarConfigModel tabbarConfigModel){
		if(tabbarConfigMap == null){
			tabbarConfigMap = new HashMap<String, TabbarConfigModel>();
		}
		if(tabbarConfigMap.containsKey(tabIndex)){
			tabbarConfigMap.remove(tabIndex);
		}
		tabbarConfigMap.put(tabIndex, tabbarConfigModel);
	}

	public synchronized static boolean isExistInDownloadTask(String url){
		if(downloadMap == null){
			return false;
		}
		if(downloadMap.containsKey(url)){
			return true;
		}else{
			return false;
		}
	}

	private synchronized static String getPrivatePath(Context context) {
        if (context == null) {
            return null;
        }
        File file = context.getFilesDir();
        if (file == null)
            file = context.getCacheDir();
        String loadPath = file.getAbsolutePath() + TabbarConstant.TABBAR_RES_PATH;
        return loadPath;
    }


	public synchronized static void updateTabConfig(Context context){
		try{
			HashMap<String, TabbarConfigData> resourceList = TabbarManager.getConfigFromCfgService();
	        if(resourceList == null){
	        	LoggerFactory.getTraceLogger().debug(TAG, "resourceList == null");
	        	setDefaultConfig();
	        	return;
	        }
	        HashSet<String> hashset = new HashSet<String>();
	        for(Map.Entry<String, TabbarConfigData>  entry : resourceList.entrySet()){
	        	TabbarConfigModel tabbarConfigModel = new TabbarConfigModel();
				TabbarConfigData value = entry.getValue();
				String index = value.getIndex();
				String fromTime = value.getFromDate();
				String toTime = value.getToDate();
				String tabName = value.getTabName();
				String selectImage = value.getSelectedImage();
				String defaultImage = value.getDefaultImage();
				String selectTitleColor = value.getSelectTitleColor();
				String defaultTitleColor = value.getDefaultTitleColor();
				String selectImagePath = getPrivatePath(context) + index + "_" + selectImage;
				Drawable selectDrawable = Drawable.createFromPath(selectImagePath);
				tabbarConfigModel.setSelectedImage(selectDrawable);
				LoggerFactory.getTraceLogger().debug(TAG, "selectImagefile exist : " + selectImagePath);
				String defaultImagePath = getPrivatePath(context) + index + "_" + defaultImage;
				Drawable defaultDrawable = Drawable.createFromPath(defaultImagePath);
				tabbarConfigModel.setDefaultImage(defaultDrawable);
				LoggerFactory.getTraceLogger().debug(TAG, "selectImagefile exist : " + defaultImagePath);
				tabbarConfigModel.setTabName(tabName);
				tabbarConfigModel.setSelectTitleColor(selectTitleColor);
				tabbarConfigModel.setDefaultTitleColor(defaultTitleColor);
				tabbarConfigModel.success = true;
				if(shouldBeShow(fromTime, toTime)){
					TabbarManager.addConfig(index, tabbarConfigModel);
					hashset.add(index);
				}else{
					if(isConfigExistInMem(index)){
						clearConfig(index);
					}
				}
			}
	        if(hashset != null){
	        	if(!hashset.contains("0")){
	        		if(needShow()){
	        			setAlipayDefaultConfig(context);
	        		}else{
	        			TabbarConfigModel alipayTabbarConfigModel = new TabbarConfigModel();
	        			alipayTabbarConfigModel.success = false;
	        			TabbarManager.addConfig("0", alipayTabbarConfigModel);
	        		}
	        	}
	        	if(!hashset.contains("1")){
	        		if(needShow()){
	        			setO2ODefaultConfig(context);
	        		}else{
	        			TabbarConfigModel alipayTabbarConfigModel = new TabbarConfigModel();
	        			alipayTabbarConfigModel.success = false;
	        			TabbarManager.addConfig("1", alipayTabbarConfigModel);
	        		}
	        	}
	        	if(!hashset.contains("2")){
	        		if(needShow()){
	        			setContactDefaultConfig(context);
	        		}else{
	        			TabbarConfigModel alipayTabbarConfigModel = new TabbarConfigModel();
	        			alipayTabbarConfigModel.success = false;
	        			TabbarManager.addConfig("2", alipayTabbarConfigModel);
	        		}
	        	}
	        	if(!hashset.contains("3")){
	        		if(needShow()){
	        			setWealthDefaultConfig(context);
	        		}else{
	        			TabbarConfigModel alipayTabbarConfigModel = new TabbarConfigModel();
	        			alipayTabbarConfigModel.success = false;
	        			TabbarManager.addConfig("3", alipayTabbarConfigModel);
	        		}
	        	}
	        }
		}catch (Exception e) {
			setDefaultConfig();
		}

	}

	public synchronized static boolean needShow(){
		String fromDate = "20160207";
		String toDate = "20160214";
		boolean isShow = shouldBeShow(fromDate, toDate);
		return isShow;
	}

	public synchronized static void setAlipayDefaultConfig(Context context){
		TabbarConfigModel alipayTabbarConfigModel = new TabbarConfigModel();
		Drawable alipaySelectDrawable = context.getResources().getDrawable(R.drawable.alipay_select_image);
		Drawable alipayDefaultDrawable = context.getResources().getDrawable(R.drawable.alipay_default_image);
		alipayTabbarConfigModel.setTabName("支付宝");
		alipayTabbarConfigModel.setSelectedImage(alipaySelectDrawable);
		alipayTabbarConfigModel.setDefaultImage(alipayDefaultDrawable);
		alipayTabbarConfigModel.setSelectTitleColor("#E44535");
		alipayTabbarConfigModel.setDefaultTitleColor("#E44535");
		alipayTabbarConfigModel.success = true;
		TabbarManager.addConfig("0", alipayTabbarConfigModel);
	}
	public synchronized static void setO2ODefaultConfig(Context context){
		TabbarConfigModel o2oTabbarConfigModel = new TabbarConfigModel();
		Drawable o2oSelectDrawable = context.getResources().getDrawable(R.drawable.o2o_select_image);
		Drawable o2oDefaultDrawable = context.getResources().getDrawable(R.drawable.o2o_default_image);
		o2oTabbarConfigModel.setTabName("口碑");
		o2oTabbarConfigModel.setSelectedImage(o2oSelectDrawable);
		o2oTabbarConfigModel.setDefaultImage(o2oDefaultDrawable);
		o2oTabbarConfigModel.setSelectTitleColor("#E44535");
		o2oTabbarConfigModel.setDefaultTitleColor("#E44535");
		o2oTabbarConfigModel.success = true;
		TabbarManager.addConfig("1", o2oTabbarConfigModel);
	}
	public synchronized static void setContactDefaultConfig(Context context){
		TabbarConfigModel contactTabbarConfigModel = new TabbarConfigModel();
		Drawable contactSelectDrawable = context.getResources().getDrawable(R.drawable.contact_select_image);
		Drawable contactDefaultDrawable = context.getResources().getDrawable(R.drawable.contact_default_image);
		contactTabbarConfigModel.setTabName("朋友");
		contactTabbarConfigModel.setSelectedImage(contactSelectDrawable);
		contactTabbarConfigModel.setDefaultImage(contactDefaultDrawable);
		contactTabbarConfigModel.setSelectTitleColor("#E44535");
		contactTabbarConfigModel.setDefaultTitleColor("#E44535");
		contactTabbarConfigModel.success = true;
		TabbarManager.addConfig("2", contactTabbarConfigModel);
	}
	public synchronized static void setWealthDefaultConfig(Context context){
		TabbarConfigModel wealthTabbarConfigModel = new TabbarConfigModel();
		Drawable wealthSelectDrawable = context.getResources().getDrawable(R.drawable.wealth_select_image);
		Drawable wealthDefaultDrawable = context.getResources().getDrawable(R.drawable.wealth_default_image);
		wealthTabbarConfigModel.setTabName("我的");
		wealthTabbarConfigModel.setSelectedImage(wealthSelectDrawable);
		wealthTabbarConfigModel.setDefaultImage(wealthDefaultDrawable);
		wealthTabbarConfigModel.setSelectTitleColor("#E44535");
		wealthTabbarConfigModel.setDefaultTitleColor("#E44535");
		wealthTabbarConfigModel.success = true;
		TabbarManager.addConfig("3", wealthTabbarConfigModel);
	}

	public synchronized static TabbarConfigModel getConfig(String index){
		if(tabbarConfigMap == null){
			return null;
		}else{
			return tabbarConfigMap.get(index);
		}
	}


//////////////////////////////12.27改/////////////////////////////////


////////////////////////////////////ConfigService/////////////////////////////////////////////////////



	/*public synchronized static TabbarConfigData getConfigFromCfgService(String index){
		if(configDataFromConfigService == null){
			return null;
		}else{
			return configDataFromConfigService.get(index);
		}
	}*/

	///////////////////////////////////////////////////////////////////////////////////////////////////////

	public synchronized static boolean doCheckResource(Context context){
		HashMap<String, TabbarConfigData> resourceList = TabbarManager.getConfigFromCfgService();
        if(resourceList == null){
        	setDefaultConfig();
        	return true;
        }
        int configSize = resourceList.size();
        int count = 0;
        boolean pass = true;
        for(Map.Entry<String, TabbarConfigData>  entry : resourceList.entrySet()){
			TabbarConfigData value = entry.getValue();
			String index = value.getIndex();
			String selectImage = value.getSelectedImage();
			String defaultImage = value.getDefaultImage();
			//查询两个图片是否存在
			String selectImagePath = getPrivatePath(context) + index + "_" + selectImage;
			File selectImagefile = new File(selectImagePath);
			if(selectImagefile != null && selectImagefile.exists()){
				LoggerFactory.getTraceLogger().debug(TAG, "selectImagefile exist : " + selectImagePath);
			}else{
				pass = false;
				LoggerFactory.getTraceLogger().debug(TAG, "selectImagefile not exist");
				if(TabbarManager.isExistInDownloadTask(selectImage)){
					LoggerFactory.getTraceLogger().debug(TAG, "selectImagefile in download task");
				}else{
					TabbarManager.addDownloadTask(selectImage, selectImagePath);
				}
				//return false;
				//加入下载队列
			}
			String defaultImagePath = getPrivatePath(context) + index + "_" + defaultImage;
			File defaultImageFile = new File(defaultImagePath);
			if(defaultImageFile != null && defaultImageFile.exists()){
				LoggerFactory.getTraceLogger().debug(TAG, "selectImagefile exist : " + defaultImagePath);
			}else{
				//加入下载队列
				pass = false;
				LoggerFactory.getTraceLogger().debug(TAG, "defaultImageFile not exist");
				if(TabbarManager.isExistInDownloadTask(defaultImage)){
					LoggerFactory.getTraceLogger().debug(TAG, "defaultImageFile in download task");
				}else{
					TabbarManager.addDownloadTask(defaultImage, defaultImagePath);
				}
			}
			if(pass){
				count++;
			}
		}
        if(!pass){
        	HashMap<String, String> tasks = TabbarManager.getDownloadTask();//获取下载列表
			if(tasks != null){
				LoggerFactory.getTraceLogger().debug(TAG, "tasks != null");
				for(Map.Entry<String, String>  entry : tasks.entrySet()){
					String url = entry.getKey();
					String storePath = entry.getValue();
					postToTaskSchedule(url, storePath);
					LoggerFactory.getTraceLogger().debug(TAG, "download : " + url + " " + storePath);
				}
			}
			return pass;
        }
        if(count < configSize){
        	return false;
        }else{
        	LoggerFactory.getTraceLogger().debug(TAG, "tabbarConfigMap can use");
        	return true;
        }
	}

	private static void postToTaskSchedule(final String url, final String storePath){

		MicroApplicationContext microApp = LauncherApplicationAgent
    	.getInstance().getMicroApplicationContext();

		if (microApp == null) {
		    LoggerFactory.getTraceLogger().error(TAG,
		            "MicroApplicationContext is NULL");
		    return;
		}

		final TaskScheduleService scheduleService = microApp
		        .findServiceByInterface(TaskScheduleService.class.getName());
		try{
			if (scheduleService == null) {
			    LoggerFactory.getTraceLogger().error(TAG,
			            "TaskScheduleService is NULL, return");
			    return;
			}

			scheduleService.acquireScheduledExecutor().schedule(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					LoggerFactory.getTraceLogger().info(TAG,
	            	"init tabbar config");
				downloadImage(url, storePath);
				}
			},0, TimeUnit.MILLISECONDS);

		}catch(Exception e){
			return;
		}
	}

	private static void downloadImage(final String url, final String storePath){
		DownloadService downloadService = LauncherApplicationAgent
			.getInstance().getMicroApplicationContext()
			.findServiceByInterface(DownloadService.class.getName());
		if(downloadService == null){
			return;
		}
		AuthService authService = LauncherApplicationAgent.getInstance().getMicroApplicationContext()
			.findServiceByInterface(AuthService.class.getName());
		String id = null;
		if(authService != null && authService.getUserInfo() != null){
			id = authService.getUserInfo().getUserId();
		}
		final String userId = id;
		DownloadRequest downloadRequest = new DownloadRequest(url);
        downloadRequest.setPath(storePath);
        downloadRequest.setTransportCallback(new TransportCallback() {

			@Override
			public void onProgressUpdate(Request arg0, double arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPreExecute(Request arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPostExecute(Request arg0, Response arg1) {
				// TODO Auto-generated method stub
				LoggerFactory.getTraceLogger().info(TAG,
						"download success : " + url + " save to : " + storePath);
				//下载成功后，从下载列表中移除该项
				TabbarManager.removeDownloadTask(url);
				//全部下载完后，通知更新用户数据
				LoggerUtils.tabbarConfigLog("success|" + userId + "|" + url);
			}

			@Override
			public void onFailed(Request arg0, int arg1, String arg2) {
				// TODO Auto-generated method stub
				LoggerUtils.tabbarConfigLog("failed|" + userId + "|" + url);
			}

			@Override
			public void onCancelled(Request arg0) {
				// TODO Auto-generated method stub

			}
		});
        downloadService.addDownload(downloadRequest);
	}




	/*public synchronized static void addConfigToSp(String key, String value){
		try {
			sharedPreferences = context.getSharedPreferences("tabbar_config",
                    Context.MODE_PRIVATE);
        } catch (Exception e) {
        	LoggerFactory.getTraceLogger().debug(TAG, "sharedPreferences == null");
        }

        if(sharedPreferences == null){
        	return;
        }

        sharedPreferences.edit().putString(key, value).commit();
	}

	public synchronized static String getConfigDataFromSp(String key){
		try {
			sharedPreferences = context.getSharedPreferences("tabbar_config",
                    Context.MODE_PRIVATE);
        } catch (Exception e) {
        	LoggerFactory.getTraceLogger().debug(TAG, "sharedPreferences == null");
        }

        if(sharedPreferences == null){
        	return null;
        }

        String value = sharedPreferences.getString(key, "");

        return value;

	}*/

}

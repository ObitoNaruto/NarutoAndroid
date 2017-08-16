package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.config;


import android.text.TextUtils;
import android.util.Log;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
//import com.alipay.mobile.base.config.ConfigService;
//import com.alipay.mobile.common.logging.api.LoggerFactory;
//import com.alipay.mobile.framework.MicroApplicationContext;
//import com.googlecode.androidannotations.api.BackgroundExecutor;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
/**
 * Created by xiangui.fxg on 2015/6/15.
 */
public class ConfigManager {
    private static final String TAG = "ConfigManager";
    private final String KEY_VERSION = "multimedia_config_ver";
    private HashMap<String,String> mConfigMap = new HashMap<String, String>();
    private static ConfigManager mInstance;

    private void ConfigManager(){

    }

    public synchronized static ConfigManager getInstance() {
        if (mInstance == null) {
            mInstance = new ConfigManager();
        }
        return mInstance;
    }

    //同步服务端配置
    public void updateConfig(boolean bBackground) {
        if(bBackground){
//            BackgroundExecutor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    updateConfigInner();
//                }
//            });
        }else{
            updateConfigInner();
        }
    }

    private void updateConfigInner(){
//        try {
//            MicroApplicationContext context = AppUtils.getMicroApplicationContext();
//
//            ConfigService configService = context.findServiceByInterface(ConfigService.class.getName());
//            String configValue = configService.getConfig(ConfigConstants.LOG_SAMPLE_INTERVAL_KEY);
//            mConfigMap.put(ConfigConstants.LOG_SAMPLE_INTERVAL_KEY,configValue);
//            //Log.d("LogItem","updateConfigInner configValue="+configValue);
////            if (configValue != null) {
////                JSONObject config = new JSONObject(configValue);
////                update(config);
////            }
//        } catch (Exception e) {
//            LoggerFactory.getTraceLogger().error(TAG, "updateConfigInner", e);
//        }
    }

//    public void update(JSONObject config) {
//        try {
//            if (config != null) {
//                Iterator<String> keys = config.keys();
//                while (keys.hasNext()) {
//                    String key = keys.next();
//                    String val = config.getString(key);
//                    if (key != null && val != null) {
//                        mConfigMap.put(key.toLowerCase(Locale.ENGLISH), val);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            LoggerFactory.getTraceLogger().error(TAG, "update", e);
//        }
//    }

    public int getIntValue(String name, int defaultValue) {
        int val = defaultValue;
        try {
            if(name != null && mConfigMap.containsKey(name)) {
                String str = mConfigMap.get(name);
                if(!TextUtils.isEmpty(str)) {
                    val = (int)Float.parseFloat(str);
                }
            }
        } catch (Exception e) {
            //LoggerFactory.getTraceLogger().error(TAG, "getIntValue", e);
        }
        return val;
    }

    public String getStringValue(String name, String defaultValue) {
        String val = defaultValue;
        try {
            if(name != null && mConfigMap.containsKey(name)) {
                val = mConfigMap.get(name);
            }
        } catch (Exception e) {
            //LoggerFactory.getTraceLogger().error(TAG, "getStringValue", e);
        }
        return val;
    }
}

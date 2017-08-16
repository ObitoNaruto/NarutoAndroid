package com.naruto.mobile.base.framework.info;

import java.util.UUID;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;


/**
 * 应用的管理以及配置信息
 * 
 * @author sanping.li@alipay.com
 *
 */
public class AppInfo {
    private static AppInfo mInstance;
    private Context mContext;

    /**
     * ActivityManager
     */
    private ActivityManager mActivityManager;
    
    private String mProductID;
    
    /**
     * 软件版本
     */
    private String mProductVersion;
    
    private String mChannels;
    
    /**
     * 是否调试版本
     */
    private boolean mDebuggable;
    /**
     * 当前运行的进程ID
     */
    private int mPid;
    
    private String mAwid;
    
   

    private AppInfo(Context context) {
        mContext = context;
        
        //永远是最后一行
        init();
    }

    /**
     * 获取应用的管理以及配置信息实例
     * 
     * @return 性能记录器
     */
    public static AppInfo getInstance() {
        if (mInstance == null)
            throw new IllegalStateException(
                "AppManager must be created by calling createInstance(Context context)");
        return mInstance;
    }

    /**
     * 创建应用的管理以及配置信息实例
     * 
     * @param context 上下文
     * @return
     */
    public static synchronized AppInfo createInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppInfo(context);
        }
        return mInstance;
    }

    /**
     * 初始化
     */
    private void init() {
        try {
            String tpackageName = mContext.getPackageName();
            Log.d("AppInfo", "getPackageName "+tpackageName);
            
            PackageInfo mPackageInfo = mContext.getPackageManager().getPackageInfo(tpackageName, 0);
            mProductVersion = mPackageInfo.versionName;

            ApplicationInfo applicationInfo = mContext.getPackageManager().getApplicationInfo(
                mContext.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            if ((applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                // development mode
                mDebuggable = true;
            }

            mActivityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
            mPid = android.os.Process.myPid();
            mProductID = "";//mContext.getString(R.string.useragent);
            mChannels = "";//CacheSet.getInstance(mContext).getString("channels");
            
            mAwid = UUID.randomUUID().toString();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("AppManager", "init: " + e == null ? "" : e.getMessage());
        }
    }

    /**
     * 是否是开发状态
     */
    public boolean isDebuggable() {
        return mDebuggable;
    }

    /**
     * 获取当前运行的进程ID
     * 
     * @return 进程ID
     */
    public int getPid() {
        return mPid;
    }
    
    

    public String getProductID(){
        if(mProductID.equals(""))
        {
            return "Android-container";
        }
        else
        {
            return "Android-container" + "_" + mProductID;
        }
    }
    public String getmProductVersion() {
        return mProductVersion;
    }

    public String getmChannels() {
        return mChannels;
    }

    public String getmAwid() {
        return mAwid;
    }

    /**
     * 获取当前所占内存
     * 
     * @return 当前所占内存
     */
    public long getTotalMemory() {
        android.os.Debug.MemoryInfo[] mems = mActivityManager
            .getProcessMemoryInfo(new int[] { mPid });
        return mems[0].getTotalPrivateDirty();
    }

    /**
     * 获取应用的data/data/....File目录
     * 
     * @return File目录
     */
    public String getFilesDirPath() {
        return mContext.getFilesDir().getAbsolutePath();
    }

    /**
     * 获取应用的data/data/....Cache目录
     * 
     * @return Cache目录
     */
    public String getCacheDirPath() {
        return mContext.getCacheDir().getAbsolutePath();
    }
}

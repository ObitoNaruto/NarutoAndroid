package com.naruto.mobile.base.framework.info;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.DisplayMetrics;
import android.util.Log;

//import com.ut.device.UTDevice;

/**
 * 设备管理工具
 * 
 * @author sanping.li@alipay.com
 *
 */
public class DeviceInfo {
    private static final String TAG    = "DeviceInfo";

    private static DeviceInfo   mInstance;

    /**
     * 上下文
     */
    private Context             mContext;
    /**
     * 屏幕宽度
     */
    private int                 mScreenWidth;
    /**
     * 屏幕高度
     */
    private int                 mScreenHeight;
    /**
     * 屏幕Dencity
     */
    private int                 mDencity;
    /**
     * clientid
     */
    private String              mClientId;
    /**
     * IMEI
     */
    private String              mImei;
    /**
     * IMSI
     */
    private String              mImsi;
    /**
     * 原始 IMEI
     */
    private String              defImei;
    /**
     * 原始 IMSI
     */
    private String              defImsi;

    /** 
     * 
     *  */
    private String              mMobileBrand;
    
    /**
     * 运营商
     */
    private String              operator;

    /** 
     * 
     *  */
    private String              mMobileModel;

    private String              mSystemVersion;

    private boolean             mRooted;

    private String              mDid;

    private String              mClientKey;
    
    private AtomicBoolean  mDidGenerating = new AtomicBoolean(false);
    
    
    //中国移动
    public static final String  CMCC                  = "cmcc";

    //中国联通
    public static final String  CUCC                  = "cucc";

    //中国电信
    public static final String  CTCC                  = "ctcc";
    
    //未知
    public static final String  UNKNOWN               = "unknown";

    public static final String  NULL                  = "null";

    /**
     * 任意个0的字符串的正则表达式
     */
    public static final String  ANY_ZERO_STR          = "[0]+";
    
    /**
     * 定义硬件标志的非法长度，目前暂定为5
     */
    public static final int     HARDWARD_INVALID_LEN  = 5;
    
    
    /*
     * 字母表
     */
    public String[] baseString =          { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                                            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                                            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
                                            "u", "v", "w", "x", "y", "z", "A", "B", "C", "D",
                                            "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
                                            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
                                            "Y", "Z"
                                            };
    
    /**
     * clientKey的最大长度
     */
    public final static int CLIENT_KEY_MAX_LENGTH = 10;
    
    public final static int IMEI_LEN = 15;
    
    
    //同步锁
    private Object mLock = new Object();
    
    
    private static boolean reforceInit = false;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat    format = new SimpleDateFormat("yyMMddHHmmssSSS");
    
    private WifiManager mWifiManager;

    private DeviceInfo(Context context) {
        mContext = context;
    }

    /**
     * 获取设备信息实例
     * 
     * @return 性能记录器
     */
    public static synchronized DeviceInfo getInstance() {
        if (mInstance == null)
            throw new IllegalStateException(
                "DeviceManager must be create by call createInstance(Context context)");
        return mInstance;
    }

    /**
     * 创建设备信息示例
     * 
     * @param context 上下文
     * @return 性能记录器
     */
    public static synchronized DeviceInfo createInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DeviceInfo(context);
            mInstance.init();
        }
        return mInstance;
    }
    
    /**
     * 重新强制初始化clientId
     */
    public static synchronized void getSecurityInstance(){
        if (!reforceInit) {
            if (mInstance != null) {
                mInstance.reInitClientId();
            }
        }
    }

    /**
     * 重新初始化IMSI/IMEI等
     */
    private void reInitClientId() {
        mClientId = initClientId();
        mImei = initIMEI();
        mImsi = initIMSI();
        
        reforceInit = true;
    }

    /**
     * 初始化
     */
    private void init() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
        mDencity = displayMetrics.densityDpi;
        mClientId = initClientId();
        mImei = initIMEI();
        mImsi = initIMSI();
        mClientKey = initClientKey();
//        mDid = mClientId + "|" + mClientKey;
    	generateUtDid();
        
        mMobileBrand = Build.BRAND;
        mMobileModel = Build.MODEL;
        mSystemVersion = Build.VERSION.RELEASE;
        mRooted = rooted();
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);  
    }

    /**
     * 生成utdid,需要在子线程生成
     */
    private void generateUtDid() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mDidGenerating.set(true);
				mDid = "";/*UTDevice.getUtdid(mContext);*/
				mDidGenerating.set(false);
				synchronized (mLock) {
					mLock.notifyAll();
				}
			}
		}).start();
	}

	/**
     * 生成10位随机数
     * @return
     */
    private String initClientKey() {
        String clientKey = null;
        SharedPreferences settings = mContext.getSharedPreferences(mContext.getPackageName()
                                                                   + ".config",
            Context.MODE_PRIVATE);
        clientKey = settings.getString("clientKey", "");
        if(!"".equals(clientKey))
        {
        	 return clientKey;
        }
      /*  if (isValidateClientKey(clientKey)) {
            return clientKey;
        }*/
        clientKey = generateClientKey();
        settings.edit().putString("clientKey", clientKey).commit();
        return clientKey;
    }

    /**
     * 刷新clientKey
     * @return
     */
    public String refleshClientKey() {
        SharedPreferences settings = mContext.getSharedPreferences(mContext.getPackageName()
                                                                   + ".config",
            Context.MODE_PRIVATE);
        String clientKey = generateClientKey();
        settings.edit().putString("clientKey", clientKey).commit();
        mClientKey = clientKey;
        return clientKey;
    }

    /**
     * 先随机生成一个字符串
     * 
     * @return
     */
    private String generateClientKey() {

        Random random = new Random(System.currentTimeMillis());
        int length = baseString.length;
        String randomString = "";
        for (int i = 0; i < CLIENT_KEY_MAX_LENGTH; i++) {
            randomString += baseString[random.nextInt(length)];
        }
        
        return randomString;
    }

    private boolean rooted() {
        boolean ret = false;
        Class<?> cla = null;
        Object value = null;
        try {
            cla = Class.forName("android.os.SystemProperties");
            @SuppressWarnings("rawtypes")
            Class[] claArrayTypes = { String.class };
            Method meth = cla.getMethod("get", claArrayTypes);
            // Method meth = cla.getMethod("secure", claArrayTypes);
            Object[] arglist = { "ro.secure" };
            value = meth.invoke(null, arglist);
        } catch (Exception e) {
            Log.e(TAG, "",e);
        }
        if (value != null && "1".equals(value)) {
            ret = false;
        } else if (value != null && "0".equals(value)) {
            ret = true;
        }
        return ret;
    }

    public String getmDid() {
    	if(mDidGenerating.get()){//如果utdid生成过程中，等待生成
    		synchronized (mLock) {
    			try {
					mLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
    	}
    	
        return (mDid == null || "".equals(mDid)) ? mClientId + "|"+ mClientKey :mDid;
    }

    public void setmDid(String mDid) {
        this.mDid = mDid;
    }

    public int getmScreenWidth() {
        return mScreenWidth;
    }

    public int getmScreenHeight() {
        return mScreenHeight;
    }

    public String getmMobileBrand() {
        return mMobileBrand;
    }

    public String getmMobileModel() {
        return mMobileModel;
    }

    public String getmSystemVersion() {
        return mSystemVersion;
    }

    public boolean ismRooted() {
        return mRooted;
    }

    /**
     * 获取设备屏幕宽度
     * 
     * @return 屏幕宽度
     */
    public int getScreenWidth() {
        return mScreenWidth;
    }

    /**
     * 获取设备屏幕高度
     * 
     * @return 屏幕高度
     */
    public int getScreenHeight() {
        return mScreenHeight;
    }

    /**
     * 获取设备屏幕宽度
     * 
     * @return 屏幕宽度
     */
    public int getDencity() {
        return mDencity;
    }

    /**
     * 获取OS版本
     * 
     * @return OS版本
     */
    public String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取UserAgent
     * 
     * @return UserAgent
     */
    public String getUserAgent() {
        return Build.MANUFACTURER + Build.MODEL;
    }

    /**
     * IMEI
     * 
     * @return IMEI
     */
    public String getImei() {
        return mImei;
    }

    /**
     * IMSI
     * 
     * @return IMSI
     */
    public String getImsi() {
        return mImsi;
    }

    /**
     * 获取外接SD卡上context.getPackageName()下面的目录，如果不存在则创建
     * 
     * @param context 上下文
     * @param dir 外接SD卡上context.getPackageName()下面的目录名
     * @return
     */
    public String getExternalStoragePath(String dir) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getPath() + File.separatorChar
                          + mContext.getPackageName();
            File file = new File(path);
            if (!file.exists() && !file.mkdir()) {
                Log.e(TAG, "fail to creat " + dir + " dir:" + path);
                return path;
            } else if (!file.isDirectory()) {
                Log.e(TAG, dir + " dir exist,but not directory:" + path);
                return null;
            }

            path = path + File.separatorChar + dir;
            file = new File(path);
            if (!file.exists() && !file.mkdir()) {
                Log.e(TAG, "fail to creat " + dir + " dir:" + path);
                return path;
            } else if (!file.isDirectory()) {
                Log.e(TAG, dir + " dir exist,but not directory:" + path);
                return null;
            } else {
                return path;
            }
        }
        return null;
    }

    /**
     * 是否为合法的字符串，对于imsi或者imei
     * @param imsiOrimei
     * @return
     */
    private boolean isValid(String imsiOrimei) {
        if( imsiOrimei == null || imsiOrimei.trim().length() == 0){
            return false;
        }
        String trimS = imsiOrimei.trim();
        if(trimS.equalsIgnoreCase(UNKNOWN) || trimS.equalsIgnoreCase(NULL)){
            return false;
        }
        if (trimS.matches(ANY_ZERO_STR)) {
            return false;
        }
        if(trimS.length() <= HARDWARD_INVALID_LEN){
            return false;
        }
        return true;
    }
    
    /**
     * 判断是否为空字符串
     * @param s
     * @return
     */
    private boolean isBlank(String s) {
        return s == null || s.trim().length() == 0;
    }

    /**
     * 初始化clientId: 
     * 如果本地已经有保存的clientId且没有改变，则使用保存的。
     * 如果本地保存了，但imei或imsi任意一个改变，则使用新的。取到的imei或imsi的值为空时不算改变。
     * 如果本地没有保存，则使用取到的值，如果取到的值为空，则随机生成一个15位字符串。
     * 这样可以保证对以前版本的兼容。同时避免imsi有时能取到，有时取不到时clientId的震荡变化。
     * @param context
     * @return
     */
    private String initClientId() {
        SharedPreferences settings = mContext.getSharedPreferences(mContext.getPackageName()
                                                                   + ".config",
            Context.MODE_PRIVATE);
        Editor editor = settings.edit();
        TelephonyManager telMgr = (TelephonyManager) mContext
            .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telMgr.getDeviceId();
        Log.d(TAG, "origin imei:" + imei);
//        if (isInvalid(imei))//imei有时候会为unknown或null，其实就意味着取不到，此时应该置为空
//        {
//            imei = "";
//        }
//        LogCatLog.d(TAG, "changed imei:" + imei);
        /**默认值**/
        defImei = imei;
        String imsi = telMgr.getSubscriberId();
        Log.d(TAG, "origin imsi:" + imsi);
        /**默认值**/
        defImsi = imsi;
        String savedClientId = settings.getString("clientId", "");
        Log.d(TAG, "saved clientid:" + savedClientId);
        String newClientId = savedClientId;
        if (isValidClientID(savedClientId)) //如果保存的clientId合法
        {
            Log.d(TAG, "client id is valid:" + savedClientId);
            String savedImsi = savedClientId.substring(0, IMEI_LEN);
            if (isValid(imsi)) {
                String imsiT = this.replaceNonHexChar(imsi);
                if (imsiT.length() > IMEI_LEN) {
                    imsiT = imsiT.substring(0, IMEI_LEN);
                }
                if (!savedImsi.startsWith(imsiT)) {
                    savedImsi = imsi;
                }
            }
            String savedImei = savedClientId.substring(savedClientId.length() - IMEI_LEN,
                savedClientId.length());
            if(isValid(imei)){
                String imeiT = this.replaceNonHexChar(imei);
                if (imeiT.length() > IMEI_LEN) {
                    imeiT = imeiT.substring(0, IMEI_LEN);
                }
                if (!savedImei.startsWith(imeiT)) {
                    savedImei = imei;
                }
            }
            Log.d(TAG, "client id is valid:" + savedImsi + "|" + savedImei);
            newClientId = normalizedClientId(savedImsi, savedImei);
            Log.d(TAG, "normarlize, imsi:" + imsi + ",imei:" + imei + ",newClientId:"
                             + newClientId);
            editor.putString("clientId", newClientId);
            editor.commit();
        } else {
            Log.d(TAG, "client is is not valid, imei:" + imei + ",imsi:" + imsi);
            if (!isValid(imei))//如果为空，则用当前时间随机生成一个
                imei = getTimeStamp();
            if (!isValid(imsi))//如果为空，则用当前时间随机生成一个
                imsi = getTimeStamp();
            newClientId = normalizedClientId(imsi, imei);
            Log.d(TAG, "normalize, imei:" + imei + ",imsi:" + imsi + ",newClientId:"
                             + newClientId);
            editor.putString("clientId", newClientId);
            editor.commit();
        }
        return newClientId;
    }

    /**
     * 判断clientId是否合法
     * @param clientID
     * @return
     */
    private boolean isValidClientID(String clientID) {
        if (isBlank(clientID))
            return false;
        return clientID.matches("[[a-z][A-Z][0-9]]{15}\\|[[a-z][A-Z][0-9]]{15}");
    }

    /**
     * 将imei或imsi正规化，不足位数则补全，有非数字则用0代替。
     * @param imeiOrImsi
     * @return
     */
    private String normalize(String imeiOrImsi) {
        if (!isValid(imeiOrImsi)) {
            imeiOrImsi = getTimeStamp();
        }
        imeiOrImsi = (imeiOrImsi + "123456789012345").substring(0, IMEI_LEN);
        return replaceNonHexChar(imeiOrImsi);
    }

    /**
     * 将非16进制字符替换成'0'
     * @param imei
     * @return
     */
    private String replaceNonHexChar(String imei) {
        if (isBlank(imei))
            return imei;
        byte[] byteClientId = imei.getBytes();
        for (int i = 0; i < byteClientId.length; i++) {
            if (!isDigitOrAlphaBelta(byteClientId[i])) //如果不是十六进制字符，则用0替换。
                byteClientId[i] = '0';
        }
        return new String(byteClientId);
    }

    /**
     * 判断c是否是十六进制字符
     * @param c
     * @return
     */
    private boolean isDigitOrAlphaBelta(byte c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }


    /**
     * 根据imsi和imei得到规范化的clientId
     * @param imsi
     * @param imei
     * @return
     */
    private String normalizedClientId(String imsi, String imei) {
        return normalize(imsi) + "|" + normalize(imei);
    }

    public String getTimeStamp() {
        String timeStamp = this.format.format(System.currentTimeMillis());
        return timeStamp;
    }

    private String initIMEI() {
        if (isValidClientID(mClientId))
            return mClientId.substring(mClientId.length() - IMEI_LEN);
        else
            return "";
    }

    private String initIMSI() {
        if (isValidClientID(mClientId))
            return mClientId.substring(0, mClientId.length() - IMEI_LEN-1);
        else
            return "";
    }

    public String getDefImei() {
        return defImei;
    }

    public void setDefImei(String defImei) {
        this.defImei = defImei;
    }

    public String getDefImsi() {
        return defImsi;
    }

    public void setDefImsi(String defImsi) {
        this.defImsi = defImsi;
    }

    /**
     * 获取ClientId
     * 
     * @return ClientId
     */
    public String getClientId() {
        return mClientId;
    }

    public String getAccessPoint() {
        String apn = "wifi";

        try {
            ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();

            String extra = ni.getExtraInfo();
            if (extra == null || extra.indexOf("none") != -1)
                apn = ni.getTypeName();
            else
                apn = extra;
        } catch (Exception e) {
            Log.e(TAG, "",e);
        }

        apn = apn.replace("internet", "wifi");
        apn = apn.replace("\"", "");
        return apn;
    }

    public String getCellInfo() {
        String cellInfo = "-1;-1";

        try {
            TelephonyManager tm = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
            //haitong
            CellLocation temp = tm.getCellLocation();
            if(temp!=null) {
            	StringBuilder sbcellInfo = new StringBuilder();
                if(temp instanceof GsmCellLocation) {
    	            GsmCellLocation gsmcl = (GsmCellLocation) temp;//tm.getCellLocation();
    	            int cellid = gsmcl.getCid();
    	            int lac = gsmcl.getLac();
    	   	            
    	            sbcellInfo.append(lac);
    	            sbcellInfo.append(";");
    	            sbcellInfo.append(cellid);
    	
    	            cellInfo = sbcellInfo.toString();
                } else if (temp instanceof CdmaCellLocation) {
                	CdmaCellLocation cdmacl = (CdmaCellLocation) temp;
                	int cellid = cdmacl.getBaseStationLatitude();
                	int lac = cdmacl.getBaseStationLongitude();

                	sbcellInfo.append(lac);
    	            sbcellInfo.append(";");
    	            sbcellInfo.append(cellid);
    	
    	            cellInfo = sbcellInfo.toString();
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "",e);
        }

        return cellInfo;
    }

    public String getmClientKey() {
        return mClientKey;
    }

    /**
     * xuxi 
     * @return
     */
    public String getKey() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String strKey = format.format(date);
        return strKey;
    }

    
    /**
     * 初始化运营商
     */
    public String getOperator(){
        if(operator == null){
            TelephonyManager telephonyManager = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String imsi = telephonyManager.getSubscriberId();
            if(imsi == null)
            	return UNKNOWN;
            
            if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007")) {
                operator =  CMCC;
            } else if (imsi.startsWith("46001")) {
                operator =  CUCC;

            } else if (imsi.startsWith("46003")) {
                operator = CTCC;
            } else {
                operator = UNKNOWN;
            }
        }
        return operator;
    }

    /**
     * 判断是否安装某应用
     * @param packageName
     * @return
     */
    public boolean isInstalled(String packageName){
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(packageName, 0);
            return packageInfo!=null;
        } catch (NameNotFoundException e) {
            Log.e(TAG, "",e);
            return false;
        }
    }
    
    /**
     * 获取MAC地址
     * @return
     */
    public String getMacAddress(){
        WifiInfo info = mWifiManager.getConnectionInfo(); 
        return info.getMacAddress();
    }
    
    /**
     * 获取SSID
     * @return
     */
    public String getSSID(){
        WifiInfo info = mWifiManager.getConnectionInfo(); 
        return info.getSSID();
    }
    
    /**
     * 获取经度
     * @return
     */
    public String getLatitude(){    
//        LBSLocation location = LBSLocationManagerProxy.getInstance(mContext).getLastKnownLocation(mContext);
//        if ( location != null ){
//            return String.valueOf(location.getLatitude());
//        }        
        return null;
    }
    
    /**
     * 获取纬度
     * @return
     */
    public String getLongitude(){    
//        LBSLocation location = LBSLocationManagerProxy.getInstance(mContext).getLastKnownLocation(mContext);
//        if ( location != null ){
//            return String.valueOf(location.getLongitude());
//        }      
        return null;  
    }
    
    public void installApk(String path){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        
        intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }
}

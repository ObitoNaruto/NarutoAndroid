/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Environment;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
//import com.taobao.wireless.security.sdk.SecurityGuardManager;
//import com.taobao.wireless.security.sdk.SecurityGuardParamContext;
//import com.taobao.wireless.security.sdk.securesignature.ISecureSignatureComponent;
//import com.taobao.wireless.security.sdk.securesignature.SecureSignatureDefine;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.BaseDownResp;
//import com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
//import com.alipay.android.phone.mobilesdk.storage.utils.FileUtils;
//import com.alipay.mobile.common.transport.utils.NetworkUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.naruto.mobile.framework.rpc.myhttp.utils.NetworkUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.BaseDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * Django SDK的通用工具类
 *
 * @author jinzhaoyu
 */
public class DjangoUtils {
    private static final String TAG = "DjangoUtils";
    /**
     * The Unix separator character.
     */
    private static final char UNIX_SEPARATOR = '/';

    /**
     * The Windows separator character.
     */
    private static final char WINDOWS_SEPARATOR = '\\';

    /**
     * The extension separator character.
     *
     * @since 1.4
     */
    public static final char EXTENSION_SEPARATOR = '.';

    /**
     * The extension separator String.
     *
     * @since 1.4
     */
    public static final String EXTENSION_SEPARATOR_STR = Character.toString(EXTENSION_SEPARATOR);
    private static final String ExtDataTunnel = "ExtDataTunnel";

    public static final int DJANGO_NETWORK_TYPE_UNKNOWN = 0;
    public static final int DJANGO_NETWORK_TYPE_ETHERNET = 1;
    public static final int DJANGO_NETWORK_TYPE_WIFI = 2;
    public static final int DJANGO_NETWORK_TYPE_2G = 3;
    public static final int DJANGO_NETWORK_TYPE_3G = 4;
    public static final int DJANGO_NETWORK_TYPE_4G = 5;
    public static final int DJANGO_NETWORK_TYPE_NONE = 127;

    /**
     * @param appKey
     * @param timestamp
     * @return
     */
    public static String genSignature(String appKey, Long timestamp) {
        StringBuilder builder = new StringBuilder(appKey);
        builder.append(timestamp);
        return genSignature(appKey,builder.toString());
    }

    public static String genSignature(String appKey, String content){
//        SecurityGuardManager sgMgr = null;
//        String sign = "";
//        try {
//            sgMgr = SecurityGuardManager.getInstance(AppUtils.getApplicationContext());
//            ISecureSignatureComponent signComp = sgMgr.getSecureSignatureComp();
//
//            HashMap<String, String> paramMap = new HashMap<String, String>();
//            paramMap.put(SecureSignatureDefine.SG_KEY_SIGN_INPUT,content);
//            SecurityGuardParamContext paramContext = new SecurityGuardParamContext();
//            paramContext.appKey = appKey;
//            paramContext.paramMap = paramMap;
//            paramContext.requestType = SecureSignatureDefine.SIGN_XIAMI;
//
//            sign = signComp.signRequest(paramContext);
//        } catch (Exception e) {
//            Logger.E(TAG, e, "genSignature exp ");
//        }
//
//        //Logger.D(TAG, "signatureSecret sign=" + sign);
//
//        return sign;
        return "";
    }

    /**
     * 获取设备唯一ID
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        String deviceId = null;
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (manager != null) {
            deviceId = manager.getDeviceId();
        }
        if (deviceId != null) {
            return "IMEI:" + deviceId;
        }
        deviceId = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
        if (deviceId != null) {
            return "ANDROID:" + deviceId;
        }
        deviceId = Installation.id(context);
        return "UUID:" + deviceId;
    }

    /**
     * 查询系统中已安装的app信息
     *
     * @param context
     * @param all     true:所有app信息，false:只返回非系统应用信息
     * @return
     */
    public static List<PackageInfo> getAllApps(Context context, boolean all) {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = (PackageInfo) paklist.get(i);
            if (all) {
                apps.add(pak);
            } else {
                // 判断是否为非系统预装的应用程序
                if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                    // customs applications
                    apps.add(pak);
                }
            }
        }
        return apps;
    }

    /**
     * 判断是否已经安装某个包的apk
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isInstall(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            throw new IllegalArgumentException("packageName must not blank.");
        }
        if (context == null) {
            throw new IllegalArgumentException("context must not null.");
        }
        List<PackageInfo> apps = getAllApps(context, true);
        if (apps == null) {
            return false;
        }
        for (PackageInfo pkg : apps) {
            if (packageName.equals(pkg.packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解决{@code JSONObject}对与value为null时返回“null”字符串的问题
     *
     * @param json
     * @param key
     * @return
     */
    public static String getJsonString(JSONObject json, String key) {
        if (json.isNull(key)) {
            return null;
        } else {
            return json.optString(key);
        }
    }

    /**
     * 释放下载时产生的响应流
     *
     * @param baseDownResp
     */
    public static void releaseDownloadResponse(BaseDownResp baseDownResp) {
        if (baseDownResp == null) {
            return;
        }
        try {
            releaseConnection(baseDownResp.getMethod(), baseDownResp.getResp());
        } catch (Exception e) {
            Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
        }
    }

    /**
     * 释放Http连接资源
     *
     * @param method
     * @param resp
     */
    public static void releaseConnection(HttpRequestBase method, HttpResponse resp) {
        if (method != null) {
            method.abort();
        }

        if (resp != null) {
            HttpClientUtils.consumeQuietly(resp.getEntity());
        }
    }


//	/**
//	 * 给请求做HMAC签名。
//	 * 
//	 * @param sortedParams
//	 *            所有字符型的TOP请求参数
//	 * @param secret
//	 *            签名密钥
//	 * @return 签名
//	 * @throws IOException
//	 */
//	public static String signRequest(TreeMap<String, String> sortedParams, String secret)
//			throws IOException {
//		// 第一步：把字典按Key的字母顺序排序,参数使用TreeMap已经完成排序
//		Set<Entry<String, String>> paramSet = sortedParams.entrySet();
//
//		// 第二步：把所有参数名和参数值串在一起
//		StringBuilder query = new StringBuilder();
//		for (Entry<String, String> param : paramSet) {
//			if (!TextUtils.isEmpty(param.getKey())
//					&& !TextUtils.isEmpty(param.getValue())) {
//				query.append(param.getKey()).append(param.getValue());
//			}
//		}
//
//		// 第三步：使用MD5/HMAC加密
//		byte[] bytes = encryptHMAC(query.toString(), secret);
//
//		// 第四步：把二进制转化为大写的十六进制
//		return new String(Hex.encodeHex(bytes, false));
//	}
//	
//	private static byte[] encryptHMAC(String data, String secret)
//			throws IOException {
//		byte[] bytes = null;
//		try {
//			SecretKey secretKey = new SecretKeySpec(secret.getBytes("UTF-8"),
//					"HmacMD5");
//			Mac mac = Mac.getInstance(secretKey.getAlgorithm());
//			mac.init(secretKey);
//			bytes = mac.doFinal(data.getBytes("UTF-8"));
//		} catch (GeneralSecurityException gse) {
//			String msg = getStringFromException(gse);
//			throw new IOException(msg);
//		}
//		return bytes;
//	}
//
//	private static String getStringFromException(Throwable e) {
//		String result = "";
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		PrintStream ps = new PrintStream(bos);
//		try {
//			result = bos.toString("UTF-8");
//		} catch (UnsupportedEncodingException e1) {
//			// won't happen
//		}
//		return result;
//	}

    /**
     * 获取文件的真实后缀名。目前只支持JPG, GIF, PNG, BMP四种图片文件。
     *
     * @param bytes 文件字节流
     * @return JPG, GIF, PNG or null
     */
    public static String getFileSuffix(byte[] bytes) {
        if (bytes == null || bytes.length < 10) {
            return null;
        }

        if (bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') {
            return "GIF";
        } else if (bytes[1] == 'P' && bytes[2] == 'N' && bytes[3] == 'G') {
            return "PNG";
        } else if (bytes[6] == 'J' && bytes[7] == 'F' && bytes[8] == 'I'
                && bytes[9] == 'F') {
            return "JPG";
        } else if (bytes[0] == 'B' && bytes[1] == 'M') {
            return "BMP";
        } else {
            return null;
        }
    }

    /**
     * 获取文件的真实媒体类型。目前只支持JPG, GIF, PNG, BMP四种图片文件。
     *
     * @param bytes 文件字节流
     * @return 媒体类型(MEME-TYPE)
     */
    @SuppressLint("DefaultLocale")
    public static String getMimeType(byte[] bytes) {
        String suffix = getFileSuffix(bytes);
        String mimeType;

        if ("JPG".equals(suffix)) {
            mimeType = "image/jpeg";
        } else if ("GIF".equals(suffix)) {
            mimeType = "image/gif";
        } else if ("PNG".equals(suffix)) {
            mimeType = "image/png";
        } else if ("BMP".equals(suffix)) {
            mimeType = "image/bmp";
        } else {
            mimeType = "application/octet-stream";
        }

        return mimeType;
    }

    /**
     * Returns the index of the last extension separator character, which is a dot.
     * <p/>
     * This method also checks that there is no directory separator after the last dot.
     * To do this it uses {@link #indexOfLastSeparator(String)} which will
     * handle a file in either Unix or Windows format.
     * <p/>
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param filename the filename to find the last path separator in, null returns -1
     * @return the index of the last separator character, or -1 if there
     * is no such character
     */
    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        }
        int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? -1 : extensionPos;
    }

    /**
     * Returns the index of the last directory separator character.
     * <p/>
     * This method will handle a file in either Unix or Windows format.
     * The position of the last forward or backslash is returned.
     * <p/>
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param filename the filename to find the last path separator in, null returns -1
     * @return the index of the last separator character, or -1 if there
     * is no such character
     */
    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        }
        int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }

    /**
     * Gets the extension of a filename.
     * <p/>
     * This method returns the textual part of the filename after the last dot.
     * There must be no directory separator after the dot.
     * <pre>
     * foo.txt      --> "txt"
     * a/b/c.jpg    --> "jpg"
     * a/b.txt/c    --> ""
     * a/b/c        --> ""
     * </pre>
     * <p/>
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param filename the filename to retrieve the extension of.
     * @return the extension of the file or an empty string if none exists or {@code null}
     * if the filename is {@code null}.
     */
    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfExtension(filename);
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }


    public static String getMediaDir(Context mContext, String subPath) throws DjangoClientException {
        String mBaseDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File root = mContext.getExternalFilesDir(null);//Environment.getExternalStorageDirectory().getAbsolutePath();
            //这里有兼容性问题
            if(root != null){
                mBaseDir = root.getAbsolutePath();
            }else{
                /**
                 * 解决小米手机上无法创建应用目录的问题，若系统api返回"/",则迁移到sdcard/ExtDataTunnel/files目录下
                 */
                String sdcard = ""/*FileUtils.getSDPath()*/;
                if (!TextUtils.isEmpty(sdcard)) {
                    mBaseDir = sdcard + File.separator + ExtDataTunnel + File.separator + "files";
                }else{
                    mBaseDir = mContext.getCacheDir().getAbsolutePath();
                }
            }
        } else {
            mBaseDir = mContext.getCacheDir().getAbsolutePath();
        }
        mBaseDir = mBaseDir + File.separator + DjangoConstant.STORE_PATH + subPath;
        File dir = new File(mBaseDir);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }

        Logger.D(DjangoClient.LOG_TAG, "getBaseDir mBaseDir: " + mBaseDir);
        return dir.getAbsolutePath();
    }

    public static int convertNetworkType(final int type) {
        switch (type) {
//            case NetworkUtils.NETWORK_TYPE_2G:
//                return DJANGO_NETWORK_TYPE_2G;
//            case NetworkUtils.NETWORK_TYPE_3G:
//                return DJANGO_NETWORK_TYPE_3G;
//            case NetworkUtils.NETWORK_TYPE_WIFI:
//                return DJANGO_NETWORK_TYPE_WIFI;
//            case NetworkUtils.NETWORK_TYPE_4G:
//                return DJANGO_NETWORK_TYPE_4G;
//            case NetworkUtils.NETWORK_TYPE_INVALID:
//                return DJANGO_NETWORK_TYPE_NONE;
            default:
                return DJANGO_NETWORK_TYPE_UNKNOWN;
        }
    }

    private static Pattern pattern = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
    /**
     * 判断是否为合法IP
     * @return the ip
     */
    public static boolean isValidIp(String ipAddress) {
        if(TextUtils.isEmpty(ipAddress)){
            return false;
        }

        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }
}

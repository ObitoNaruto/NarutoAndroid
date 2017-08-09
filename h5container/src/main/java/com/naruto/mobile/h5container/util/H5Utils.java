
package com.naruto.mobile.h5container.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.naruto.mobile.h5container.env.H5Environment;

import org.apache.http.util.ByteArrayBuffer;

/**
 * Trivial utility class for H5 container implement
 */
public class H5Utils {
    public static final String TAG = "H5Utils";

    /**
     * Get external application package info, return null if not exists
     * @param context
     * @param packageName
     * @return
     */
    public static PackageInfo getPackageInfo(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        if (packageInfos == null) {
            return null;
        }
        for (int index = 0; index < packageInfos.size(); index++) {
            PackageInfo packageInfo = packageInfos.get(index);
            final String name = packageInfo.packageName;
            if (packageName.equals(name)) {
                return packageInfo;
            }
        }
        return null;
    }

    public static String getString(Bundle bundle, String key) {
        return getString(bundle, key, "");
    }

    public static String getString(Bundle bundle, String key, String df) {
        if (df == null) {
            df = "";
        }
        return getValue(bundle, key, df);
    }

    public static boolean getBoolean(Bundle bundle, String key, boolean df) {
        return getValue(bundle, key, df);
    }

    public static int getInt(Bundle bundle, String key) {
        return getInt(bundle, key, 0);
    }

    public static int getInt(Bundle bundle, String key, int df) {
        return getValue(bundle, key, df);
    }

    public static double getDouble(Bundle bundle, String key) {
        return getDouble(bundle, key, 0.0);
    }

    public static double getDouble(Bundle bundle, String key, double df) {
        return getValue(bundle, key, df);
    }

    public static boolean contains(Bundle bundle, String key) {
        if (bundle == null || TextUtils.isEmpty(key)) {
            return false;
        }
        return bundle.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(Bundle bundle, String key, T df) {
        if (bundle == null || TextUtils.isEmpty(key)) {
            return df;
        }
        if (df == null) {
            return df;
        }

        if (!bundle.containsKey(key)) {
            return df;
        }
        T value = df;
        Object obj = bundle.get(key);
        if (obj != null && value.getClass().isAssignableFrom(obj.getClass())) {
            value = (T) obj;
        } else {
            H5Log.d(TAG, "[key] " + key + " [value] " + obj);
        }
        return value;
    }

    public static JSONObject toJSONObject(Bundle bundle) {
        JSONObject joBundle = new JSONObject();
        if (bundle == null) {
            return joBundle;
        }

        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            Object value = bundle.get(key);
            joBundle.put(key, value);
        }
        return joBundle;
    }

    public static String getString(JSONObject params, String key) {
        return getString(params, key, "");
    }

    public static String getString(JSONObject params, String key, String df) {
        if (df == null) {
            df = "";
        }
        return getValue(params, key, df);
    }

    public static boolean getBoolean(JSONObject params, String key, boolean df) {
        return getValue(params, key, df);
    }

    public static int getInt(JSONObject params, String key) {
        return getInt(params, key, 0);
    }

    public static int getInt(JSONObject params, String key, int df) {
        return getValue(params, key, df);
    }

    public static JSONObject getJSONObject(JSONObject params, String key,
            JSONObject df) {
        if (df == null) {
            df = new JSONObject();
        }
        return getValue(params, key, df);
    }

    public static JSONArray getJSONArray(JSONObject params, String key,
            JSONArray df) {
        if (df == null) {
            df = new JSONArray();
        }
        return getValue(params, key, df);
    }

    public static boolean contains(JSONObject params, String key) {
        if (params == null || params.isEmpty()) {
            return false;
        }

        return params.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(JSONObject params, String key, T df) {
        if (params == null || params.isEmpty()) {
            return df;
        }
        if (df == null) {
            return df;
        }

        if (!params.containsKey(key)) {
            return df;
        }

        T value = df;
        Object obj = params.get(key);
        if (obj != null && value.getClass().isAssignableFrom(obj.getClass())) {
            value = (T) obj;
        } else {
            H5Log.w(TAG, "[key] " + key + " [value] " + obj);
        }
        return value;
    }

    public static String getConfigString(Context context, String key) {
        String value = null;
        try {
            Uri contentUri = Uri.parse("content://com.alipay.setting/" + key);
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(contentUri, new String[] {
                    ""
            }, "",
                    new String[] {}, "");
            while (cursor.moveToNext()) {
                value = cursor.getString(0);
            }
            cursor.close();
        } catch (Exception e) {

        }
        return value;
    }

    public static boolean getConfigBoolean(Context context, String key) {
        boolean value = false;
        try {
            Uri contentUri = Uri.parse("content://com.alipay.setting/" + key);
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(contentUri, new String[] {
                    ""
            }, "",
                    new String[] {}, "");
            if (cursor == null) {
                return value;
            }

            if (cursor.moveToNext()) {
                value = cursor.getInt(0) == 1;
            }
            cursor.close();
        } catch (Exception e) {

        }
        return value;
    }

    public static Bundle toBundle(JSONObject params) {
        return toBundle(null, params);
    }

    public static Bundle toBundle(Bundle bundle, JSONObject params) {
        if (bundle == null) {
            bundle = new Bundle();
        }

        if (params == null || params.isEmpty()) {
            return bundle;
        }

        for (String key : params.keySet()) {
            try {
                Object value = params.get(key);
                if (value instanceof Integer) {
                    bundle.putInt(key, (Integer) value);
                } else if (value instanceof Boolean) {
                    bundle.putBoolean(key, (Boolean) value);
                } else if (value instanceof String) {
                    bundle.putString(key, (String) value);
                } else if (value instanceof Long) {
                    bundle.putLong(key, (Long) value);
                } else if (value instanceof Double) {
                    bundle.putDouble(key, (Double) value);
                } else if (value instanceof Float) {
                    float f = (Float) value;
                    BigDecimal bd = new BigDecimal(Float.toString(f));
                    double dd = bd.doubleValue();
                    bundle.putDouble(key, dd);
                } else if (value instanceof JSON) {
                    String jsonStr = ((JSON) value).toJSONString();
                    bundle.putString(key, jsonStr);
                }
            } catch (Exception e) {
                H5Log.e(TAG, "toBundle exception", e);
            }
        }

        return bundle;
    }

    public static final JSONObject parseObject(String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }

        JSONObject jo = null;
        try {
            jo = JSON.parseObject(text);
        } catch (Exception e) {
            H5Log.e(TAG, e);
        }
        return jo;
    }

    public static JSONArray parseArray(String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }

        JSONArray ja = null;
        try {
            ja = JSON.parseArray(text);
        } catch (Exception e) {
            H5Log.e(TAG, e);
        }
        return ja;
    }

    public static boolean isDebugable() {
        try {
            ApplicationInfo info = H5Environment.getContext()
                    .getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {

        }
        return false;
    }

    public static void runOnMain(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        boolean isMain = Looper.getMainLooper() == Looper.myLooper();
        if (isMain) {
            runnable.run();
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(runnable);
        }
    }

    public static void runOnMain(Runnable runnable, long delay) {
        if (runnable == null) {
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, delay);
    }

    public static String getApplicaitonDir() {
        Context context = H5Environment.getContext();
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        String applicationDir = null;
        try {
            PackageInfo p = packageManager.getPackageInfo(packageName, 0);
            applicationDir = p.applicationInfo.dataDir;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return applicationDir;
    }

    public static int dip2px(float dipValue) {
        final float scale = H5Environment.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int dip2px(int dip) {
        int px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dip, H5Environment.getResources()
                        .getDisplayMetrics()));
        return px;
    }

    public static int px2dip(float pxValue) {
        final float scale = H5Environment.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2dip(int px) {
        DisplayMetrics displayMetrics = H5Environment.getResources()
                .getDisplayMetrics();
        int dp = Math.round(px
                / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int sp2pix(float sp) {
        float scale = H5Environment.getResources().getDisplayMetrics().scaledDensity;
        int px = Math.round(sp * scale);
        return px;
    }

    public static String getNetworkType() {
        Context context = H5Environment.getContext();
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        String type = "fail";
        NetworkInfo networkInfo = null;
        try {
            networkInfo = connMgr.getActiveNetworkInfo();
        } catch (Exception e) {

        }

        if (networkInfo != null) {
            int nType = networkInfo.getType();
            if (nType == ConnectivityManager.TYPE_WIFI
                    || nType == ConnectivityManager.TYPE_ETHERNET) {
                type = "wifi";
            } else {
                type = "wwan";
            }
        }
        return type;
    }

    public static int getUid(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_ACTIVITIES);
            return ai.uid;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String readRaw(int resId) {
        String text = null;
        InputStream ips = null;
        try {
            Resources resources = H5Environment.getResources();
            ips = resources.openRawResource(resId);

            ByteArrayBuffer buff = new ByteArrayBuffer(1024);
            byte tmp[] = new byte[1024];
            for (int index = ips.read(tmp); index != -1; index = ips.read(tmp)) {
                buff.append(tmp, 0, index);
            }
            text = new String(buff.toByteArray());
        } catch (Exception e) {
            H5Log.e(TAG, "read raw file exception.", e);
        } finally {
            try {
                ips.close();
            } catch (Exception e) {

            }
        }
        return text;
    }

    public static String getClassName(Object object) {
        if (object == null) {
            return null;
        }

        String clazz = object.getClass().getCanonicalName();
        if (clazz == null) {
            clazz = object.getClass().getName();
        }
        return clazz;
    }
}

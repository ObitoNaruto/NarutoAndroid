
package com.naruto.mobile.h5container.download;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InfoCache {
    public static final String TAG = "InfoCache";

    public static final long EXPIRE_TIME = 604800000; // 7*24*60*60*1000

    private Map<String, TaskInfo> info;

    public InfoCache(Context context) {
        info = new HashMap<String, TaskInfo>();
        try {
            SharedPreferences sp = context.getSharedPreferences(
                    "h5_download_info", Context.MODE_PRIVATE);
            Map<String, ?> map = sp.getAll();
            if (map == null || map.isEmpty()) {
                return;
            }
            Set<String> keys = map.keySet();
            for (String key : keys) {
                Object obj = map.get(key);
                if (!(obj instanceof String)) {
                    continue;
                }
                String text = (String) obj;
                TaskInfo t = new TaskInfo(text);
                info.put(key, t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        clearExpired(context);
    }

    public void save(Context context) {
        try {
            SharedPreferences sp = context.getSharedPreferences(
                    "h5_download_info", Context.MODE_PRIVATE);
            Editor editor = sp.edit();
            editor.clear().commit();
            if (!info.isEmpty()) {
                for (String key : info.keySet()) {
                    TaskInfo t = info.get(key);
                    editor.putString(key, t.toString());
                }
                editor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TaskInfo get(String key) {
        return info.get(key);
    }

    public void set(String key, TaskInfo t) {
        if (TextUtils.isEmpty(key) || t == null) {
            return;
        }
        info.put(key, t);
    }

    public boolean contains(String key) {
        return info.containsKey(key);
    }

    public TaskInfo remove(String key) {
        return info.remove(key);
    }

    public void clearExpired(Context context) {
        if (info == null || info.isEmpty()) {
            return;
        }

        long current = System.currentTimeMillis();
        try {
            boolean cleared = false;
            for (String key : info.keySet()) {
                TaskInfo t = info.get(key);
                long time = t.getTime();
                if ((current - time) > EXPIRE_TIME) {
                    info.remove(key);
                    cleared = true;
                }
            }

            if (cleared) {
                save(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.naruto.mobile.log.KLog.klog;

import android.util.Log;


import com.naruto.mobile.log.KLog.KLog;
import com.naruto.mobile.log.KLog.KLogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 */
public class JsonLog {

    public static void printJson(String tag, String msg, String headString) {

        String message;

        try {
            if (msg.startsWith("{") && msg.endsWith("}")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(KLog.JSON_INDENT);//返回格式化数据,各个级别间4个空格
            } else if (msg.startsWith("[") && msg.endsWith("]")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(KLog.JSON_INDENT);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        KLogUtil.printLine(tag, true);//打印顶部线条
        message = headString + KLog.LINE_SEPARATOR + message;
        String[] lines = message.split(KLog.LINE_SEPARATOR);//获取行数
        for (String line : lines) {
            Log.d(tag, "║ " + line);//打印竖线格式
        }
        KLogUtil.printLine(tag, false);//打印底部线条
    }
}

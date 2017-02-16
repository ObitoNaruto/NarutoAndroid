package com.naruto.mobile.log.KLog.klog;

import android.util.Log;

import com.naruto.mobile.log.KLog.KLog;


/**
 */
public class BaseLog {

    private static final int MAX_LENGTH = 4000;

    public static void printDefault(int type, String tag, String msg) {

        int index = 0;//本次打印日志的开始位置索引
        int length = msg.length();//日志字符串的长度
        int countOfSub = length / MAX_LENGTH;//需要打印的次数

        if (countOfSub > 0) {////本次日志字符串少于4000字符,遍历进行打印
            for (int i = 0; i < countOfSub; i++) {
                String sub = msg.substring(index, index + MAX_LENGTH);//本次遍历打印的日志子字符串
                printSub(type, tag, sub);
                index += MAX_LENGTH;//索引更新
            }
            printSub(type, tag, msg.substring(index, length));
        } else {
            printSub(type, tag, msg);//本次日志字符串少于4000字符,直接打印
        }
    }

    /**
     * 日志打印
     * @param type　打印日志类型级别
     * @param tag　TAG标签
     * @param sub 日志内容
     */
    private static void printSub(int type, String tag, String sub) {
        switch (type) {
            case KLog.V:
                Log.v(tag, sub);
                break;
            case KLog.D:
                Log.d(tag, sub);
                break;
            case KLog.I:
                Log.i(tag, sub);
                break;
            case KLog.W:
                Log.w(tag, sub);
                break;
            case KLog.E:
                Log.e(tag, sub);
                break;
            case KLog.A:
                Log.wtf(tag, sub);
                break;
        }
    }

}

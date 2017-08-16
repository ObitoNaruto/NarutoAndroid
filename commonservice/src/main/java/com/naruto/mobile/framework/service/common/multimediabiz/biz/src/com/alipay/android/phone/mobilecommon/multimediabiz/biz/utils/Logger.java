package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;

import android.text.TextUtils;
import android.util.Log;

import com.naruto.mobile.base.log.logging.LoggerFactory;

/**
 * 日志类
 * Created by jinmin on 15/5/25.
 */
public class Logger {

    public static final String COST_TIME_TAG = "CostTime";

    private static final boolean DEBUG = true;
    private static final boolean USE_SYSTEM_LOG = false;
    private static final boolean USE_ALIPAY_LOG = true;

    private static String format(String format, Object...args) {
        String str = format;
        if (args != null && args.length > 0) {
            try {
                str = String.format(format, args);
            } catch (Exception e) {
                //ignore
            }
        }
        str = "[" + Thread.currentThread().getName() + "] " + str;
        return str;
    }

    //static
    public static void V(String tag, String format, Object...args) {
        if (DEBUG) {
            String msg = format(format, args);
            if (USE_SYSTEM_LOG) Log.v(tag, msg);
//            if (USE_ALIPAY_LOG) LoggerFactory.getTraceLogger().verbose(tag, msg);
        }
    }

    public static void D(String tag, String format, Object...args) {
        if (DEBUG) {
            String msg = format(format, args);
            if (USE_SYSTEM_LOG) Log.d(tag, msg);
//            if (USE_ALIPAY_LOG) LoggerFactory.getTraceLogger().debug(tag, msg);
        }
    }

    public static void I(String tag, String format, Object...args) {
        if (DEBUG) {
            String msg = format(format, args);
            if (USE_SYSTEM_LOG) Log.i(tag, msg);
//            if (USE_ALIPAY_LOG) LoggerFactory.getTraceLogger().info(tag, msg);
        }
    }

    public static void W(String tag, String format, Object...args) {
        if (DEBUG) {
            String msg = format(format, args);
            if (USE_SYSTEM_LOG) Log.w(tag, msg);
//            if (USE_ALIPAY_LOG) LoggerFactory.getTraceLogger().warn(tag, msg);
        }
    }

    public static void P(String tag, String format, Object...args) {
        if (DEBUG) {
            String msg = format(format, args);
            if (USE_SYSTEM_LOG) Log.w(tag, msg);
//            if (USE_ALIPAY_LOG) LoggerFactory.getTraceLogger().print(tag, msg);
        }
    }

    public static void E(String tag, Throwable e, String format, Object...args) {
        if (DEBUG) {
            String msg = format(format, args);
            if (USE_SYSTEM_LOG) Log.e(tag, msg, e);
            if (USE_ALIPAY_LOG) {
                if (e == null) {
//                    LoggerFactory.getTraceLogger().error(tag, msg);
                } else {
//                    LoggerFactory.getTraceLogger().error(tag, msg, e);
                }

            }
        }
    }

    public static void E(String tag, String format, Object...args) {
        E(tag, null, format, args);
    }

    public static void TIME(String format, Object...args) {
        D(Logger.COST_TIME_TAG, format, args);
    }

    //instance
    private String mTag = null;

    public static Logger getLogger(Class clazz) {
        return getLogger(clazz.getSimpleName());
    }

    public static Logger getLogger(String tag) {
        Logger logger = new Logger();
        logger.mTag = tag;
        return logger;
    }

    public void v(String format, Object...args) {
        V(mTag, format, args);
    }

    public void d(String format, Object...args) {
        D(mTag, format, args);
    }

    public void i(String format, Object...args) {
        I(mTag, format, args);
    }

    public void w(String format, Object...args) {
        W(mTag, format, args);
    }

    //这个级别的日志只是在debugable包打印到控制台，不会保存文件
    public void p(String format, Object... args) {
        P(mTag, format, args);
    }

    public void e(Throwable e, String format, Object...args) {
        E(mTag, e, format, args);
    }

    public void e(String format, Object...args) {
        E(mTag, null, format, args);
    }

    public static class TimeCost {
        private long begin;
        private String beginMsg;
        private String className;

        private TimeCost() {

        }

        public static TimeCost begin(Class clazz, String msg) {
            TimeCost cost = new TimeCost();
            cost.begin = System.currentTimeMillis();
            cost.className = clazz.getSimpleName();
            cost.beginMsg = cost.className + ", " + msg;
            Logger.TIME(msg + ", start: " + cost.begin);
            return cost;
        }

        public void end(String msg) {
            if (TextUtils.isEmpty(msg)) {
                msg = beginMsg;
            }
            Logger.TIME(msg + ", cost: " + (System.currentTimeMillis()-begin));
            begin = System.currentTimeMillis();
        }

        public void end() {
            end(null);
        }
    }
}

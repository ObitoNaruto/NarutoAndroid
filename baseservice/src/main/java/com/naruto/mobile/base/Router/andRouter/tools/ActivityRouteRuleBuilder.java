package com.naruto.mobile.base.Router.andRouter.tools;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.naruto.mobile.base.Router.andRouter.utils.UrlUtils;

import timber.log.Timber;

/**
 * 该类用来创建路由规则，当然你也可以自己手动创建
 */
public class ActivityRouteRuleBuilder extends BaseRouteRuleBuilder {
    private static final String TAG = "ActivityRouteUrlBuilder";

    List<String> mKeys = new ArrayList<>();


    @Override
    public ActivityRouteRuleBuilder setHost(String host) {
        super.setHost(host);
        return this;
    }

    @Override
    public ActivityRouteRuleBuilder setScheme(String scheme) {
        super.setScheme(scheme);
        return this;
    }

    @Override
    public ActivityRouteRuleBuilder addPathSegment(String seg) {
        super.addPathSegment(seg);
        return this;
    }

    @Override
    public ActivityRouteRuleBuilder addQueryParameter(String key, String value) {
        super.addQueryParameter(key, value);
        return this;
    }

    @Override
    public ActivityRouteRuleBuilder setPath(String path) {
        super.setPath(path);
        return this;
    }

    /**
     * 在path中添加值的定义，包括，数据类型
     * @param key
     * @param type
     * @return
     */
    public ActivityRouteRuleBuilder addKeyValueDefine(String key, Class<?> type) {
        String typeChar = "";
        if(type.equals(Integer.class)){
            //整形
            typeChar = "i";
        } else if(type.equals(Float.class)){
            typeChar = "f";
        } else if(type.equals(Long.class)){
            typeChar = "l";
        } else if(type.equals(Double.class)){
            typeChar = "d";
        } else if(type.equals(String.class) || type.equals(CharSequence.class)){
            typeChar = "s";
        } else {
            typeChar = "s";
        }
        String keyFormat = String.format(":%s{%s}", typeChar, key);
        if(mKeys.contains(keyFormat)){
            Log.e(TAG, "", new KeyDuplicateException(keyFormat));
        } else {
            addPathSegment(keyFormat);
            mKeys.add(keyFormat);
        }
        return this;
    }


    /**
     * @deprecated 不是非常可靠，在url格式不正确的时候getPathSegments返回一个空的列表，然后就返回true了
     * @param url
     * @return
     */
    @Deprecated
    public static boolean isActivityRuleValid(String url) {
        String pattern = ":[iflds]?\\{[a-zA-Z0-9]+\\}"; //key 支持大小写字母及数字
        Pattern p = Pattern.compile(pattern);
        List<String> pathSegs = UrlUtils.getPathSegments(url);
        List<String> checkedSegs = new ArrayList<>();
        for(String seg : pathSegs){
            if(seg.startsWith(":")){
                Matcher matcher = p.matcher(seg);
                if(!matcher.matches()){
                    Timber.w("The key format not match : %s" , seg);
                    return false;
                }
                if(checkedSegs.contains(seg)){
                    Timber.w("The key is duplicated : %s" , seg);
                    return false;
                }
                checkedSegs.add(seg);

            }
        }
        return true;
    }

    public static class KeyDuplicateException extends Exception{
        public KeyDuplicateException(String key){
            super("The key is duplicated: "+ key);
        }
    }
}

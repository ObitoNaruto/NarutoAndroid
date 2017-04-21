package com.naruto.mobile.base.Router.andRouter.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.naruto.mobile.base.Router.andRouter.utils.UrlUtils;

import timber.log.Timber;

/**
 * 根据路径规则和参数生成路由路径，将会替换路由中的参数定义，替换为具体的值
 */
public class ActivityRouteUrlBuilder {
    private static final String TAG = "RoutePathBuilder";
    //最后返回的url
    String mPath;
    String mMatchPath;

    /**
     * @param matchPath 匹配的路由
     */
    public ActivityRouteUrlBuilder(String matchPath){
        mPath = matchPath;
        mMatchPath = matchPath;
    }



    public ActivityRouteUrlBuilder withKeyValue(String key, int value){
        mPath = mPath.replace(String.format(":i{%s}", key), Integer.toString(value));
        return this;
    }

    public ActivityRouteUrlBuilder withKeyValue(String key, float value){
        mPath = mPath.replace(String.format(":f{%s}", key), Float.toString(value));
        return this;
    }

    public ActivityRouteUrlBuilder withKeyValue(String key, long value){
        mPath = mPath.replace(String.format(":l{%s}", key), Long.toString(value));
        return this;
    }

    public ActivityRouteUrlBuilder withKeyValue(String key, double value){
        mPath = mPath.replace(String.format(":d{%s}", key), Double.toString(value));
        return this;
    }



    public ActivityRouteUrlBuilder withKeyValue(String key, String value){
        mPath = mPath.replace(String.format(":{%s}", key), value);
        mPath = mPath.replace(String.format(":s{%s}", key), value);
        return this;
    }

    public ActivityRouteUrlBuilder withKeyValue(String key, char value){
        mPath = mPath.replace(String.format(":c{%s}", key), Character.toString(value));
        return this;
    }

    public ActivityRouteUrlBuilder withQueryParameter(String key, String value){
        mPath = UrlUtils.addQueryParameters(mPath, key, value);
        return this;
    }

    public String build(){
        Matcher matcher = Pattern.compile(":[i, f, l, d, s, c]?\\{[a-zA-Z0-9]+?\\}").matcher(mPath);
        if(matcher.find()){
            Timber.w("Not all the key settled");
        }
        return mPath;
    }

}

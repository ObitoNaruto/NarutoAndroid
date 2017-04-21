package com.naruto.mobile.base.Router.andRouter.tools;

import android.net.Uri;

/**
 */
public abstract class BaseRouteRuleBuilder implements IRouteRuleBuilder {

    Uri.Builder builder = new Uri.Builder();

    @Override
    public IRouteRuleBuilder setScheme(String scheme) {
        builder.scheme(scheme);
        return this;
    }

    @Override
    public IRouteRuleBuilder setHost(String host) {
        builder.authority(host);
        return this;
    }


    @Override
    public IRouteRuleBuilder setPath(String path) {
        builder.path(path);
        return this;
    }


    @Override
    public IRouteRuleBuilder addPathSegment(String seg) {
        builder.appendPath(seg);
        return this;
    }


    @Override
    public IRouteRuleBuilder addQueryParameter(String key, String value) {
        builder.appendQueryParameter(key, value);
        return this;
    }


    public String build(){
        return builder.build().toString();
    }
}

package com.naruto.mobile.base.Router.andRouter.tools;

/**
 * 路由url创建者接口，用户可以根据route规则手写，但是出错几率比较大，所以路由编写者需要提供该类给用户创建路由url
 */
public interface IRouteRuleBuilder {

    IRouteRuleBuilder setScheme(String scheme);
    IRouteRuleBuilder setHost(String host);
    IRouteRuleBuilder setPath(String path);
    IRouteRuleBuilder addPathSegment(String seg);
    IRouteRuleBuilder addQueryParameter(String key, String value);
}

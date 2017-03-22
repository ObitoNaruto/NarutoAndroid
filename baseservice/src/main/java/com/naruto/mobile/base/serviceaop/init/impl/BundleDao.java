package com.naruto.mobile.base.serviceaop.init.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public class BundleDao {
    public List<Bundle> getBundles() {
        List<Bundle> list = new ArrayList<Bundle>();
        //eg:注册了当前module，以后增加了依赖的话，都统一在这里配置
        Bundle bundle = new Bundle("demo", false, "com.naruto.mobile.base.serviceaop.demo");
        list.add(bundle);
        Bundle commonserviceBundle = new Bundle("commonservice", false, "com.naruto.mobile.framework");
        list.add(commonserviceBundle);

        return list;
    }
}

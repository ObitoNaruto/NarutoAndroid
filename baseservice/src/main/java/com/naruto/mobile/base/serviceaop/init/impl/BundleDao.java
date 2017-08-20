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

        Bundle h5ServiceBundle = new Bundle("h5ServiceDemo", false, "com.naruto.mobile.h5container");
        list.add(h5ServiceBundle);


        Bundle funccomponentBundle = new Bundle("funccomponent", false, "com.naruto.mobile");

        Bundle launcherBundle = new Bundle("launcher", true, "com.naruto.mobile.applauncher");
        list.add(launcherBundle);

        Bundle app2DemoBundle = new Bundle("app2Demo", false, "com.naruto.mobile.app2demo");
        list.add(app2DemoBundle);

        return list;
    }
}

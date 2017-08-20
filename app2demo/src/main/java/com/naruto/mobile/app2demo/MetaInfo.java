package com.naruto.mobile.app2demo;

import com.naruto.mobile.base.serviceaop.app.ApplicationDescription;
import com.naruto.mobile.base.serviceaop.service.BaseMetaInfo;

/**
 */

public class MetaInfo extends BaseMetaInfo {

    public static final String appId = "20000002";

    public MetaInfo() {
        super();

        ApplicationDescription applicationDescription = new ApplicationDescription();
        applicationDescription.setName("app2Demo");
        applicationDescription.setClassName(App2DemoActivityApplication.class.getName());
        applicationDescription.setAppId(appId);
        setEntry("app2Demo");
        addApplication(applicationDescription);
    }
}

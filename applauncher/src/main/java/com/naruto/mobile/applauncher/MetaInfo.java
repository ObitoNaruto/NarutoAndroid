package com.naruto.mobile.applauncher;

import com.naruto.mobile.base.serviceaop.app.ApplicationDescription;
import com.naruto.mobile.base.serviceaop.service.BaseMetaInfo;

/**
 */

public class MetaInfo extends BaseMetaInfo {

    public final static String appId = "20000001";

    public MetaInfo(){
        super();

        ApplicationDescription applicationDescription = new ApplicationDescription();
        applicationDescription.setName("launcher");
        applicationDescription.setClassName(LauncherActivityApplication.class.getName());
        applicationDescription.setAppId(appId);
        setEntry("launcher");
        addApplication(applicationDescription);
    }
}

package com.naruto.mobile;

import com.naruto.mobile.base.serviceaop.app.ApplicationDescription;
import com.naruto.mobile.base.serviceaop.service.BaseMetaInfo;

/**
 */

public  class MetaInfo extends BaseMetaInfo {
    public final static String appId = "200000003";

    public MetaInfo(){
        super();

        ApplicationDescription appDes = new ApplicationDescription();
        appDes.setName("funccomponent");
        appDes.setClassName(FuncComponentApplication.class.getName());
        appDes.setAppId(appId);
        setEntry("funccomponent");
        addApplication(appDes);

    }
}

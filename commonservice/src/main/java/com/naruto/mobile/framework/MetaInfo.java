package com.naruto.mobile.framework;

import com.naruto.mobile.base.serviceaop.service.BaseMetaInfo;
import com.naruto.mobile.base.serviceaop.service.ServiceDescription;
import com.naruto.mobile.framework.biz.ext.ShortCutService;
import com.naruto.mobile.framework.biz.ext.impl.ShortCutServiceImpl;

public class MetaInfo extends BaseMetaInfo{

    public MetaInfo(){
        ServiceDescription shortcutServiceDescription = new ServiceDescription();
        shortcutServiceDescription.setName("ShortcutService");
        shortcutServiceDescription.setClassName(ShortCutServiceImpl.class.getName());
        shortcutServiceDescription.setInterfaceClass(ShortCutService.class.getName());
        shortcutServiceDescription.setLazy(true);
        addService(shortcutServiceDescription);
    }

}

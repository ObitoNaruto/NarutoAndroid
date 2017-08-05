package com.naruto.mobile.framework;

import com.naruto.mobile.base.serviceaop.service.BaseMetaInfo;
import com.naruto.mobile.base.serviceaop.service.ServiceDescription;
import com.naruto.mobile.framework.biz.ext.photo_demo.service.PhotoService;
import com.naruto.mobile.framework.biz.ext.photo_demo.service.impl.PhotoServiceImpl;
import com.naruto.mobile.framework.biz.ext.shortCut.ShortCutService;
import com.naruto.mobile.framework.biz.ext.shortCut.impl.ShortCutServiceImpl;
import com.naruto.mobile.framework.service.common.multimedia.api.MultimediaImageService;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.service.MultimediaImageServiceImpl;

public class MetaInfo extends BaseMetaInfo{

    public MetaInfo(){
        ServiceDescription shortcutServiceDescription = new ServiceDescription();
        shortcutServiceDescription.setName("ShortcutService");
        shortcutServiceDescription.setClassName(ShortCutServiceImpl.class.getName());
        shortcutServiceDescription.setInterfaceClass(ShortCutService.class.getName());
        shortcutServiceDescription.setLazy(true);
        addService(shortcutServiceDescription);

        //测试Activity A 跳转到业务独立的Activity B上，并在B上进行交互操作后，将数据返回给A
        ServiceDescription photoServiceServiceDescription = new ServiceDescription();
        photoServiceServiceDescription.setName("photoServiceService");
        photoServiceServiceDescription.setClassName(PhotoServiceImpl.class.getName());
        photoServiceServiceDescription.setInterfaceClass(PhotoService.class.getName());
        photoServiceServiceDescription.setLazy(true);
        addService(photoServiceServiceDescription);

        ServiceDescription multimiaServiceDescription = new ServiceDescription();
        shortcutServiceDescription.setName("multimiaService");
        shortcutServiceDescription.setClassName(MultimediaImageServiceImpl.class.getName());
        shortcutServiceDescription.setInterfaceClass(MultimediaImageService.class.getName());
        shortcutServiceDescription.setLazy(true);
        addService(multimiaServiceDescription);
    }

}

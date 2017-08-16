package com.naruto.mobile.framework.service.common.multimediabiz.ui.src.com.alipay.android.phone.mobilecommon.multimediabiz;

import android.util.Log;

import com.naruto.mobile.base.serviceaop.service.BaseMetaInfo;
import com.naruto.mobile.base.serviceaop.service.ServiceDescription;
import com.naruto.mobile.framework.service.common.multimedia.api.MultimediaAudioService;
import com.naruto.mobile.framework.service.common.multimedia.api.MultimediaFileService;
import com.naruto.mobile.framework.service.common.multimedia.api.MultimediaImageService;
import com.naruto.mobile.framework.service.common.multimedia.api.MultimediaVideoService;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.service.AudioServiceImpl;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.service.FileServiceImpl;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.service.MultimediaImageServiceImpl;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.service.MultimediaVideoServiceImpl;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
//import com.alipay.mobile.framework.BaseMetaInfo;
//import com.alipay.mobile.framework.service.ServiceDescription;

public class MetaInfo extends BaseMetaInfo {

    public MetaInfo() {
        Logger.D("MetaInfo", "MetaInfo init " + MetaInfo.class.getName());

        ServiceDescription imageService = new ServiceDescription();
        imageService.setInterfaceClass(MultimediaImageService.class.getName());
        imageService.setClassName(MultimediaImageServiceImpl.class.getName());
        addService(imageService);

        ServiceDescription fileService = new ServiceDescription();
        fileService.setInterfaceClass(MultimediaFileService.class.getName());
        fileService.setClassName(FileServiceImpl.class.getName());
        addService(fileService);

        ServiceDescription audioService = new ServiceDescription();
        audioService.setInterfaceClass(MultimediaAudioService.class.getName());
        audioService.setClassName(AudioServiceImpl.class.getName());
        addService(audioService);

		ServiceDescription videoService = new ServiceDescription();
		videoService.setInterfaceClass(MultimediaVideoService.class.getName());
		videoService.setClassName(MultimediaVideoServiceImpl.class.getName());
		addService(videoService);
    }
}


package com.naruto.mobile.framework.biz.ext.photo_demo.service;

import android.os.Bundle;

import com.naruto.mobile.base.serviceaop.service.ext.ExternalService;

public abstract class PhotoService extends ExternalService {

    /**
     * @param bundle 选择照片的参数集
     */
    public abstract void selectPhoto(Bundle bundle, PhotoSelectListener listener);
}

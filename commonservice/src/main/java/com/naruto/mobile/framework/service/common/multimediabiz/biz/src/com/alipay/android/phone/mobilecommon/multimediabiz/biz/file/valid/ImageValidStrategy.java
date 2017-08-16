package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file.valid;


import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.ImageUtils;

public class ImageValidStrategy implements FileValidStrategy {

    @Override
    public boolean checkFileValid(String filePath) {
        return ImageUtils.isImage(filePath);
    }
}

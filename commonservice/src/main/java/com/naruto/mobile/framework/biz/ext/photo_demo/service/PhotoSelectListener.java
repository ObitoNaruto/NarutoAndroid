package com.naruto.mobile.framework.biz.ext.photo_demo.service;


import android.os.Bundle;

import java.util.List;

/**
 */
public interface PhotoSelectListener {

    /**
     * callback for return selected image path
     * @param imageList
     * @param bundle
     */
    void onPhotoSelected(List<PhotoInfo> imageList, Bundle bundle);

    /**
     * callback for user canceled operation
     */
    void onSelectCanceled();

}


package com.naruto.mobile.framework.biz.ext.photo_demo.data;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.naruto.mobile.framework.biz.ext.photo_demo.service.PhotoInfo;
import com.naruto.mobile.framework.biz.ext.photo_demo.service.PhotoParam;
import com.naruto.mobile.framework.biz.ext.photo_demo.service.PhotoSelectListener;

/**
 * Photo service context
 */
public class PhotoContext {

    public static final String TAG = "PhotoContext";

    public static Map<String, PhotoContext> contextMap = new HashMap<String, PhotoContext>();

    public static void remove(String index) {
        // remove current data
        if (contextMap.containsKey(index)) {
            contextMap.remove(index);
        }
    }

    public static PhotoContext get(String index) {
        PhotoContext context;
        if (contextMap.containsKey(index)) {
            context = contextMap.get(index);
        } else {
            context = new PhotoContext(index);
            contextMap.put(index, context);
        }
//        LoggerFactory.getTraceLogger().debug(TAG, "context map size " + contextMap.size());
        return context;
    }

    /**
     * all photo list for current photo service
     */
    public List<PhotoItem> photoList;

    /**
     * photo selected list
     */
    public List<PhotoItem> selectedList;

    /**
     * select image callback listener
     */
    public PhotoSelectListener selectListener;

    /**
     * current photo service context
     */
    public String contextIndex;


    private PhotoContext(String contextIndex) {
        this.photoList = new ArrayList<PhotoItem>();
        this.selectedList = new ArrayList<PhotoItem>();
        this.contextIndex = contextIndex;
    }


    public void sendSelectedPhoto() {
        if (selectListener == null) {
            Log.w(TAG, "selectListener is null");
            return;
        }

        if (selectedList.isEmpty()) {
//            LoggerFactory.getTraceLogger().debug(TAG, "no photo selected!");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(PhotoParam.USE_ORIGIN_PHOTO, true);
        List<PhotoInfo> infoList = new ArrayList<PhotoInfo>();
        for (PhotoItem photoInfo : selectedList) {
            infoList.add(photoInfo);
        }
//        LoggerFactory.getTraceLogger().debug(TAG, "sendSelectedPhoto size " + infoList.size());
        selectListener.onPhotoSelected(infoList, bundle);
    }

}

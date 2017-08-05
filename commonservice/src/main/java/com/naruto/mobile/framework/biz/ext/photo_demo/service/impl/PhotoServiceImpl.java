package com.naruto.mobile.framework.biz.ext.photo_demo.service.impl;

import android.content.Intent;
import android.os.Bundle;

import java.util.concurrent.atomic.AtomicInteger;

import com.naruto.mobile.framework.biz.ext.photo_demo.data.PhotoContext;
import com.naruto.mobile.framework.biz.ext.photo_demo.service.PhotoParam;
import com.naruto.mobile.framework.biz.ext.photo_demo.service.PhotoSelectListener;
import com.naruto.mobile.framework.biz.ext.photo_demo.service.PhotoService;
import com.naruto.mobile.framework.biz.ext.photo_demo.ui.PhotoSelectActivity;

public class PhotoServiceImpl extends PhotoService{

    private AtomicInteger mAtomicIndex;


    @Override
    protected void onCreate(Bundle params) {
        mAtomicIndex = new AtomicInteger(0);
    }

    @Override
    protected void onDestroy(Bundle params) {
    }

    @Override
    public void selectPhoto(Bundle bundle, PhotoSelectListener listener) {
        if(listener == null){
            return;
        }

        if(bundle == null){
            bundle = new Bundle();
        }

        String contextKey = generateContextKey();
        bundle.putString(PhotoParam.CONTEXT_INDEX, contextKey);
        PhotoContext photoContext = PhotoContext.get(contextKey);
        photoContext.photoList = null;
        photoContext.selectListener = listener;

        Intent intent = new Intent(getNarutoApplicationContext().getApplicationContext(), PhotoSelectActivity.class);
        intent.putExtras(bundle);
        getNarutoApplicationContext().getTopActivity().get().startActivity(intent);
    }

    private String generateContextKey(){
        int index = mAtomicIndex.getAndIncrement();
        return "photo" + index;
    }
}

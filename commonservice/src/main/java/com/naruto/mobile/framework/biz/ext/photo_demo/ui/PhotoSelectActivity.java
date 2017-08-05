
package com.naruto.mobile.framework.biz.ext.photo_demo.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.naruto.mobile.framework.biz.ext.photo_demo.data.PhotoContext;
import com.naruto.mobile.framework.biz.ext.photo_demo.data.PhotoItem;
import com.naruto.mobile.framework.biz.ext.photo_demo.service.PhotoParam;

public class PhotoSelectActivity extends Activity  {

    public static final String TAG = "PhotoSelectActivity";

    private String contextIndex;

    private PhotoContext photoContext;

    private List<PhotoItem> photoList;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (bundle == null) {
            bundle = getIntent().getExtras();
            if (bundle == null) {
                finish();
                return;
            }
        } else {
            Log.d(TAG, "initialize photo select with save instance.");
        }

        TextView tv = new TextView(this);
        tv.setText("1243654768797899gjhgjdfdhgfdhfjhdfghdhgfhgfhgf");
        setContentView(tv);
        contextIndex = bundle.getString(PhotoParam.CONTEXT_INDEX);
        Log.d(TAG, "PhotoSelect context index " + contextIndex);

        photoContext = PhotoContext.get(contextIndex);
        PhotoContext.remove(contextIndex);
        photoList = photoContext.photoList;


        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                photoContext.selectedList = mockSelectdList();
                onSelected();
            }
        });


    }

    private List<PhotoItem> mockSelectdList(){
        List<PhotoItem> photoItemList = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            PhotoItem photoItem = new PhotoItem();
            photoItem.setSelectable(false);
            photoItem.setPhotoPath("file://sdfsjlgfdjlhg" + i);
            photoItem.setPhotoWidth(100 + i);
            photoItem.setPhotoHeight(200 + i);
            photoItemList.add(photoItem);
        }
        Log.e(TAG, "mockSelectdList result:" + photoItemList);
        return photoItemList;
    }

    private void onSelected() {
        photoContext.sendSelectedPhoto();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (photoContext != null) {
            this.photoContext.contextMap.clear();
        }
        photoContext = null;
    }

}

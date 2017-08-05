package com.naruto.mobile.photo_demo_test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import com.naruto.mobile.R;
import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.framework.biz.ext.photo_demo.service.PhotoInfo;
import com.naruto.mobile.framework.biz.ext.photo_demo.service.PhotoParam;
import com.naruto.mobile.framework.biz.ext.photo_demo.service.PhotoSelectListener;
import com.naruto.mobile.framework.biz.ext.photo_demo.service.PhotoService;

public class PhotoServiceDemoActivity extends AppCompatActivity {

    private static final String TAG = "PhotoServiceDemoActivity";

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_service_demo);
        mButton = (Button) findViewById(R.id.btn_photo_test);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoService photoService = NarutoApplication.getInstance().getNarutoApplicationContext()
                        .getExtServiceByInterface(PhotoService.class.getName());
                Bundle bundle = new Bundle();
                bundle.putBoolean(PhotoParam.USE_ORIGIN_PHOTO, true);
                photoService.selectPhoto(bundle, new PhotoSelectListener() {
                    @Override
                    public void onPhotoSelected(List<PhotoInfo> imageList, Bundle bundle) {
                        Log.d(TAG, "onPhotoSelected imageList:" + (imageList == null ? "null" : imageList.toString()));
                        Log.d(TAG, "onPhotoSelected bundle:" + (bundle == null ? "null" : bundle.getBoolean(PhotoParam.USE_ORIGIN_PHOTO)));
                        Toast.makeText(PhotoServiceDemoActivity.this, imageList.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSelectCanceled() {

                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

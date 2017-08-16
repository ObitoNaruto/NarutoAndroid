package com.naruto.mobile.multimedia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.naruto.mobile.R;
import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.framework.service.common.multimedia.api.MultimediaImageService;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageLoadRequest;

public class MultimediaDemoActivity extends AppCompatActivity {

    private ImageView mIvTest;

    private static final String url = "http://i.imgur.com/7spzG.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multimedia_demo);
        mIvTest = (ImageView) findViewById(R.id.iv_test);
        loadImageRequest();
    }

    private void loadImageRequest() {
        APImageLoadRequest request = new APImageLoadRequest();
        request.imageView = mIvTest;
        request.path = url;
        request.width = 120;
        request.height = 120;
        request.defaultDrawable = getDrawable(R.drawable.flow_warning_simple);
        MultimediaImageService multimediaImageService = NarutoApplication.getInstance().getNarutoApplicationContext()
                .getExtServiceByInterface(MultimediaImageService.class.getName());
        multimediaImageService.loadImage(request);

    }
}

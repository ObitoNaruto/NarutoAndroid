package com.naruto.mobile.h5container;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.h5container.HomeActivity;
import com.naruto.mobile.h5container.api.H5Param;
import com.naruto.mobile.h5container.service.H5Service;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void click(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void clickH5service(View view){
        H5Service h5Service = NarutoApplication.getInstance().getNarutoApplicationContext()
                .getExtServiceByInterface(H5Service.class.getName());
        if(h5Service != null){
            Bundle bundle = new Bundle();
            bundle.putInt(H5Param.BACKGROUND_COLOR, Color.parseColor("#ffffffff"));
            bundle.putInt(H5Param.LONG_BACKGROUND_COLOR, Color.parseColor("#ffffffff"));
            bundle.putString(H5Param.URL, "https://www.baidu.com");
            h5Service.startPage(bundle);
        }
    }
}

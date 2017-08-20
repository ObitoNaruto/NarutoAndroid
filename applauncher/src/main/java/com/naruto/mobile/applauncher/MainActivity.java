package com.naruto.mobile.applauncher;

import android.os.Bundle;
import android.view.View;

import com.naruto.mobile.applauncher.utils.NarutoUtils;
import com.naruto.mobile.base.serviceaop.app.ui.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.tv_startApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("key", "test");
                NarutoUtils.startApp(MetaInfo.appId, "20000002", bundle);
            }
        });
    }
}

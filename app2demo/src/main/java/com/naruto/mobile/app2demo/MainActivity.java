package com.naruto.mobile.app2demo;

import android.content.Intent;
import android.os.Bundle;

import com.naruto.mobile.base.serviceaop.app.ui.BaseActivity;

public class MainActivity extends BaseActivity {

    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app2_demo_activity_main);

        parseIntent();
//
//        findViewById(R.id.tv_go_on).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NarutoApplicationContext context = NarutoApplication.getInstance().getNarutoApplicationContext();
//                context.startApp(MetaInfo.appId, "200000003", mBundle);
//            }
//        });
    }

    private void parseIntent(){
        Intent intent = getIntent();
        if(intent == null) {
            return;
        }
        mBundle = intent.getExtras();

        if(mBundle == null) {
            mBundle = new Bundle();
        }
    }
}

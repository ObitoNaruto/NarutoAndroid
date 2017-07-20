package com.naruto.mobile.rpc.volley;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.naruto.mobile.R;

public class VolleyDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley_demo);
    }


    public void onClickSendSimpleRequest(View view){
        Intent intent = new Intent(this, SendSimpleRequestAcvity.class);
        startActivity(intent);
    }

    public void onClickSettingUpRequestQueue(View view){
        Intent intent = new Intent(this, SettingUpRequestQueueActivity.class);
        startActivity(intent);
    }

    public void onClickMakingStandardRequest(View view){
        Intent intent = new Intent(this, MarkingStandardRequestActivity.class);
        startActivity(intent);
    }
}

package com.naruto.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.naruto.mobile.CountDownTimer.CountDownTimerTestActivity;
import com.naruto.mobile.LogReport.CrashHandler.CrashHandlerTestActivity;
import com.naruto.mobile.LogReport.PowerLogReport.LogReportTestActivity;
import com.naruto.mobile.RainbowBridge.RainbowBrideAcitivity;
import com.naruto.mobile.binderPool.BinderPoolActivity;
import com.naruto.mobile.log.KLog.test.KLogTestActivity;
import com.naruto.mobile.serviceaoptestdemo.ServiceAopActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickKLog(View view){
        Intent intent = new Intent(this, KLogTestActivity.class);
        startActivity(intent);
    }

    public void onClickCarshHandler(View view){
        Intent intent = new Intent(this, CrashHandlerTestActivity.class);
        startActivity(intent);
    }

    public void onClickLogReportTest(View view){
        Intent intent = new Intent(this, LogReportTestActivity.class);
        startActivity(intent);
    }

    public void onClickBinderPoolTest(View view){
        Intent intent = new Intent(this, BinderPoolActivity.class);
        startActivity(intent);
    }

    public void onClickCountDownTimerTest(View view){
        Intent intent = new Intent(this, CountDownTimerTestActivity.class);
        startActivity(intent);
    }

    public void onClickServiceAopTest(View view){
        Intent intent = new Intent(this, ServiceAopActivity.class);
        startActivity(intent);
    }

    public void onClickRainbowBridgeTest(View view){
        Intent intent = new Intent(this, RainbowBrideAcitivity.class);
        startActivity(intent);
    }

}

package com.naruto.mobile.log.KLog.test;

import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import com.naruto.mobile.R;
import com.naruto.mobile.log.KLog.KLog;

public class KLogTestActivity extends AppCompatActivity {

    private static final String LOG_MSG = "KLog is a so cool Log Tool!";
    private static final String TAG = "KLog";
    private static final String URL_XML = "https://raw.githubusercontent.com/ZhaoKaiQiang/KLog/master/app/src/main/AndroidManifest.xml";
    private static String XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><!--  Copyright w3school.com.cn --><note><to>George</to><from>John</from><heading>Reminder</heading><body>Don't forget the meeting!</body></note>";
    private static String JSON;
    private static String JSON_LONG;
    private static String STRING_LONG;
    private AsyncHttpClient httpClient;

    private static Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            KLog.d("Inner Class Test");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_klog_test);

        initView();
        initData();
        handler.sendEmptyMessageDelayed(0, 3000);
    }

    private void initView() {
    }

    private void initData() {
        httpClient = new AsyncHttpClient();
        JSON_LONG = getResources().getString(R.string.json_long);
        JSON = getResources().getString(R.string.json);
        STRING_LONG = getString(R.string.string_long);
    }

    public void logTraceStack(View view) {
        TestTraceUtil.testTrace();
    }

    public void logDebug(View view) {
        KLog.debug();
        KLog.debug("This is a debug message");
        KLog.debug("DEBUG", "params1", "params2", this);
    }

    public void log(View view) {
        KLog.v();
        KLog.d();
        KLog.i();
        KLog.w();
        KLog.e();
        KLog.a();
    }

    public void logWithNull(View view) {
        KLog.v(null);
        KLog.d(null);
        KLog.i(null);
        KLog.w(null);
        KLog.e(null);
        KLog.a(null);
    }

    public void logWithMsg(View view) {
        KLog.v(LOG_MSG);
        KLog.d(LOG_MSG);
        KLog.i(LOG_MSG);
        KLog.w(LOG_MSG);
        KLog.e(LOG_MSG);
        KLog.a(LOG_MSG);
    }

    public void logWithTag(View view) {
        KLog.v(TAG, LOG_MSG);
        KLog.d(TAG, LOG_MSG);
        KLog.i(TAG, LOG_MSG);
        KLog.w(TAG, LOG_MSG);
        KLog.e(TAG, LOG_MSG);
        KLog.a(TAG, LOG_MSG);
    }

    public void logWithLong(View view) {
        KLog.d(TAG, STRING_LONG);
    }

    public void logWithParams(View view) {
        KLog.v(TAG, LOG_MSG, "params1", "params2", this);
        KLog.d(TAG, LOG_MSG, "params1", "params2", this);
        KLog.i(TAG, LOG_MSG, "params1", "params2", this);
        KLog.w(TAG, LOG_MSG, "params1", "params2", this);
        KLog.e(TAG, LOG_MSG, "params1", "params2", this);
        KLog.a(TAG, LOG_MSG, "params1", "params2", this);
    }


    public void logWithJson(View view) {
        KLog.json("12345");
        KLog.json(null);
        KLog.json(JSON);
    }

    public void logWithLongJson(View view) {
        KLog.json(JSON_LONG);
    }

    public void logWithJsonTag(View view) {
        KLog.json(TAG, JSON);
    }

    public void logWithFile(View view) {
        KLog.file(Environment.getExternalStorageDirectory(), JSON_LONG);
        KLog.file(TAG, Environment.getExternalStorageDirectory(), JSON_LONG);
        KLog.file(TAG, Environment.getExternalStorageDirectory(), "test.txt", JSON_LONG);
    }

    public void logWithXml(View view) {
        KLog.xml("12345");
        KLog.xml(null);
        KLog.xml(XML);
    }

}

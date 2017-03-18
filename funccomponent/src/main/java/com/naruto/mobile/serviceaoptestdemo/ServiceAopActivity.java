package com.naruto.mobile.serviceaoptestdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.naruto.mobile.R;
import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.base.serviceaop.demo.service.ExtTextService;
import com.naruto.mobile.base.serviceaop.demo.service.InnerTxtService;

public class ServiceAopActivity extends AppCompatActivity {

    private TextView mTextView, mTextView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_aop);
        mTextView = (TextView) findViewById(R.id.tv);
        mTextView2 = (TextView)findViewById(R.id.tv2);
        ExtTextService service = NarutoApplication.getInstance().getNarutoApplicationContext().getExtServiceByInterface(ExtTextService.class.getName());
        Log.d("xxm, 1 + 9", service.add(1, 9) + "");
        mTextView.setText(service.add(1, 9)+ "");
        InnerTxtService innerTxtService = NarutoApplication.getInstance().getNarutoApplicationContext().findServiceByInterface(InnerTxtService.class.getName());
        Log.d("xxm, 1 - 9", innerTxtService.subtract(1, 9) + "");
        mTextView2.setText(innerTxtService.subtract(1, 9)+ "");
    }
}

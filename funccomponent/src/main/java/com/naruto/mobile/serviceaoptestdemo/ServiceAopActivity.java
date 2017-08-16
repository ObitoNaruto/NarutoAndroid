package com.naruto.mobile.serviceaoptestdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.naruto.mobile.R;
import com.naruto.mobile.base.serviceaop.NarutoApplication;
import com.naruto.mobile.base.serviceaop.demo.broadcast.TestDemoReceiverConstants;
import com.naruto.mobile.base.serviceaop.demo.service.ExtTextService;
import com.naruto.mobile.base.serviceaop.demo.service.InnerTxtService;
import com.naruto.mobile.framework.biz.ext.shortCut.ShortCutService;

public class ServiceAopActivity extends AppCompatActivity {

    private TextView mTextView, mTextView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_aop);
        mTextView = (TextView) findViewById(R.id.tv);
        mTextView2 = (TextView)findViewById(R.id.tv2);
        //外部服务，需要在moduled的MetaInfo中进行配置
        ExtTextService service = NarutoApplication.getInstance().getNarutoApplicationContext().getExtServiceByInterface(ExtTextService.class.getName());
        Log.d("xxm, 1 + 9", service.add(1, 9) + "");
        mTextView.setText(service.add(1, 9)+ "");
        //内你内部框架提供的基础服务
        InnerTxtService innerTxtService = NarutoApplication.getInstance().getNarutoApplicationContext().findServiceByInterface(InnerTxtService.class.getName());
        Log.d("xxm, 1 - 9", innerTxtService.subtract(1, 9) + "");
        mTextView2.setText(innerTxtService.subtract(1, 9)+ "");

        //测试一下系统框架的广播
        Intent intent = new Intent(TestDemoReceiverConstants.TEST_BROADCAST_RECEIVER);
        LocalBroadcastManager.getInstance(NarutoApplication.getInstance().getApplicationContext()).sendBroadcast(intent);

    }

    public void onClickShortcutService(View view){
        ShortCutService shortCutService = NarutoApplication.getInstance().getNarutoApplicationContext().getExtServiceByInterface(ShortCutService.class.getName());
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        try{
            shortCutService.installAppSchemeShortCut("徐新明测试", bitmap, null, ServiceAopActivity.class.getName());
        }catch (Exception e){
            Log.e("xxm", "installAppSchemeShortCut", e);
        }
    }
}

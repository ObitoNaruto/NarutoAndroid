package com.naruto.mobile.LogReport.PowerLogReport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;

import com.naruto.mobile.LogReport.PowerLogReport.libray.LogReport;
import com.naruto.mobile.LogReport.PowerLogReport.libray.save.imp.LogWriter;
import com.naruto.mobile.LogReport.PowerLogReport.libray.util.FileUtil;
import com.naruto.mobile.R;

public class LogReportTestActivity extends AppCompatActivity {
    public static final String TAG = "LogReportTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_report_test);
        LogReport.getInstance().upload(this);
        setUpListener();
    }

    private void setUpListener() {
        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = null;
                s.length();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogWriter.writeLog(TAG, "打Log测试！！！！");
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtil.deleteDir(new File(LogReport.getInstance().getROOT()));
            }
        });
    }
}

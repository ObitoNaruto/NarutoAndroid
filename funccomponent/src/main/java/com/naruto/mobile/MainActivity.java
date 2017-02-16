package com.naruto.mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.naruto.mobile.log.KLog.test.KLogTestActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickKLog(View view){
        Intent intent = new Intent(this, KLogTestActivity.class);
        startActivity(intent);
    }
}

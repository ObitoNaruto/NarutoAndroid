package com.naruto.mobile.swipeback.demo;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import com.naruto.mobile.R;

public class TestActivity2 extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            datas.add(String.valueOf(i));
        }
    }
}

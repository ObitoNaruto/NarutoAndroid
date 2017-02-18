package com.naruto.mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.naruto.mobile.adapterdelegate.sample.AdapterDelegateTestActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickAdapterDelegate(View view){
        Intent intent = new Intent(this, AdapterDelegateTestActivity.class);
        startActivity(intent);
    }
}

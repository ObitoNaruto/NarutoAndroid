package com.naruto.mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.naruto.mobile.adapterdelegate.sample.AdapterDelegateTestActivity;
import com.naruto.mobile.toast.WealthToast.WealthToastTestActivity;

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

    public void onClickBaseRecyclerViewAdapterHelper(View view){
        Intent intent = new Intent(this, com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.HomeActivity.class);
        startActivity(intent);
    }

    public void onClickWealthToastTest(View view){
        Intent intent = new Intent(this, WealthToastTestActivity.class);
        startActivity(intent);
    }
}

package com.naruto.mobile.toast.WealthToast;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.naruto.mobile.R;

public class WealthToastTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wealth_toast_test);
    }

    public void onClickSuccessToastTest(View View){
        WealthToast.getInstance().makeSuccess(this, "成功");
    }

    public void onClickFavoriteToastTest(View view){
        WealthToast.getInstance().makeFavorite(this, "失败");
    }
}

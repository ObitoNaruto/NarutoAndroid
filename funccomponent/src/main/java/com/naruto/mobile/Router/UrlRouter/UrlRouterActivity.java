package com.naruto.mobile.Router.UrlRouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.naruto.mobile.R;
import com.naruto.mobile.base.Router.UrlRouter.UrlRouter;

public class UrlRouterActivity extends AppCompatActivity {

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_router);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mButton = (Button)findViewById(R.id.btn);
        Toast.makeText(this, mButton.getText().toString(), Toast.LENGTH_SHORT).show();
//        mButton.setText("跳转下一个界面");
        mButton.setVisibility(View.VISIBLE);

//        Route startedRoute = UrlRouter.getStartedRoute(this);
//        Route currentRoute = UrlRouter.getCurrentRoute(this);
//        if (startedRoute != null)
//            Log.e("zxy", "Main:startedRoute:" + startedRoute.toString());
//        if (currentRoute != null)
//            Log.e("zxy", "Main:currentRoute:" + currentRoute.toString());
    }
    public void onClick(View view) {
        UrlRouter.from(this).requestCode(8).jump("activity://native/login");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 8) {
                Log.e("zxy", "SUCCESS");
            }
        }
    }

}

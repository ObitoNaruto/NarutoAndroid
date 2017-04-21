package com.naruto.mobile.Router.AndRouter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.naruto.mobile.R;
import com.naruto.mobile.RouterMap;
import com.naruto.mobile.base.Router.andRouter.router.ActivityRouter;


@RouterMap({"activity2://third", "activity://third2"})
public class AndRouterThirdActivity extends Activity {

    TextView vTimeTv;
    Button vBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.and_router_activity_third);
        vTimeTv = (TextView) findViewById(R.id.time);
        Date date = (Date) getIntent().getSerializableExtra("date");
        vTimeTv.setText(new SimpleDateFormat("HH:mm").format(date));
        vBtn = (Button) findViewById(R.id.set_flags_btn);
        vBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityRouter
                        .getInstance()
                        .getRoute("activity://main")
                        .withOpenMethodStart(AndRouterThirdActivity.this)
                        .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                        .open();
            }
        });
    }
}

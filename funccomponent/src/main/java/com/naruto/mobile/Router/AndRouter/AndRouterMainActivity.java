package com.naruto.mobile.Router.AndRouter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Date;
import java.util.Queue;

import com.naruto.mobile.R;
import com.naruto.mobile.RouterMap;
import com.naruto.mobile.base.Router.andRouter.Router;
import com.naruto.mobile.base.Router.andRouter.route.ActivityRoute;
import com.naruto.mobile.base.Router.andRouter.router.ActivityRouter;
import com.naruto.mobile.base.Router.andRouter.router.HistoryItem;

@RouterMap("activity://main")
public class AndRouterMainActivity extends Activity {


    Button btn1;

    Button btn2;

    Button btn3;

    Button btn4;

    Button btn5;

    Button btn6;

    Button btn7;

    Button btn8;

    Button btn9;

    Button btn10;

    Button btn11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.and_router_main_activity);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn5 = (Button) findViewById(R.id.btn5);
        btn6 = (Button) findViewById(R.id.btn6);
        btn7 = (Button) findViewById(R.id.btn7);
        btn8 = (Button) findViewById(R.id.btn8);
        btn9 = (Button) findViewById(R.id.btn9);
        btn10 = (Button) findViewById(R.id.btn10);
        btn11 = (Button) findViewById(R.id.btn11);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openSecondActivity();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecondActivityWithVerticalAnim();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecondActivityWithHorizontalAnim();
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecondActivityForResult();
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.open("http://www.baidu.com");
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThirdActivityWithExtraValue();
            }
        });

        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThirdActivityWithExtraValueUsingAnotherRoute();
            }
        });

        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUnknowUrl();
            }
        });

        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.open(AndRouterMainActivity.this, "http://www.souhu.com");
            }
        });

        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.open(AndRouterMainActivity.this, "activity://intercepted");
            }
        });

        btn11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityRouter.getInstance()
                        .getRoute("activity://intercepted")
                        .withOpenMethodStart(AndRouterMainActivity.this)
                        .open();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Queue<HistoryItem> historyItems = Router.getActivityChangedHistories();
        for(HistoryItem item : historyItems){
//            Timber.i("%s %s", item.getFrom().toString(), item.getTo().toString());
        }
    }

    private void openSecondActivity(){
        Router.open(this, "activity://second/%s", "汤二狗");
    }

    private void openSecondActivityWithVerticalAnim(){
        ActivityRoute activityRoute = (ActivityRoute) Router.getRoute("activity://second/%s", "汤二狗");
        activityRoute
                .setAnimation(this, R.anim.and_router_in_from_left, R.anim.and_router_out_to_right)//这里传递进当前上下文，当前activity，todo:以后可以改造，自动获取栈顶的activity
                .open();


    }

    private void openSecondActivityWithHorizontalAnim(){
        ActivityRoute activityRoute = (ActivityRoute) Router.getRoute("activity://second/%s", "汤二狗");
        activityRoute.setAnimation(this, R.anim.and_router_in_from_top, R.anim.and_router_out_to_bottom)
                .open();
    }

    private void openSecondActivityForResult(){
        ActivityRoute activityRoute = (ActivityRoute) Router.getRoute("activity://second/汤二狗");
        activityRoute.withOpenMethodStartForResult(this, 200)
                .open();
    }

    private void openThirdActivityWithExtraValue(){
        Date date = new Date();
        ActivityRoute activityRoute = (ActivityRoute) Router.getRoute("activity2://third");
        activityRoute
                .withParams("date", date)
                .open();
    }


    private void openThirdActivityWithExtraValueUsingAnotherRoute(){
        Date date = new Date();
        ActivityRoute activityRoute = (ActivityRoute) Router.getRoute("activity2://third2?text=%d", 33);
        toasts("" + activityRoute
                .withParams("date", date)
                .open());
    }

    private void openUnknowUrl(){
        toasts("" + Router.open("activity://unknow"));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200){
            Toast.makeText(this, "Result code "+ resultCode, Toast.LENGTH_SHORT).show();
        }
    }


    private void toasts(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}

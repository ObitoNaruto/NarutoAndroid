package com.naruto.mobile.pullrefresh.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.naruto.mobile.R;
import com.naruto.mobile.pullrefresh.base.impl.PullRefreshListView;

public class PullRefreshActivity extends AppCompatActivity {

    ListView mListView;

    PullRefreshListView mPullRefreshListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pull_refresh_main_demo);

        findViewById(R.id.refresh_listview).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PullRefreshActivity.this, ShowActivity.class);
                intent.putExtra("index", ShowActivity.REFRESH_LV);
                startActivity(intent);
            }
        });

        findViewById(R.id.refresh_gridview).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PullRefreshActivity.this, ShowActivity.class);
                intent.putExtra("index", ShowActivity.REFRESH_GV);
                startActivity(intent);
            }
        });

        findViewById(R.id.refresh_textview).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PullRefreshActivity.this, ShowActivity.class);
                intent.putExtra("index", ShowActivity.REFRESH_TV);
                startActivity(intent);
            }
        });

        findViewById(R.id.refresh_slide_lv).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PullRefreshActivity.this, ShowActivity.class);
                intent.putExtra("index", ShowActivity.REFRESH_SLIDE_LV);
                startActivity(intent);
            }
        });

        // swipe layout
        findViewById(R.id.swipe_refresh_lv).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PullRefreshActivity.this, ShowActivity.class);
                intent.putExtra("index", ShowActivity.SWIPE_LV);
                startActivity(intent);
            }
        });

    }
}

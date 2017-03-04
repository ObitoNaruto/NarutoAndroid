package com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.BaseQuickAdapter;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.adapter.HomeAdapter;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.entity.HomeItem;
import com.naruto.mobile.R;


/**
 */
public class HomeActivity extends AppCompatActivity {
    private static final Class<?>[] ACTIVITY = {
            AnimationUseActivity.class,
            MultipleItemUseActivity.class,
            HeaderAndFooterUseActivity.class,
            PullToRefreshUseActivity.class,
            SectionUseActivity.class,
            EmptyViewUseActivity.class};
    private static final String[] TITLE = {
            "Animation Use",
            "MultipleItem Use",
            "HeaderAndFooter Use",
            "PullToRefresh Use",
            "Section Use",
            "EmptyView Use"};
    private static final String[] COLOR_STR = {
            "#0dddb8",
            "#0bd4c3",
            "#03cdcd",
            "#00b1c5",
            "#04b2d1",
            "#04b2d1",
            "#04b2d1"};
    private ArrayList<HomeItem> mDataList;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initData();
        //将item布局和数据设置进入
        BaseQuickAdapter homeAdapter = new HomeAdapter( R.layout.home_item_view, mDataList);
        homeAdapter.openLoadAnimation();//打开动画开关
        homeAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(HomeActivity.this, ACTIVITY[position]);
                startActivity(intent);
            }
        });
        homeAdapter.setOnRecyclerViewItemLongClickListener(new BaseQuickAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                Toast.makeText(HomeActivity.this,"onItemLongClick", Toast.LENGTH_LONG).show();
                return true;
            }
        });
        mRecyclerView.setAdapter(homeAdapter);
    }

    /**
     * mock数据
     */
    private void initData() {
        mDataList = new ArrayList<>();
        for (int i = 0; i < TITLE.length; i++) {
            HomeItem item = new HomeItem();
            item.setTitle(TITLE[i]);
            item.setActivity(ACTIVITY[i]);
            item.setColorStr(COLOR_STR[i]);
            mDataList.add(item);
        }
    }

}

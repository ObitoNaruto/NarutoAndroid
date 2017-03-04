package com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.adapter.QuickAdapter;
import com.naruto.mobile.R;


public class EmptyViewUseActivity extends Activity {
    private RecyclerView mRecyclerView;
    private QuickAdapter mQuickAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_view_use);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
    }

    private void initAdapter() {
        mQuickAdapter = new QuickAdapter(this, 0);
        View emptyView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) mRecyclerView.getParent(), false);//空view的样式
        mQuickAdapter.setEmptyView(true, true, emptyView);//显示
        View view = getLayoutInflater().inflate(R.layout.head_view, (ViewGroup) mRecyclerView.getParent(), false);
        mQuickAdapter.addHeaderView(view);
        mQuickAdapter.addHeaderView(view);
        mQuickAdapter.addFooterView(view);
        mRecyclerView.setAdapter(mQuickAdapter);
    }
}

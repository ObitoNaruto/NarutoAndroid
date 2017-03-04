package com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.BaseQuickAdapter;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.adapter.QuickAdapter;
import com.naruto.mobile.R;


/**
 */
public class HeaderAndFooterUseActivity extends Activity {

    private RecyclerView mRecyclerView;
    private QuickAdapter mQuickAdapter;
    private static final int PAGE_SIZE = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header_and_footer_use);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
        mQuickAdapter.addHeaderView(getView());//添加头view
        mQuickAdapter.addFooterView(getView());//添加尾view
        mRecyclerView.setAdapter(mQuickAdapter);
    }

    private View getView() {
        View view = getLayoutInflater().inflate(R.layout.head_view, null);
        view.findViewById(R.id.tv).setVisibility(View.GONE);
        view.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HeaderAndFooterUseActivity.this, "click View", Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    private void initAdapter() {
        mQuickAdapter = new QuickAdapter(HeaderAndFooterUseActivity.this, PAGE_SIZE);
        mQuickAdapter.openLoadAnimation();
        mRecyclerView.setAdapter(mQuickAdapter);
        mQuickAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(HeaderAndFooterUseActivity.this, "" + Integer.toString(position), Toast.LENGTH_LONG).show();
            }
        });
    }

}

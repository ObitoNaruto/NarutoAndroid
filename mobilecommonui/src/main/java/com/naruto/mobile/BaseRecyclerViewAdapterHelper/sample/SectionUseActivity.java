package com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.BaseQuickAdapter;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.adapter.SectionAdapter;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.data.DataServer;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.entity.MySection;
import com.naruto.mobile.R;


/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class SectionUseActivity extends Activity implements BaseQuickAdapter.OnRecyclerViewItemClickListener {
    private RecyclerView mRecyclerView;
    private List<MySection> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_uer);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));//表格布局
        mData = DataServer.getSampleData();//数据有两种样式，用以显示两种view
        SectionAdapter sectionAdapter = new SectionAdapter(R.layout.item_section_content, R.layout.def_section_head, mData);//demo可以这样写，但是从网络拉数据，这样就不方便，因为事先没法预知有多少样式的数据
        sectionAdapter.setOnRecyclerViewItemClickListener(this);
        //同时需要在adapter中进行对某个子view进行设置监听，否则没效果
        sectionAdapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {//item项中某个子view的点击事件
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(SectionUseActivity.this, "onItemChildClick:more", Toast.LENGTH_LONG).show();
            }
        });
        View view = getLayoutInflater().inflate(R.layout.head_view, (ViewGroup) mRecyclerView.getParent(), false);
        sectionAdapter.addHeaderView(view);
        sectionAdapter.addFooterView(view);
        mRecyclerView.setAdapter(sectionAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        MySection mySection = mData.get(position);
        if (mySection.isHeader)
            Toast.makeText(this, mySection.header, Toast.LENGTH_LONG).show();//长条item
        else
            Toast.makeText(this, mySection.t.getName(), Toast.LENGTH_LONG).show();//方形的item
    }
}

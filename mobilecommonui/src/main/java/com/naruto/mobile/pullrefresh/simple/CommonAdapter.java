package com.naruto.mobile.pullrefresh.simple;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {
    Context mContext;
    List<T> mData;
    private int mItemLayoutId = -1;

    public CommonAdapter(Context context, int itemLayoutResId, List<T> dataSource) {
        this.checkParams(context, itemLayoutResId, dataSource);
        this.mContext = context;
        this.mItemLayoutId = itemLayoutResId;
        this.mData = dataSource;
    }

    private void checkParams(Context context, int itemLayoutResId, List<T> dataSource) {
        if(context == null || itemLayoutResId < 0 || dataSource == null) {
            throw new RuntimeException("context == null || itemLayoutResId < 0 || dataSource == null, please check your params");
        }
    }

    public int getCount() {
        return this.mData.size();
    }

    public T getItem(int position) {
        return this.mData.get(position);
    }

    public long getItemId(int position) {
        return (long)position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder viewHolder = CommonViewHolder.getViewHolder(this.mContext, convertView, parent, this.mItemLayoutId);
        this.fillItemData(viewHolder, position, this.getItem(position));
        return viewHolder.getContentView();
    }

    protected abstract void fillItemData(CommonViewHolder viewHolder, int position, T item);
}

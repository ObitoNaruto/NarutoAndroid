package com.naruto.mobile.adapterdelegate.sample.model;

import android.support.annotation.IdRes;
import android.support.v4.util.SparseArrayCompat;
import android.view.View;

import com.naruto.mobile.adapterdelegate.library.ViewHolder;


/**
 * Created by benio on 2016/3/4.
 */
public class ListViewHolder extends ViewHolder {

    private SparseArrayCompat<View> mViews;

    public ListViewHolder(View itemView) {
        super(itemView);
        this.mViews = new SparseArrayCompat<>();
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(@IdRes int viewId) {
        View view = mViews.get(viewId);
        if (null == view) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

}

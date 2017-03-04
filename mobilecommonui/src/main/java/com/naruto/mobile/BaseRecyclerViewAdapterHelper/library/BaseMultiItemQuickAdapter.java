package com.naruto.mobile.BaseRecyclerViewAdapterHelper.library;

import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.entity.MultiItemEntity;


/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public abstract class BaseMultiItemQuickAdapter<T extends MultiItemEntity> extends BaseQuickAdapter {

    /**
     * layouts indexed with their types
     */
    private SparseArray<Integer> layouts;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data    A new list is created out of this one to avoid mutable list
     */
    public BaseMultiItemQuickAdapter( List<T> data) {
        super( data);
    }

    @Override
    protected int getDefItemViewType(int position) {
        return ((MultiItemEntity) mData.get(position)).getItemType();//获取当前数据对应的itemViewType
    }


    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return createBaseViewHolder(parent, getLayoutId(viewType));
    }

    private int getLayoutId(int viewType) {
        return layouts.get(viewType);
    }

    protected void addItemType(int type, int layoutResId) {
        if (layouts == null) {
            layouts = new SparseArray<>();
        }
        layouts.put(type, layoutResId);
    }


    @Override
    protected void convert(BaseViewHolder helper, Object item) {
        convert(helper, (T) item);
    }

    protected abstract void convert(BaseViewHolder helper, T item);


}



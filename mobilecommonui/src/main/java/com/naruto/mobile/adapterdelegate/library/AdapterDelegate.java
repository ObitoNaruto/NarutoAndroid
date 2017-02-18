package com.naruto.mobile.adapterdelegate.library;


import com.naruto.mobile.adapterdelegate.library.interf.DataProvider;
import com.naruto.mobile.adapterdelegate.library.interf.Delegate;

/**
 * A simple {@link Delegate} implementation that already implements {@link #getItem(int)} {@link #getItemCount()}
 * <p/>
 * Created by benio on 2016/1/26.
 *
 * @param <VH> The type of the ViewHolder
 */
public abstract class AdapterDelegate<VH> implements Delegate<VH>, DataProvider {

    private DataProvider mDataProvider;

    public AdapterDelegate() {
    }

    public AdapterDelegate(DataProvider dataProvider) {
        this.mDataProvider = dataProvider;
    }

    public void setDataProvider(DataProvider provider) {
        this.mDataProvider = provider;
    }

    public DataProvider getDataProvider() {
        return mDataProvider;
    }

    @Override
    public Object getItem(int position) {
        return mDataProvider != null ? mDataProvider.getItem(position) : null;//DataProvider的getItem方法在MainAdapter中初始化
    }

    @Override
    public int getItemCount() {
        return mDataProvider != null ? mDataProvider.getItemCount() : 0;//getItemCount同上
    }
}

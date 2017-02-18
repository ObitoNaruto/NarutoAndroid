package com.naruto.mobile.adapterdelegate.sample.delegate.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naruto.mobile.R;
import com.naruto.mobile.adapterdelegate.library.AdapterDelegate;
import com.naruto.mobile.adapterdelegate.library.interf.DataProvider;
import com.naruto.mobile.adapterdelegate.sample.model.Advertisement;


/**
 * Created by benio on 2016/3/2.
 */
public class AdvertisementAdapterDelegate extends AdapterDelegate<RecyclerView.ViewHolder> {
    public AdvertisementAdapterDelegate() {
    }

    public AdvertisementAdapterDelegate(DataProvider dataProvider) {
        super(dataProvider);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_advertisement, parent, false);
        return new AdvertisementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(int position, RecyclerView.ViewHolder holder) {

    }

    @Override
    public boolean isForPosition(int position) {
        return getItem(position) instanceof Advertisement;
    }

    /**
     * The ViewHolder
     */
    static class AdvertisementViewHolder extends RecyclerView.ViewHolder {
        public AdvertisementViewHolder(View itemView) {
            super(itemView);
        }
    }
}

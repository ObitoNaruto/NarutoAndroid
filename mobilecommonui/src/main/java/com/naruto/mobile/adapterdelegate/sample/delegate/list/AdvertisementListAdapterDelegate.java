package com.naruto.mobile.adapterdelegate.sample.delegate.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naruto.mobile.R;
import com.naruto.mobile.adapterdelegate.library.AdapterDelegate;
import com.naruto.mobile.adapterdelegate.library.interf.DataProvider;
import com.naruto.mobile.adapterdelegate.sample.model.Advertisement;
import com.naruto.mobile.adapterdelegate.sample.model.ListViewHolder;


/**
 * Created by benio on 2016/3/2.
 */
public class AdvertisementListAdapterDelegate extends AdapterDelegate<ListViewHolder> {
    public AdvertisementListAdapterDelegate() {
    }

    public AdvertisementListAdapterDelegate(DataProvider dataProvider) {
        super(dataProvider);
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_advertisement, parent, false);
        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(int position, ListViewHolder holder) {

    }

    @Override
    public boolean isForPosition(int position) {
        return getItem(position) instanceof Advertisement;
    }
}

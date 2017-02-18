package com.naruto.mobile.adapterdelegate.sample.delegate.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naruto.mobile.R;
import com.naruto.mobile.adapterdelegate.library.AdapterDelegate;
import com.naruto.mobile.adapterdelegate.library.interf.DataProvider;
import com.naruto.mobile.adapterdelegate.sample.model.Cat;


/**
 * Created by benio on 2016/3/2.
 */
public class CatAdapterDelegate extends AdapterDelegate<RecyclerView.ViewHolder> {
    public CatAdapterDelegate() {
    }

    public CatAdapterDelegate(DataProvider dataProvider) {
        super(dataProvider);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cat, parent, false);
        return new CatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(int position, RecyclerView.ViewHolder holder) {
        TextView textView = ((CatViewHolder) holder).name;
        Cat cat = (Cat) getItem(position);
        textView.setText(cat.getName());
    }

    @Override
    public boolean isForPosition(int position) {
        return getItem(position) instanceof Cat;
    }

    static class CatViewHolder extends RecyclerView.ViewHolder {

        public TextView name;

        public CatViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }
}

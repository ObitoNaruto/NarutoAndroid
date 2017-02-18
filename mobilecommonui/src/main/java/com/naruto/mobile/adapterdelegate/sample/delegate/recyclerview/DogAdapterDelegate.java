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
import com.naruto.mobile.adapterdelegate.sample.model.Dog;


/**
 * Created by benio on 2016/3/2.
 */
public class DogAdapterDelegate extends AdapterDelegate<RecyclerView.ViewHolder> {
    public DogAdapterDelegate() {
    }

    public DogAdapterDelegate(DataProvider dataProvider) {
        super(dataProvider);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dog, parent, false);
        return new DogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(int position, RecyclerView.ViewHolder holder) {
        TextView textView = ((DogViewHolder) holder).name;
        Dog dog = (Dog) getItem(position);
        textView.setText(dog.getName());
    }

    @Override
    public boolean isForPosition(int position) {
        return getItem(position) instanceof Dog;
    }

    static class DogViewHolder extends RecyclerView.ViewHolder {

        public TextView name;

        public DogViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }

}

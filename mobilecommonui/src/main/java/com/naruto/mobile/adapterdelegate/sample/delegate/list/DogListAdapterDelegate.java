package com.naruto.mobile.adapterdelegate.sample.delegate.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naruto.mobile.R;
import com.naruto.mobile.adapterdelegate.library.AdapterDelegate;
import com.naruto.mobile.adapterdelegate.library.interf.DataProvider;
import com.naruto.mobile.adapterdelegate.sample.model.Dog;
import com.naruto.mobile.adapterdelegate.sample.model.ListViewHolder;


/**
 * Created by benio on 2016/3/2.
 */
public class DogListAdapterDelegate extends AdapterDelegate<ListViewHolder> {
    public DogListAdapterDelegate() {
    }

    public DogListAdapterDelegate(DataProvider dataProvider) {
        super(dataProvider);
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dog, parent, false);
        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(int position, ListViewHolder holder) {
        TextView textView = holder.getView(R.id.name);
        Dog dog = (Dog) getItem(position);
        textView.setText(dog.getName());
    }

    @Override
    public boolean isForPosition(int position) {
        return getItem(position) instanceof Dog;
    }
}

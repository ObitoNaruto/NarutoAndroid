package com.naruto.mobile.adapterdelegate.sample.delegate.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naruto.mobile.R;
import com.naruto.mobile.adapterdelegate.library.AdapterDelegate;
import com.naruto.mobile.adapterdelegate.library.interf.DataProvider;
import com.naruto.mobile.adapterdelegate.sample.model.Gecko;
import com.naruto.mobile.adapterdelegate.sample.model.ListViewHolder;


/**
 * Created by benio on 2016/3/2.
 */
public class GeckoListAdapterDelegate extends AdapterDelegate<ListViewHolder> {
    public GeckoListAdapterDelegate() {
    }

    public GeckoListAdapterDelegate(DataProvider dataProvider) {
        super(dataProvider);
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gecko, parent, false);
        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(int position, ListViewHolder holder) {
        Gecko gecko = (Gecko) getItem(position);
        TextView nameView = holder.getView(R.id.name);
        TextView raceView = holder.getView(R.id.race);
        nameView.setText(gecko.getName());
        raceView.setText(gecko.getRace());
    }

    @Override
    public boolean isForPosition(int position) {
        return getItem(position) instanceof Gecko;
    }
}

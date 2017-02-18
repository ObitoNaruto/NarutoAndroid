package com.naruto.mobile.adapterdelegate.sample.delegate.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naruto.mobile.R;
import com.naruto.mobile.adapterdelegate.library.AdapterDelegate;
import com.naruto.mobile.adapterdelegate.library.interf.DataProvider;
import com.naruto.mobile.adapterdelegate.sample.model.Gecko;


/**
 * Created by benio on 2016/3/2.
 */
public class GeckoAdapterDelegate extends AdapterDelegate<RecyclerView.ViewHolder> {
    private static final String TAG = "GeckoAdapterDelegate";

    public GeckoAdapterDelegate() {
    }

    public GeckoAdapterDelegate(DataProvider dataProvider) {
        super(dataProvider);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gecko, parent, false);
        return new GeckoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(int position, RecyclerView.ViewHolder holder) {
        Gecko gecko = (Gecko) getItem(position);
        TextView nameView = ((GeckoViewHolder) holder).name;
        TextView raceView = ((GeckoViewHolder) holder).race;
        nameView.setText(gecko.getName());
        raceView.setText(gecko.getRace());
    }

    @Override
    public boolean isForPosition(int position) {
        return getItem(position) instanceof Gecko;
    }

    static class GeckoViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView race;

        public GeckoViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            race = (TextView) itemView.findViewById(R.id.race);
        }
    }

    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        Log.d(TAG, "attachViewToWindow: ");
    }

    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        Log.d(TAG, "detachViewFromWindow: ");
    }
}

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
import com.naruto.mobile.adapterdelegate.sample.model.Snake;


/**
 * Created by benio on 2016/3/2.
 */
public class SnakeAdapterDelegate extends AdapterDelegate<RecyclerView.ViewHolder> {
    private static final String TAG = "SnakeAdapterDelegate";

    public SnakeAdapterDelegate() {
    }

    public SnakeAdapterDelegate(DataProvider dataProvider) {
        super(dataProvider);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_snake, parent, false);
        return new SnakeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(int position, RecyclerView.ViewHolder holder) {
        Snake snake = (Snake) getItem(position);
        TextView nameView = ((SnakeViewHolder) holder).name;
        TextView raceView = ((SnakeViewHolder) holder).race;
        nameView.setText(snake.getName());
        raceView.setText(snake.getRace());
    }

    @Override
    public boolean isForPosition(int position) {
        return getItem(position) instanceof Snake;
    }

    static class SnakeViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView race;

        public SnakeViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            race = (TextView) itemView.findViewById(R.id.race);
        }
    }
}

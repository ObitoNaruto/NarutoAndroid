package com.naruto.mobile.adapterdelegate.sample.delegate.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naruto.mobile.R;
import com.naruto.mobile.adapterdelegate.library.AdapterDelegate;
import com.naruto.mobile.adapterdelegate.library.interf.DataProvider;
import com.naruto.mobile.adapterdelegate.sample.model.ListViewHolder;
import com.naruto.mobile.adapterdelegate.sample.model.Snake;


/**
 * Created by benio on 2016/3/2.
 */
public class SnakeListAdapterDelegate extends AdapterDelegate<ListViewHolder> {
    public SnakeListAdapterDelegate() {
    }

    public SnakeListAdapterDelegate(DataProvider dataProvider) {
        super(dataProvider);
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_snake, parent, false);
        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(int position, ListViewHolder holder) {
        Snake snake = (Snake) getItem(position);
        TextView nameView = holder.getView(R.id.name);
        TextView raceView = holder.getView(R.id.race);
        nameView.setText(snake.getName());
        raceView.setText(snake.getRace());
    }

    @Override
    public boolean isForPosition(int position) {
        return getItem(position) instanceof Snake;
    }
}

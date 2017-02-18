package com.naruto.mobile.adapterdelegate.sample.adapter;

import java.util.List;

import com.naruto.mobile.adapterdelegate.library.AdapterDelegateManager;
import com.naruto.mobile.adapterdelegate.library.DelegateBaseAdapter;
import com.naruto.mobile.adapterdelegate.sample.delegate.list.AdvertisementListAdapterDelegate;
import com.naruto.mobile.adapterdelegate.sample.delegate.list.CatListAdapterDelegate;
import com.naruto.mobile.adapterdelegate.sample.delegate.list.DogListAdapterDelegate;
import com.naruto.mobile.adapterdelegate.sample.delegate.list.GeckoListAdapterDelegate;
import com.naruto.mobile.adapterdelegate.sample.delegate.list.SnakeListAdapterDelegate;
import com.naruto.mobile.adapterdelegate.sample.model.DisplayableItem;
import com.naruto.mobile.adapterdelegate.sample.model.ListViewHolder;


/**
 * Created by benio on 2016/3/2.
 */
public class MainListAdapter extends DelegateBaseAdapter<ListViewHolder> {
    private List<DisplayableItem> mData;

    public MainListAdapter(List<DisplayableItem> data) {
        this.mData = data;

        AdapterDelegateManager<ListViewHolder> manager = getDelegateManager();
//        manager.setDataProvider(this);
        manager.addDelegate(new AdvertisementListAdapterDelegate())
                .addDelegate(new CatListAdapterDelegate())
                .addDelegate(new DogListAdapterDelegate())
                .addDelegate(new GeckoListAdapterDelegate())
                .addDelegate(new SnakeListAdapterDelegate());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

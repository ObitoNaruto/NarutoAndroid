package com.naruto.mobile.adapterdelegate.sample.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import com.naruto.mobile.adapterdelegate.library.DelegateRecyclerAdapter;
import com.naruto.mobile.adapterdelegate.library.interf.DelegateManager;
import com.naruto.mobile.adapterdelegate.sample.delegate.recyclerview.AdvertisementAdapterDelegate;
import com.naruto.mobile.adapterdelegate.sample.delegate.recyclerview.CatAdapterDelegate;
import com.naruto.mobile.adapterdelegate.sample.delegate.recyclerview.DogAdapterDelegate;
import com.naruto.mobile.adapterdelegate.sample.delegate.recyclerview.GeckoAdapterDelegate;
import com.naruto.mobile.adapterdelegate.sample.delegate.recyclerview.SnakeAdapterDelegate;
import com.naruto.mobile.adapterdelegate.sample.model.DisplayableItem;


/**
 */
public class MainAdapter extends DelegateRecyclerAdapter<RecyclerView.ViewHolder> {
    private List<DisplayableItem> mData;

    GeckoAdapterDelegate mGeckoAdapterDelegate;

    /**
     * 如果子类的构造方法中没有通过super显式调用父类的有参构造方法，也没有通过this显式调用自身的其它构造方法，则会默认先调用父类的无参构造方法。
     * @param data
     *
     * 在父类DelegateRecyclerAdapter构造方法中初始话manager,并且绑定DataProvider,在调用的manager的addDelegate方法时，产生回调，在回调中为当前deletegate设置DataProvider
     */
    public MainAdapter(List<DisplayableItem> data) {
        this.mData = data;

        DelegateManager<RecyclerView.ViewHolder> manager = getDelegateManager();//获取Deletgate管理器
        // 手动调用onViewAttachedToWindow，onViewDetachedFromWindow方法
        mGeckoAdapterDelegate = new GeckoAdapterDelegate(this);
        // Delegates
        manager.addDelegate(mGeckoAdapterDelegate)
                .addDelegate(new AdvertisementAdapterDelegate())
                .addDelegate(new CatAdapterDelegate())
                .addDelegate(new DogAdapterDelegate())
                .addDelegate(new SnakeAdapterDelegate());
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (mGeckoAdapterDelegate.isForPosition(holder.getAdapterPosition())) {
            mGeckoAdapterDelegate.onViewAttachedToWindow(holder);
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (mGeckoAdapterDelegate.isForPosition(holder.getAdapterPosition())) {
            mGeckoAdapterDelegate.onViewDetachedFromWindow(holder);
        }
    }
}

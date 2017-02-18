package com.naruto.mobile.adapterdelegate.library;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

import com.naruto.mobile.adapterdelegate.library.interf.DataProvider;
import com.naruto.mobile.adapterdelegate.library.interf.DelegateManager;


/**
 * A {@link RecyclerView.Adapter} subclass using ViewHolder and DelegateManager.<p>
 */
public abstract class DelegateRecyclerAdapter<VH extends ViewHolder> extends Adapter<VH>
        implements DataProvider {

    private final DelegateManager<VH> mDelegateManager;

    public DelegateRecyclerAdapter() {
        this(null);
    }

    protected DelegateRecyclerAdapter(DelegateManager<VH> manager) {
        if (manager == null) {
            manager = new AdapterDelegateManager<>();
        }
        if (manager instanceof AdapterDelegateManager
                && (((AdapterDelegateManager) manager).getDataProvider()) == null) {
            ((AdapterDelegateManager) manager).setDataProvider(this);//setDataProvider是为了获得getItem和getItemCount回调
        }
        this.mDelegateManager = manager;
    }

    /**
     * @return DelegateManager using in this adapter.
     */
    @SuppressWarnings("unchecked")
    protected <T extends DelegateManager<VH>> T getDelegateManager() {
        return (T) mDelegateManager;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return mDelegateManager.createViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        mDelegateManager.bindViewHolder(holder, position, holder.getItemViewType());
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = mDelegateManager.getItemViewType(position);//position其实就是viewType
        if (viewType == DelegateManager.INVALID_TYPE) {
            throw new IllegalArgumentException("No Delegate is responsible for position =" + position
                    + ". Please check your Delegates");
        }
        return viewType;
    }
}

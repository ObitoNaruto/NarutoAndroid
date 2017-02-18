package com.naruto.mobile.adapterdelegate.library;


import com.naruto.mobile.adapterdelegate.library.interf.DataProvider;
import com.naruto.mobile.adapterdelegate.library.interf.Delegate;

/**
 * An extension of {@link ListDelegateManager} that can set {@link DataProvider}
 * automatically for {@link AdapterDelegate} when a delegate is added to this DelegateManager
 *
 * @param <VH> The type of the ViewHolder
 */
public class AdapterDelegateManager<VH> extends ListDelegateManager<VH> {

    /**
     * dataProvider which is to provide data for AdapterDelegate
     */
    private DataProvider mDataProvider;

    public AdapterDelegateManager() {
    }

    public AdapterDelegateManager(int initialCapacity) {
        super(initialCapacity);
    }

    public void setDataProvider(DataProvider dataProvider) {
        mDataProvider = dataProvider;
    }

    public DataProvider getDataProvider() {
        return mDataProvider;
    }

    @Override
    public void onDelegateAdded(Delegate<VH> delegate) {
        if (mDataProvider != null
                && delegate instanceof AdapterDelegate
                && (((AdapterDelegate<VH>) delegate).getDataProvider()) == null) {
            ((AdapterDelegate<VH>) delegate).setDataProvider(mDataProvider);
        }
    }

    @Override
    public void onDelegateRemoved(Delegate<VH> delegate) {
        if (mDataProvider != null
                && delegate instanceof AdapterDelegate
                && (((AdapterDelegate<VH>) delegate).getDataProvider()) == mDataProvider) {
            ((AdapterDelegate<VH>) delegate).setDataProvider(null);
        }
    }
}

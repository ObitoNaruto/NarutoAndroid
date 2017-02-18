package com.naruto.mobile.adapterdelegate.library;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.naruto.mobile.adapterdelegate.library.interf.Delegate;
import com.naruto.mobile.adapterdelegate.library.interf.DelegateManager;
import com.naruto.mobile.adapterdelegate.library.interf.OnDelegateStateChangeListener;


/**
 * An implementation of {@link DelegateManager}
 *
 * @param <VH> The type of the ViewHolder
 */
public class ListDelegateManager<VH> implements DelegateManager<VH> {
    /**
     * Default capacity of {@link DelegateManager}
     */
    public static final int INITIAL_CAPACITY = 4;

    /**
     * A List to store delegates
     */
    private List<Delegate<VH>> mDelegates;

    /**
     * A List to store delegateStateChangeListeners
     */
    private List<OnDelegateStateChangeListener<VH>> mOnDelegateStateChangeListeners;

    /**
     * Creates a new ListDelegateManager with default capacity.
     */
    public ListDelegateManager() {
        this(INITIAL_CAPACITY);
    }

    /**
     * Creates a new ListDelegateManager with the specified capacity.
     *
     * @param initialCapacity capacity of ListDelegateManager to store delegates
     */
    public ListDelegateManager(int initialCapacity) {
        mDelegates = new ArrayList<>(initialCapacity);
    }

    @Override
    public ListDelegateManager<VH> addDelegate(Delegate<VH> delegate) {
        delegate = Utils.checkNotNull(delegate, "Delegate is null");//检查一下是否为空
        // if not found
        if (!mDelegates.contains(delegate)) {
            mDelegates.add(delegate);
            dispatchDelegateAdded(delegate);
        }
        return this;
    }

    private void dispatchDelegateAdded(Delegate<VH> delegate) {
        onDelegateAdded(delegate);//为delegate设置setDataProvider
        if (mOnDelegateStateChangeListeners != null) {
            final int count = mOnDelegateStateChangeListeners.size();
            for (int i = 0; i < count; i++) {
                mOnDelegateStateChangeListeners.get(i).onDelegateAdded(delegate);
            }
        }
    }

    @Override
    public ListDelegateManager<VH> removeDelegate(Delegate<VH> delegate) {
        delegate = Utils.checkNotNull(delegate, "Delegate is null");
        if (mDelegates.remove(delegate)) {
            dispatchDelegateRemoved(delegate);
        }
        return this;
    }

    private void dispatchDelegateRemoved(Delegate<VH> delegate) {
        onDelegateRemoved(delegate);//为delegate设置setDataProvider,解绑
        if (mOnDelegateStateChangeListeners != null) {
            final int count = mOnDelegateStateChangeListeners.size();
            for (int i = 0; i < count; i++) {
                mOnDelegateStateChangeListeners.get(i).onDelegateRemoved(delegate);
            }
        }
    }

    /**
     * @return a unmodifiable List of Delegate.
     */
    @Override
    public List<Delegate<VH>> getDelegates() {
        return Collections.unmodifiableList(mDelegates);
    }

    @Override
    public int getDelegateCount() {
        return mDelegates.size();
    }

    @Override
    public int getItemViewType(int position) {
        int size = mDelegates.size();
        for (int i = 0; i < size; i++) {
            Delegate<VH> delegate = mDelegates.get(i);
            if (delegate.isForPosition(position)) {
                // return index as view type
                return i;
            }
        }
        return INVALID_TYPE;
    }

    @Override
    public VH createViewHolder(ViewGroup parent, int viewType) {
        Delegate<VH> delegate = getDelegateByViewType(viewType);
        delegate = Utils.checkNotNull(delegate, "Delegate is not found and viewType is " + viewType);

        VH vh = delegate.onCreateViewHolder(parent);
        vh = Utils.checkNotNull(vh, "ViewHolder is null");
        return vh;
    }

    @Override
    public void bindViewHolder(VH holder, int position, int viewType) {
        Delegate<VH> delegate = getDelegateByViewType(viewType);
        delegate = Utils.checkNotNull(delegate, "Delegate is not found and viewType is " + viewType);
        delegate.onBindViewHolder(position, holder);
    }

    private Delegate<VH> getDelegateByViewType(int viewType) {
        // view type is same as index here
        // @see #getItemViewType(int)
        int index = viewType;
        return index >= 0 && index < mDelegates.size() ? mDelegates.get(index) : null;
    }

    /**
     * Register a listener that will be notified Delegate is added to or removed
     * from DelegateManager.
     *
     * @param listener Listener to register
     */
    public void addOnDelegateStateChangeListener(OnDelegateStateChangeListener<VH> listener) {
        if (mOnDelegateStateChangeListeners == null) {
            mOnDelegateStateChangeListeners = new ArrayList<>();
        }
        mOnDelegateStateChangeListeners.add(listener);
    }

    /**
     * Removes the provided listener from delegate state listeners list.
     *
     * @param listener Listener to unregister
     */
    public void removeOnDelegateStateChangeListener(OnDelegateStateChangeListener<VH> listener) {
        if (mOnDelegateStateChangeListeners == null) {
            return;
        }
        mOnDelegateStateChangeListeners.remove(listener);
    }

    /**
     * Removes all listeners that were added via
     * {@link #addOnDelegateStateChangeListener(OnDelegateStateChangeListener)}.
     */
    public void clearOnDelegateStateChangeListeners() {
        if (mOnDelegateStateChangeListeners != null) {
            mOnDelegateStateChangeListeners.clear();
        }
    }

    /**
     * Called when a delegate is added to the DelegateManager.
     * <p>Subclasses of DelegateManager may want to perform extra bookkeeping or modifications
     * of delegate as they become added.</p>
     *
     * @param delegate The Delegate which is added to the DelegateManager
     */
    public void onDelegateAdded(Delegate<VH> delegate) {
    }

    /**
     * Called when a delegate is removed from DelegateManager.
     * <p>Subclasses of DelegateManager may want to perform extra bookkeeping or modifications
     * of delegate as they become removed.</p>
     *
     * @param delegate The Delegate which is being removed from the DelegateManager
     */
    public void onDelegateRemoved(Delegate<VH> delegate) {
    }
}

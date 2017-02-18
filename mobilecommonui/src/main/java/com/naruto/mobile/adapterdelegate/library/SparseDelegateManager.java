package com.naruto.mobile.adapterdelegate.library;

import android.support.v4.util.SparseArrayCompat;
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
public class SparseDelegateManager<VH> implements DelegateManager<VH> {
    /**
     * Default capacity of SparseDelegateManager
     */
    public static final int INITIAL_CAPACITY = 4;

    /**
     * A map for viewType to Delegate
     */
    private SparseArrayCompat<Delegate<VH>> mDelegates;

    /**
     * A List to store delegateStateChangeListeners
     */
    private List<OnDelegateStateChangeListener<VH>> mOnDelegateStateChangeListeners;

    /**
     * Creates a new SparseDelegateManager with default capacity.
     */
    public SparseDelegateManager() {
        this(INITIAL_CAPACITY);
    }

    /**
     * Creates a new SparseDelegateManager with specified capacity.
     *
     * @param initialCapacity capacity of SparseDelegateManager
     */
    public SparseDelegateManager(int initialCapacity) {
        mDelegates = new SparseArrayCompat<>(initialCapacity);
    }

    @Override
    public SparseDelegateManager<VH> addDelegate(Delegate<VH> delegate) {
        delegate = Utils.checkNotNull(delegate, "Delegate is null");
        int index = mDelegates.indexOfValue(delegate);
        // not found
        if (index < 0) {
            int viewType = generateViewType(delegate);
            mDelegates.append(viewType, delegate);
            dispatchDelegateAdded(delegate);
        }
        return this;
    }

    protected int generateViewType(Delegate<VH> delegate) {
        return mDelegates.size();
    }

    private void dispatchDelegateAdded(Delegate<VH> delegate) {
        onDelegateAdded(delegate);
        if (mOnDelegateStateChangeListeners != null) {
            final int count = mOnDelegateStateChangeListeners.size();
            for (int i = 0; i < count; i++) {
                mOnDelegateStateChangeListeners.get(i).onDelegateAdded(delegate);
            }
        }
    }

    @Override
    public SparseDelegateManager<VH> removeDelegate(Delegate<VH> delegate) {
        delegate = Utils.checkNotNull(delegate, "Delegate is null");
        int index = mDelegates.indexOfValue(delegate);
        if (index >= 0) {
            mDelegates.removeAt(index);
            dispatchDelegateRemoved(delegate);
        }
        return this;
    }

    private void dispatchDelegateRemoved(Delegate<VH> delegate) {
        onDelegateRemoved(delegate);
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
        int size = mDelegates.size();
        List<Delegate<VH>> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Delegate<VH> delegate = mDelegates.valueAt(i);
            result.add(delegate);
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public int getDelegateCount() {
        return mDelegates.size();
    }

    @Override
    public int getItemViewType(int position) {
        int size = mDelegates.size();
        for (int i = 0; i < size; i++) {
            Delegate<VH> delegate = mDelegates.valueAt(i);
            if (delegate.isForPosition(position)) {
                return mDelegates.keyAt(i);
            }
        }
        return INVALID_TYPE;
    }

    @Override
    public VH createViewHolder(ViewGroup parent, int viewType) {
        Delegate<VH> delegate = mDelegates.get(viewType);
        delegate = Utils.checkNotNull(delegate, "Delegate is not found and viewType is " + viewType);

        VH vh = delegate.onCreateViewHolder(parent);
        vh = Utils.checkNotNull(vh, "ViewHolder is null");
        return vh;
    }

    @Override
    public void bindViewHolder(VH holder, int position, int viewType) {
        Delegate<VH> delegate = mDelegates.get(viewType);
        delegate = Utils.checkNotNull(delegate, "Delegate is not found and viewType is " + viewType);
        delegate.onBindViewHolder(position, holder);
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

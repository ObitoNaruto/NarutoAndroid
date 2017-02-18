package com.naruto.mobile.adapterdelegate.library.interf;

/**
 * A listener interface that can be added to a {@link DelegateManager} to get notified
 * whenever a Delegate is added to or removed from {@link DelegateManager}.
 */
public interface OnDelegateStateChangeListener<VH> {

    /**
     * Called when a delegate is added to the DelegateManager.
     *
     * @param delegate The Delegate which is added to the DelegateManager
     */
    void onDelegateAdded(Delegate<VH> delegate);

    /**
     * Called when a delegate is removed from DelegateManager.
     *
     * @param delegate The Delegate which is being removed from the DelegateManager
     */
    void onDelegateRemoved(Delegate<VH> delegate);
}
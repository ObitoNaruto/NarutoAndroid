package com.naruto.mobile.adapterdelegate.library.interf;

import android.view.ViewGroup;

import java.util.List;

/**
 * Manager of Delegate. Must call following methods in your adapter:
 * <ul>
 * <li> {@link #bindViewHolder(VH, int, int)}
 * <li> {@link #createViewHolder(ViewGroup, int)}
 * <li> {@link #getItemViewType(int)}
 * </ul>
 * <p>
 *
 * @param <VH> Type of the ViewHolder
 */
public interface DelegateManager<VH> {
    /**
     * invalid view type
     */
    public static final int INVALID_TYPE = -1;

    /**
     * Adds a Delegate to this manager
     *
     * @param delegate Delegate to add
     * @return self
     */
    DelegateManager<VH> addDelegate(Delegate<VH> delegate);

    /**
     * Removes the Delegate in this manager.
     *
     * @param delegate Delegate to remove
     * @return self
     */
    DelegateManager<VH> removeDelegate(Delegate<VH> delegate);

    /**
     * @return a List of Delegate.
     */
    List<Delegate<VH>> getDelegates();

    /**
     * @return number of Delegate.
     */
    int getDelegateCount();

    /**
     * Returns the view type of the item at <code>position</code> for the purposes
     * of view recycling.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * <code>position</code>.
     */
    int getItemViewType(int position);

    /**
     * Called when a new ViewHolder of the given type to represent an item is needed.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #bindViewHolder(VH, int, int)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link android.view.View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #bindViewHolder(VH, int, int)
     */
    VH createViewHolder(ViewGroup parent, int viewType);

    /**
     * Called to display the data at the specified position. This method should
     * update the contents of the ViewHolder to reflect the item at the given
     * position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     * @param viewType The viewType of ViewHolder
     */
    void bindViewHolder(VH holder, int position, int viewType);
}

package com.naruto.mobile.adapterdelegate.library;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.naruto.mobile.R;

/**
 * An {@link BaseAdapter} using View Holder Pattern
 *
 * @param <VH> The type of the ViewHolder
 */
public abstract class ViewHolderAdapter<VH extends ViewHolder> extends BaseAdapter {

    @SuppressWarnings("unchecked")
    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        VH holder;
        if (null == convertView) {
            holder = createViewHolder(parent, getItemViewType(position));
            // convert view set tag
            convertView = holder.itemView;
            convertView.setTag(R.id.tag, holder);
        } else {
            holder = (VH) convertView.getTag(R.id.tag);
        }

        bindViewHolder(holder, position);
        return convertView;
    }

    /**
     * This method calls {@link #onCreateViewHolder(ViewGroup, int)} to create a new
     * {@link ViewHolder} and initializes some fields to be used by ViewHolder.
     *
     * @see #onCreateViewHolder(ViewGroup, int)
     */
    public final VH createViewHolder(final ViewGroup parent, int viewType) {
        final VH holder = onCreateViewHolder(parent, viewType);
        holder.mItemViewType = viewType;
        return holder;
    }

    /**
     * This method internally calls {@link #onBindViewHolder(ViewHolder, int)} to update the
     * {@link ViewHolder} contents with the item at the given position and also sets up some
     * fields to be used by ViewHolder
     *
     * @see #onBindViewHolder(ViewHolder, int)
     */
    public final void bindViewHolder(VH holder, int position) {
        holder.mPosition = position;
        holder.mItemId = getItemId(position);
        onBindViewHolder(holder, position);
    }

    /**
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link android.view.View#findViewById(int)} calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    /**
     * Called to display the data at the specified position. This method should
     * update the contents of the ViewHolder to reflect the item at the given
     * position.
     * <p/>
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    public abstract void onBindViewHolder(VH holder, int position);
}

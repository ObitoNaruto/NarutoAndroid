package com.naruto.mobile.adapterdelegate.library;

import android.view.View;

/**
 * ViewHolder for {@link DelegateBaseAdapter}
 */
public class ViewHolder {
    public static final int NO_POSITION = -1;
    public static final long NO_ID = -1;
    public static final int INVALID_TYPE = -1;

    public final View itemView;

    int mItemViewType = INVALID_TYPE;
    int mPosition = NO_POSITION;
    long mItemId = NO_ID;

    public ViewHolder(View itemView) {
        this.itemView = itemView;
    }

    /**
     * Get the row id associated with the specified position.
     *
     * @return The id of the item at the specified position.
     */
    public final long getItemId() {
        return mItemId;
    }

    /**
     * @return The position of this ViewHolder.
     */
    public final int getPosition() {
        return mPosition;
    }

    /**
     * @return The view type of this ViewHolder.
     */
    public final int getItemViewType() {
        return mItemViewType;
    }
}
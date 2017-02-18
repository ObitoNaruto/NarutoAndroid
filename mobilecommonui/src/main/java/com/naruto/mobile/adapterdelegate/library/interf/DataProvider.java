package com.naruto.mobile.adapterdelegate.library.interf;

/**
 * An interface to provide data<p>
 */
public interface DataProvider {
    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    Object getItem(int position);

    /**
     * Returns the total number of items in the data set hold by the data provider.
     *
     * @return The total number of items in this data provider.
     */
    int getItemCount();
}

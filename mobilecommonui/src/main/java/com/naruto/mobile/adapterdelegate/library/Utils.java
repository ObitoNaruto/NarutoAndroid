package com.naruto.mobile.adapterdelegate.library;

/**
 * Created by benio on 2016/6/4.
 */
public class Utils {
    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference     an object reference
     * @param detailMessage the detail message for this exception
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference, String detailMessage) {
        if (reference == null) {
            throw new NullPointerException(detailMessage);
        }
        return reference;
    }
}

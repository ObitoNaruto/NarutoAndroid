/*
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;

import android.annotation.TargetApi;
import android.graphics.Bitmap;

/**
 * Utility methods for handling Bitmaps.
 */
public class Bitmaps {
    /**
     * The only bitmap config we use. Every bitmap managed by this pool will use this config.
     * If we need to change this, please change BYTES_PER_PIXEL below.
     */
    public static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;

    /**
     * Bytes per pixel - corresponds to the specific BITMAP_CONFIG above ARGB_8888.
     * Must change if the Config above changes
     */
    public static final int BYTES_PER_PIXEL = 4;

    static {
        System.loadLibrary("bitmaps");
    }

    /**
     * Pin the bitmap so that it cannot be 'purged'. Only makes sense for purgeable bitmaps
     * WARNING: Use with caution. Make sure that the pinned bitmap is recycled eventually. Otherwise,
     * this will simply eat up ashmem memory and eventually lead to unfortunate crashes.
     * We *may* eventually provide an unpin method - but we don't yet have a compelling use case for
     * that.
     *
     * @param bitmap the purgeable bitmap to pin
     */
    public static void pinBitmap(Bitmap bitmap) {
        checkNotNull(bitmap);
        nativePinBitmap(bitmap);
    }


    /**
     * This blits the pixel data from src to dest.
     * <p>The destination bitmap must have both a height and a width equal to the source. For maximum
     * speed stride should be equal as well.
     * <p>Both bitmaps must be in {@link Bitmap.Config#ARGB_8888} format.
     * <p>If the src is purgeable, it will be decoded as part of this operation if it was purged.
     * The dest should not be purgeable. If it is, the copy will still take place,
     * but will be lost the next time the dest gets purged, without warning.
     * <p>The dest must be mutable.
     *
     * @param dest Bitmap to copy into
     * @param src  Bitmap to copy out of
     */
    public static void copyBitmap(Bitmap dest, Bitmap src) {
//        checkArgument(src.getConfig() == Bitmap.Config.ARGB_8888);
//        checkArgument(dest.getConfig() == Bitmap.Config.ARGB_8888);
        checkArgument(dest.getConfig()==src.getConfig());
        checkArgument(dest.isMutable());
        checkArgument(dest.getWidth() == src.getWidth());
        checkArgument(dest.getHeight() == src.getHeight());
        nativeCopyBitmap(
                dest,
                dest.getRowBytes(),
                src,
                src.getRowBytes(),
                dest.getHeight());
    }

    /**
     * Reconfigures bitmap after checking its allocation size.
     * <p/>
     * <p> This method is here to overcome our testing framework limit. Robolectric does not provide
     * KitKat specific APIs: {@link Bitmap#reconfigure} and {@link Bitmap#getAllocationByteCount}
     * are part of that.
     */
    @TargetApi(19)
    public static void reconfigureBitmap(Bitmap bitmap, int width, int height) {
        checkArgument(
                bitmap.getAllocationByteCount() >= width * height * BYTES_PER_PIXEL);
        bitmap.reconfigure(width, height, BITMAP_CONFIG);
    }

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    private static native void nativePinBitmap(Bitmap bitmap);

    private static native void nativeCopyBitmap(
            Bitmap dest,
            int destStride,
            Bitmap src,
            int srcStride,
            int rows);
}

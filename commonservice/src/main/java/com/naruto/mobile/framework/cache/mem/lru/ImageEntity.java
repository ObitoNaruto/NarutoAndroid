package com.naruto.mobile.framework.cache.mem.lru;

import android.graphics.Bitmap;

import com.naruto.mobile.framework.cache.mem.Entity;


/**
 * @author sanping.li@alipay.com
 *
 */
public class ImageEntity extends Entity<Bitmap> {
    /**
     * 缓存所占内存的大小
     */
    private int mSize;

    public ImageEntity(String owner, String group, Bitmap value) {
        super(owner, group, value);
        mSize = value.getRowBytes() * value.getHeight();
    }

    /**
     * @return 缓存所占内存的大小
     */
    public int getSize() {
        return mSize;
    }

    @Override
    public String toString() {
        return String.format("value: %s size: %d", mValue.toString(), mSize);
    }
}

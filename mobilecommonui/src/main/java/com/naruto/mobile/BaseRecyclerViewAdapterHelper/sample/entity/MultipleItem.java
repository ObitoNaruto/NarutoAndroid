package com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.entity;


import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.entity.MultiItemEntity;

/**
 */
public class MultipleItem extends MultiItemEntity {
    public static final int TEXT = 1;
    public static final int IMG = 2;
    public static final int IMGS = 3;

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

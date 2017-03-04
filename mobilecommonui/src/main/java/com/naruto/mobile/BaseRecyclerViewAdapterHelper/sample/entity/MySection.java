package com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.entity;


import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.entity.SectionEntity;

/**
 */
public class MySection extends SectionEntity<Video> {
    private boolean isMroe;
    public MySection(boolean isHeader, String header, boolean isMroe) {
        super(isHeader, header);
        this.isMroe = isMroe;
    }

    public MySection(Video t) {
        super(t);
    }

    public boolean isMroe() {
        return isMroe;
    }

    public void setMroe(boolean mroe) {
        isMroe = mroe;
    }
}

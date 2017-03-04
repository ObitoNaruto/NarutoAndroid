package com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.adapter;

import java.util.List;

import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.BaseSectionQuickAdapter;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.BaseViewHolder;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.entity.MySection;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.entity.Video;
import com.naruto.mobile.R;


/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 * activity:SectionUseActivity
 */
public class SectionAdapter extends BaseSectionQuickAdapter<MySection> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param sectionHeadResId The section head layout id for each item
     * @param layoutResId The layout resource id of each item.
     * @param data        A new list is created out of this one to avoid mutable list
     */
    public SectionAdapter( int layoutResId, int sectionHeadResId, List data) {
        super( layoutResId, sectionHeadResId, data);
    }

    /**
     * 样式一，即head样式的布局
     * @param helper
     * @param item
     */
    @Override
    protected void convertHead(BaseViewHolder helper,final MySection item) {
        helper.setText(R.id.header, item.header);
        helper.setVisible(R.id.more,item.isMroe());
        helper.setOnClickListener(R.id.more,new OnItemChildClickListener());//
    }


    /**
     * 普通布局
     * @param helper
     * @param item
     */
    @Override
    protected void convert(BaseViewHolder helper, MySection item) {
        Video video = (Video) item.t;
//        helper.setImageUrl(R.id.iv, video.getImg());
        helper.setText(R.id.tv, video.getName());
    }
}

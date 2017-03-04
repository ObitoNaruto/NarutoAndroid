package com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.adapter;

import android.content.Context;

import java.util.List;

import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.BaseMultiItemQuickAdapter;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.BaseViewHolder;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.entity.MultipleItem;
import com.naruto.mobile.R;


/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 * Activity:MultipleItemUseActivity
 */
public class MultipleItemQuickAdapter extends BaseMultiItemQuickAdapter<MultipleItem> {

    public MultipleItemQuickAdapter(Context context, List data) {
        super( data);
        addItemType(MultipleItem.TEXT, R.layout.item_text_view);
        addItemType(MultipleItem.IMG, R.layout.item_image_view);
        addItemType(MultipleItem.IMGS, R.layout.item_image_views);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultipleItem item) {
        switch (helper.getItemViewType()) {
            case MultipleItem.TEXT://文本类型
                helper.setText(R.id.tv, item.getContent());
                break;
            case MultipleItem.IMG://一个图片
                // set img data
                break;
            case MultipleItem.IMGS://多个图片
                // set imgs data
                break;
        }
    }

}

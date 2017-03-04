package com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.adapter;

import android.animation.Animator;
import android.graphics.Color;
import android.support.v7.widget.CardView;

import java.util.List;

import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.BaseQuickAdapter;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.BaseViewHolder;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.entity.HomeItem;
import com.naruto.mobile.R;


/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper\
 * Activity:HomeActivity
 */
public class HomeAdapter extends BaseQuickAdapter<HomeItem> {
    public HomeAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    /**
     * 动画设置
     * @param anim
     * @param index
     */
    @Override
    protected void startAnim(Animator anim, int index) {
        super.startAnim(anim, index);
        if (index < 5)
        anim.setStartDelay(index * 150);//设置动画延时
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeItem item) {
        helper.setText(R.id.info_text, item.getTitle());//设置文案
        CardView cardView = helper.getView(R.id.card_view);
        cardView.setCardBackgroundColor(Color.parseColor(item.getColorStr()));//设置CardView的背景颜色
    }
}

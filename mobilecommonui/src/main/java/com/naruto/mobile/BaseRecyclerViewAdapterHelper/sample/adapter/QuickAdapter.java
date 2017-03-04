package com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.BaseQuickAdapter;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.BaseViewHolder;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.data.DataServer;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.entity.Status;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.transform.GlideCircleTransform;
import com.naruto.mobile.R;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 * 所属的activity:AnimationUseActivity,HeaderAndFooterUseActivity,PullToRefreshUseActivity,EmptyViewUseActivity
 */
public class QuickAdapter extends BaseQuickAdapter<Status> {
    public QuickAdapter() {
        super( R.layout.tweet, DataServer.getSampleData(100));
    }

    public QuickAdapter(Context context, int dataSize) {
        super( R.layout.tweet, DataServer.getSampleData(dataSize));
    }

    @Override
    protected void convert(BaseViewHolder helper, Status item) {
        helper.setText(R.id.tweetName, item.getUserName())
                .setText(R.id.tweetText, item.getText())
                .setText(R.id.tweetDate, item.getCreatedAt())
                .setVisible(R.id.tweetRT, item.isRetweet())
                .setOnClickListener(R.id.tweetAvatar, new BaseQuickAdapter.OnItemChildClickListener())//给R.id.tweetAvatar这个view添加监听
                .setOnClickListener(R.id.tweetName, new OnItemChildClickListener())
                .linkify(R.id.tweetText);
        Glide.
                with(mContext).
                load(item.getUserAvatar()).
                crossFade().
                placeholder(R.mipmap.def_head).
                transform(new GlideCircleTransform(mContext)).
                into((ImageView) helper.getView(R.id.tweetAvatar));
    }


}


package com.naruto.mobile.h5container.refresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.naruto.mobile.h5container.R;
import com.naruto.mobile.h5container.env.H5Environment;

public class H5PullHeader extends RelativeLayout {
    public static final String TAG = "H5PullHeader";
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private ProgressBar pbLoading;
    private TextView tvTitle;
    private TextView tvSummary;

    public H5PullHeader(Context context) {
        super(context);
    }

    public H5PullHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getId() == NO_ID) {
            throw new RuntimeException("must set id");
        }

        pbLoading = (ProgressBar) findViewById(R.id.pullrefresh_progress);
        tvTitle = (TextView) findViewById(R.id.pullrefresh_title);
        tvSummary = (TextView) findViewById(R.id.pullrefresh_summary);
        setLastRefresh();
    }

    public void showOpen() {
        pbLoading.setVisibility(View.INVISIBLE);
        tvTitle.setText(R.string.pull_can_refresh);
    }

    public void showOver() {
        tvTitle.setText(R.string.release_to_refresh);
    }

    public void showLoading() {
        tvTitle.setText(R.string.refreshing);
        pbLoading.setVisibility(View.VISIBLE);
    }

    public void showFinish() {
        pbLoading.setVisibility(View.INVISIBLE);
        setLastRefresh();
    }

    @SuppressLint("SimpleDateFormat")
    private void setLastRefresh() {
        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        Date date = new Date(time);
        String formatedTime = sdf.format(date);
        formatedTime = H5Environment.getResources().getString(
                R.string.last_refresh, formatedTime);
        tvSummary.setText(formatedTime);
    }
}

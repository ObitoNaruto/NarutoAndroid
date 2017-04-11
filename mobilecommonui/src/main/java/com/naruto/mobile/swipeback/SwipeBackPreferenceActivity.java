package com.naruto.mobile.swipeback;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;

/**
 */
public class SwipeBackPreferenceActivity extends PreferenceActivity implements ISwipeLayoutExtension {

    private SwipeBackLayoutHelper mSwipeBackHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSwipeBackHelper = SwipeBackLayoutHelper.create(this);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        getSwipeBackLayout().attachToActivity(this);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public View findViewById(int id) {
        View view = super.findViewById(id);
        if (view == null && mSwipeBackHelper != null) {
            view = getSwipeBackLayout().findViewById(id);
        }
        return view;
    }

    @Override
    public void setSwipeBackEnabled(boolean isEnabled) {
        getSwipeBackLayout().setEnableGesture(isEnabled);
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackHelper.getSwipeBackLayout();
    }
}

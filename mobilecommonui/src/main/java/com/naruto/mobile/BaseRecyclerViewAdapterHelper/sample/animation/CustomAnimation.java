package com.naruto.mobile.BaseRecyclerViewAdapterHelper.sample.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.animation.BaseAnimation;


/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class CustomAnimation implements BaseAnimation {

    @Override
    public Animator[] getAnimators(View view) {
        return new Animator[]{
                ObjectAnimator.ofFloat(view, "scaleY", 1, 1.1f, 1),
                ObjectAnimator.ofFloat(view, "scaleX", 1, 1.1f, 1)
        };
    }
}

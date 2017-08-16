package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.listener;

import android.support.v4.view.ViewPager;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadEngine;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

public class PauseOnPageChangeListener implements ViewPager.OnPageChangeListener {

    private static final String TAG = PauseOnPageChangeListener.class.getSimpleName();
    private Logger logger = Logger.getLogger(TAG);

    private final ImageLoadEngine loadEngine;
    private final boolean pauseOnScroll;
    private final ViewPager.OnPageChangeListener externalListener;

    public PauseOnPageChangeListener(ImageLoadEngine loadEngine, boolean pauseOnScroll,
            ViewPager.OnPageChangeListener customListener) {
        this.loadEngine = loadEngine;
        this.pauseOnScroll = pauseOnScroll;
        externalListener = customListener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (externalListener != null) {
            externalListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (externalListener != null) {
            externalListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        logger.d("onPageScrollStateChanged " + state);
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                loadEngine.resume();
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                if (pauseOnScroll) {
                    loadEngine.pause();
                }
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                break;
        }
        if (externalListener != null) {
            externalListener.onPageScrollStateChanged(state);
        }
    }
}

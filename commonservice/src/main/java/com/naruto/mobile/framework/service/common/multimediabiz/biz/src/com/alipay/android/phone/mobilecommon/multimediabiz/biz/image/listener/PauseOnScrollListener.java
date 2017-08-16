package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.listener;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.ImageLoadEngine;

public class PauseOnScrollListener implements OnScrollListener {

	private final ImageLoadEngine loadEngine;
	private final boolean pauseOnScroll;
	private final boolean pauseOnFling;
	private final OnScrollListener externalListener;

	public PauseOnScrollListener(ImageLoadEngine loadEngine, boolean pauseOnScroll, boolean pauseOnFling) {
		this(loadEngine, pauseOnScroll, pauseOnFling, null);
	}

	public PauseOnScrollListener(ImageLoadEngine loadEngine, boolean pauseOnScroll, boolean pauseOnFling,
			OnScrollListener customListener) {
		this.loadEngine = loadEngine;
		this.pauseOnScroll = pauseOnScroll;
		this.pauseOnFling = pauseOnFling;
		externalListener = customListener;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				loadEngine.resume();
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				if (pauseOnScroll) {
					loadEngine.pause();
				}
				break;
			case OnScrollListener.SCROLL_STATE_FLING:
				if (pauseOnFling) {
					loadEngine.pause();
				}
				break;
		}
		if (externalListener != null) {
			externalListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (externalListener != null) {
			externalListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}
}
package com.naruto.mobile.applauncher.core;

import android.view.KeyEvent;

import com.naruto.mobile.base.serviceaop.app.ActivityApplication;


/**
 * Interface definition for a fragmentWidgetGroup
 */
public interface IFragmentWidgetGroup extends IWidgetGroup {
	void setActApplication(ActivityApplication app);
	public boolean onKeyDown(int keyCode, KeyEvent event);
}

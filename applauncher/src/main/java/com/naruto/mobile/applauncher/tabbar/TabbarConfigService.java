package com.naruto.mobile.applauncher.tabbar;

import android.os.Bundle;

import com.alipay.mobile.framework.service.ext.ExternalService;

public abstract class TabbarConfigService extends ExternalService{

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy(Bundle arg0) {
		// TODO Auto-generated method stub

	}

	public abstract TabbarConfigModel getTabbarConfig(int tabIndex);

}

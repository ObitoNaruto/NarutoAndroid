package com.naruto.mobile.applauncher.tabbar;

import com.alipay.mobile.common.logging.api.LoggerFactory;
import com.alipay.mobile.common.logging.api.behavor.Behavor;

public class LoggerUtils {

	public static void tabbarConfigLog(String biz) {
		String seedId = "tabbar_config";
		String userCaseId = "tabbar_config_20151229";
		Behavor behavor = new Behavor();
		behavor.setSeedID(seedId);
		behavor.setUserCaseID(userCaseId);
		behavor.setParam1(biz);
		LoggerFactory.getBehavorLogger().click(behavor);
	}
}

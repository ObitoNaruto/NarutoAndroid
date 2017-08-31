package com.naruto.mobile.applauncher.core;

import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.naruto.mobile.applauncher.tabbar.TabbarGetter;

public class IBaseWidgetGroup {

	public interface TabLauncherViewGetter {

		/**
		 * 获取TabHost
		 * **/
		TabHost getTabHost();

		/**
		 * 获取TabWidget
		 * **/
		TabWidget getTabWidget();

		/**
		 * 获取TitleBar
		 * **/
		RelativeLayout getTitleBar();
		
		/**
		 * 获取父布局
		 * **/
		RelativeLayout getLauncherParent();

	}
	
	public interface TabLauncherController {

		void dochangeContentTab(String tab, String tabId, String bundleName,
                                String className);
	}
	
	public static TabbarGetter tabbarGetter = null;
	public static TabLauncherController tabLauncherController = null;
	
	public static TabLauncherViewGetter tabLauncherViewGetter = null;
	
	private APTitleBar titlebar;

	/**
	 * 获取Tablauncher的titleBar对象
	 * **/
	@Deprecated
	APTitleBar getTitleBar() {
		return titlebar;
	}
	
	public static TabLauncherViewGetter getTabLauncherViewGetter() {
		return tabLauncherViewGetter;
	}

	public static void setTabLauncherViewGetter(
			TabLauncherViewGetter tabLauncherViewGetter) {
		IBaseWidgetGroup.tabLauncherViewGetter = tabLauncherViewGetter;
	}
	
	public static TabbarGetter getTabbarGetter() {
		return tabbarGetter;
	}
	
	/**
	 * @param tab 4个tab序号（0，1，2，3）
	 * @param tabId tab的appid
	 * @param bundleName 实现类的bundleName
	 * @param 实现类的类名
	 * **/
	public static void changeContentTab(String tab, String tabId,
			String bundleName, String className) {
		if (tabLauncherController != null) {
			tabLauncherController.dochangeContentTab(tab, tabId, bundleName,
					className);
		}
	}
	
	public static void setTabbarGetter(TabbarGetter tabbarGetter) {
		IBaseWidgetGroup.tabbarGetter = tabbarGetter;
	}

	public static int getCurrentTab() {
		if (tabbarGetter != null) {
			return tabbarGetter.getCurrentTab();
		}
		return 0;
	}

	public void onRefreshIndicator(){//NOPMD
	}

	public static TabLauncherController getTabLauncherController() {
		return tabLauncherController;
	}

	public static void setTabLauncherController(
			TabLauncherController tabLauncherController) {
		IBaseWidgetGroup.tabLauncherController = tabLauncherController;
	}

	/**
	 * 页面回调,表示该IBaseWidgetGroup初始化并完成首次onGlobalLayout
	 */
	public void onLaunchFinish() {
//		LoggerFactory.getTraceLogger().info("IBaseWidgetGroup", "onLoad");
	}
	
}

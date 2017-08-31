package com.naruto.mobile.applauncher.core;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TabHost;


public class TitleBarItemView extends RelativeLayout {
	
	private int badgeNum;
	private String title;
	private int iconResource;
	private Context mContext;//NOPMD
	
	public TitleBarItemView(Context context) {
		super(context);
		mContext = context;
	}

	public TitleBarItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	public TitleBarItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public int getBadgeNum() {
		return badgeNum;
	}

	public void setBadgeNum(int badgeNum) {
		this.badgeNum = badgeNum;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIconResource() {
		return iconResource;
	}

	public void setIconResource(int iconResource) {
		this.iconResource = iconResource;
	}
	
	public int getCurrentTab() {
		int tab = 0;
		try {
			TabHost tabHost = (TabHost) this.getRootView().findViewById(
					android.R.id.tabhost);
			tab = tabHost.getCurrentTab();
		} catch (Throwable e) {
//			LoggerFactory.getTraceLogger().error("TitleBarItemView", e);
		}
//		LoggerFactory.getTraceLogger().info("TitleBarItemView",
//				"current tab = " + tab);
		return tab;
	}
	
	

}

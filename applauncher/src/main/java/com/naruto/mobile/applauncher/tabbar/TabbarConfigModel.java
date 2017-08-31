package com.naruto.mobile.applauncher.tabbar;

import android.graphics.drawable.Drawable;

public class TabbarConfigModel {
	public boolean success = false;

	private String tabName;//NOPMD
	private Drawable selectedImage;
	private Drawable defaultImage;
	private String selectTitleColor;
	private String defaultTitleColor;

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	public Drawable getSelectedImage() {
		return selectedImage;
	}
	public void setSelectedImage(Drawable selectedImage) {
		this.selectedImage = selectedImage;
	}
	public Drawable getDefaultImage() {
		return defaultImage;
	}
	public void setDefaultImage(Drawable defaultImage) {
		this.defaultImage = defaultImage;
	}
	public String getSelectTitleColor() {
		return selectTitleColor;
	}
	public void setSelectTitleColor(String selectTitleColor) {
		this.selectTitleColor = selectTitleColor;
	}
	public String getDefaultTitleColor() {
		return defaultTitleColor;
	}
	public void setDefaultTitleColor(String defaultTitleColor) {
		this.defaultTitleColor = defaultTitleColor;
	}
}

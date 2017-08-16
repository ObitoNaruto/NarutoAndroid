package com.naruto.mobile.framework.service.common.multimedia.graphics;

import android.graphics.drawable.Drawable;

/**
 * 下载可选项
 * 
 * @author xiaofeng.dxf
 * 
 */
public class APImageDownloadOption
{
	/**
	 * x尺寸
	 */
	private int image_x;
	/**
	 * y尺寸
	 */
	private int image_y;
	/**
	 * 图片类型
	 */
	private IMGTYPE imgType;

    /**
     * 用于下载前或下载失败展示的默认图片
     */
    private Drawable defaultDrawable;

    /**
     * 使用何种方式load图片
     */
    private int loadType;


    /**
     * 同步下载或者异步方式下载
     */
    private boolean isSyncLoading;

	/**
	 * 自定义渲染
	 */
	private APDisplayer displayer;

	public boolean isBackground() {
		return isBackground;
	}

	public void setIsBackground(boolean isBackground) {
		this.isBackground = isBackground;
	}

	private boolean isBackground;

    public void setSyncLoading(boolean isSyncLoading) {
        this.isSyncLoading = isSyncLoading;
    }

    boolean isSyncLoading() {
        return isSyncLoading;
    }

    public Drawable getDefaultDrawable() {

        return defaultDrawable;
    }

    public void setDefaultDrawable(Drawable defaultDrawable) {
        this.defaultDrawable = defaultDrawable;
    }

    public int getImage_x()
	{
		return image_x;
	}

	public void setImage_x(int image_x)
	{
		this.image_x = image_x;
	}

	public int getImage_y()
	{
		return image_y;
	}

	public void setImage_y(int image_y)
	{
		this.image_y = image_y;
	}

	public IMGTYPE getImgType()
	{
		return imgType;
	}

	public void setImgType(IMGTYPE imgType)
	{
		this.imgType = imgType;
	}

    public int getLoadType() {
        return loadType;
    }

    public void setLoadType(int loadType) {
        this.loadType = loadType;
    }

	public APDisplayer getDisplayer() {
		return displayer;
	}

	public void setDisplayer(APDisplayer displayer) {
		this.displayer = displayer;
	}

    /**
	 * 图片类型
	 * 
	 * @author xiaofeng.dxf
	 * 
	 */
	public enum IMGTYPE
	{
		JPEG, WEBP
	}
}

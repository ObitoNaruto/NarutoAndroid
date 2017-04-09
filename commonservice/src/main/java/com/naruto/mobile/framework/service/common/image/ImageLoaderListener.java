package com.naruto.mobile.framework.service.common.image;

import android.graphics.Bitmap;

/**
 * 图片加载通知
 */
public interface ImageLoaderListener{
    /**
     * 取消
     *
     * @param path 路径
     */
    public void onCancelled(String path);

    /**
     * 预加载
     *
     * @param path 路径
     */
    public void onPreLoad(String path);

    /**
     * 加载完成
     *
     * @param path 路径
     * @param response 响应对象
     */
    public void onPostLoad(String path, Bitmap bitmap);

    /**
     * 加载中的进度
     *
     * @param path 路径
     * @param percent 进度
     */
    public void onProgressUpdate(String path, double percent);

    /**
     * 加载失败
     *
     * @param path 路径
     * @param code 错误码
     * @param msg 错误消息
     */
    public void onFailed(String path, int code, String msg);
}
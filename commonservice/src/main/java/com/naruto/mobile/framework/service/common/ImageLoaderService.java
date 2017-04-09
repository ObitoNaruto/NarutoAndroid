
package com.naruto.mobile.framework.service.common;

import android.graphics.Bitmap;

import com.naruto.mobile.base.serviceaop.service.CommonService;
import com.naruto.mobile.framework.service.common.image.ImageCacheListener;
import com.naruto.mobile.framework.service.common.image.ImageLoaderListener;

public abstract class ImageLoaderService extends CommonService {
    /**
     * 开始加载
     *
     * @param owner 所用者：g+gid或者u+uid,比如:u10000023213
     * @param group 加载图片缓存所在组
     * @param path 路径，http:// or file://等
     * @param listener 回调
     * @param width 目标图片的宽，负数则表示加载图片原始大小
     * @param height 目标图片的高，负数则表示加载图片原始大小
     */
    public abstract void startLoad(String owner, String group, String path,
            ImageLoaderListener listener, int width, int height);
    /**
     * 从缓存中加载
     *
     * 本地IO比较耗时，建议使用异步的startLoad
     *
     * @param owner 所用者：g+gid或者u+uid,比如:u10000023213
     * @param group 加载图片缓存所在组
     * @param path 路径，http://xxx
     * @param width 目标图片的宽，负数则表示加载图片原始大小
     * @param height 目标图片的高，负数则表示加载图片原始大小
     */
    public abstract Bitmap loadFromCache(String owner, String group, String path, int width, int height);

    /**
     * 开始加载
     *
     * @param owner 所用者：g+gid或者u+uid,比如:u10000023213
     * @param group 加载图片缓存所在组
     * @param path 路径，http:// or file://等
     * @param listener 回调
     * @param width 目标图片的宽，负数则表示加载图片原始大小
     * @param height 目标图片的高，负数则表示加载图片原始大小
     */
    public abstract void startLoad(String owner, String group, String path,
            ImageLoaderListener listener, int width, int height,ImageCacheListener imageCacheListener);

    /**
     * 取消下载
     *
     * @param path
     * @param listener
     */
    public abstract void cancel(String path,ImageLoaderListener listener);
}

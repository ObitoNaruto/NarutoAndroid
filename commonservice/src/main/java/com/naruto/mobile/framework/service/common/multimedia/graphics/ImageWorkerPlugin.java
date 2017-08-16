
package com.naruto.mobile.framework.service.common.multimedia.graphics;

import android.graphics.Bitmap;

import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;

/***
 * 图片处理插件
 */
public interface ImageWorkerPlugin {

    /**
     * 用于内存缓存的key
     * @return
     */
    public String getPluginKey();
    /***
     * 图片存储的地址
     * @return
     */
//    public String getSavePath(String srcPath);

    /**
     * 图片加载后的特殊任务处理，如裁剪、圆形图片、圆角图片等
     * @param task
     * @param srcBitmap
     * @return
     */
    public Bitmap process(APMultimediaTaskModel task, Bitmap srcBitmap);

}

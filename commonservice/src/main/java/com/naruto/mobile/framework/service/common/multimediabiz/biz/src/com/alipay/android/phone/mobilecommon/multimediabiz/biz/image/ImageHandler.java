/**
 *
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image;

import android.content.Context;
import org.apache.http.client.HttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

import com.naruto.mobile.framework.service.common.multimedia.graphics.ImageWorkerPlugin;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.CutScaleType;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.BitmapCacheLoader;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.cache.disc.ImageCacheManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.impl.HttpConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.impl.HttpDjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.FalconFacade;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.manager.APMultimediaTaskManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.service.graphics.APImageManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.service.graphics.APImageWorker;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CacheUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * @author zhenghui
 */
public abstract class ImageHandler<V> implements Callable<V> {

    static final String TAG = ImageHandler.class.getSimpleName();

    // ========以下重要参数需要应用自己进行加密隐藏========
    private static final String APP_KEY = "aliwallet";
    // ================================================
    //

    private DjangoClient djangoClient;
    public String mLocalId;
    public String mCloudId;
    protected Context mContext;
    ImageLoadEngine engine = ImageLoadEngine.getInstance();
    BitmapCacheLoader cacheLoader = null;
    CutScaleType mCutScaleType = CutScaleType.KEEP_RATIO;

    protected ImageCacheManager mImageCacheManager;

    private Logger logger = Logger.getLogger(TAG);

    private FalconFacade mFalconFacade;

    public ImageHandler() {

    }

    public ImageHandler(Context context) {
        //TODO 是否可能会造成activity泄露
        mContext = context;
        cacheLoader = engine.getCacheLoader();
        mFalconFacade = FalconFacade.get();
        mImageCacheManager = APImageWorker.getInstance(context).getImageCacheManager();
    }


    public String formatCacheKey(ImageWorkerPlugin plugin, String path, int width, int height,
            CutScaleType cutScaleType) {
        return CacheUtils.makeImageCacheKey(plugin, path, width, height, cutScaleType, null);
    }

    /**
     * @param imageFile
     * @param quality   压缩质量，0-低质量，1-中质量，>=2-高质量
     * @param width
     * @param height
     * @return
     * @throws IOException
     */
    public ByteArrayOutputStream compressImage(File imageFile, int quality, int width, int height) throws IOException {
        return mFalconFacade.compressImage(imageFile, quality, width, height);
    }

    /**
     * @param imageData
     * @param quality   压缩质量，0-低质量，1-中质量，>=2-高质量
     * @param width
     * @param height
     * @return
     * @throws IOException
     */
    public ByteArrayOutputStream compressImage(byte[] imageData, int quality, int width, int height)
            throws IOException {
        return mFalconFacade.compressImage(imageData, quality, width, height);
    }




    protected APImageManager getImageManager() {
        return APImageManager.getInstance(mContext);
    }

    protected synchronized DjangoClient getDjangoClient() {
        if (djangoClient == null || djangoClient.getConnectionManager().isShutdown()) {
            ConnectionManager<HttpClient> conMgr = new HttpConnectionManager();
            conMgr.setAppKey(APP_KEY);
//            conMgr.setAppSecret(APP_SECRET);
            djangoClient = new HttpDjangoClient(mContext, conMgr);
        }
        return djangoClient;
    }

    //将网络任务根据key值加入到map中
    protected void putNetTaskTag(String key, String value) {
        APImageManager.getInstance(mContext).putLoadlingTaskTag(key, value);
    }

    public void removeNetTaskTag(String key) {
        APImageManager.getInstance(mContext).removeLoadingTaskTag(key);
    }

    protected void removeUploadCallBack(String taskId) {
        APImageManager.getInstance(mContext).unregistUploadCallback(taskId);
    }

    protected void removeTaskModel(String taskId) {
        APMultimediaTaskManager.getInstance(mContext).removeTaskRecord(taskId);
    }

}

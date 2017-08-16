package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;

import android.text.TextUtils;

import java.io.File;

import com.naruto.mobile.framework.service.common.multimedia.graphics.ImageWorkerPlugin;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageMarkRequest;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.CutScaleType;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.MD5Utils;

/**
 * 缓存工具
 * Created by jinmin on 15/5/22.
 */
public class CacheUtils {

    private static final String TAG = "CacheUtils";

    private static final int CACHE_KEY_PARTS = 5;

    public static final String CACHE_KEY_SEPARATOR = "##";
    public static final String IMAGE_CACHE_FORMAT = "%s"+CACHE_KEY_SEPARATOR+"%d"+
                                                CACHE_KEY_SEPARATOR+"%d"+CACHE_KEY_SEPARATOR+"%s";
    public static final String IMAGE_CACHE_SIZE_FORMAT = "%d" + CACHE_KEY_SEPARATOR + "%d";

    //************** 水印参数 start ***************//
    public static final String CACHE_MARK_SEPARATOR = "@@";
    public static final String CACHE_MARK_PREFIX = "mark" + CACHE_MARK_SEPARATOR;
    //************** 水印参数 end *****************//

    public static String makeImageCacheKey(ImageWorkerPlugin plugin, String path, int width, int height,
            CutScaleType type, APImageMarkRequest imageMarkRequest) {
//        long start = System.currentTimeMillis();
        StringBuffer sb = new StringBuffer();
        sb.append(path).append(CACHE_KEY_SEPARATOR).append(width).append(CACHE_KEY_SEPARATOR).append(height)
                        .append(CACHE_KEY_SEPARATOR).append(type);
//        String key = String.format(IMAGE_CACHE_FORMAT, path, width, height, type);
        if (plugin != null && !TextUtils.isEmpty(plugin.getPluginKey())) {
//            key += (CACHE_KEY_SEPARATOR + plugin.getPluginKey());
            sb.append(CACHE_KEY_SEPARATOR).append(plugin.getPluginKey());
        }
        if (imageMarkRequest != null && MarkUtil.isValidMarkRequest(imageMarkRequest)) {
            if (plugin == null || TextUtils.isEmpty(plugin.getPluginKey())) {
                sb.append(CACHE_KEY_SEPARATOR).append("no_plugin");
            }
            sb.append(CACHE_KEY_SEPARATOR).append(CACHE_MARK_PREFIX).append(imageMarkRequest.getMarkId())
                    .append(CACHE_MARK_SEPARATOR).append(imageMarkRequest.getPosition())
                    .append(CACHE_MARK_SEPARATOR).append(imageMarkRequest.getTransparency())
                    .append(CACHE_MARK_SEPARATOR).append(imageMarkRequest.getMarkWidth())
                    .append(CACHE_MARK_SEPARATOR).append(imageMarkRequest.getMarkHeight())
                    .append(CACHE_MARK_SEPARATOR).append(imageMarkRequest.getPaddingX())
                    .append(CACHE_MARK_SEPARATOR).append(imageMarkRequest.getPaddingY())
                    .append(CACHE_MARK_SEPARATOR).append(imageMarkRequest.getPercent());
        }
//        Logger.D(TAG, "makeImageCacheKey usedTime: " + (System.currentTimeMillis()-start));
//        return key;
        return sb.toString();
    }


    public static boolean checkCacheFile(String path) {
        return FileUtils.checkFile(path);
    }

    public static boolean checkCacheFile(File file) {
        return FileUtils.checkFile(file);
    }

    public static String makeCacheFileName(String input) {
        if (!TextUtils.isEmpty(input)) {
            return MD5Utils.getMD5String(input);
        }
        throw new NullPointerException("input is null");
    }

    /**
     * 分割cacheKey
     * @param input
     * @return String[4]  0:path 1:width 2:height 3:pluginId
     */
    public static String[] splitCacheKey(String input) {
        String[] out = null;
        if (!TextUtils.isEmpty(input)) {
            out = new String[CACHE_KEY_PARTS];
            String[] tmp = input.split(CACHE_KEY_SEPARATOR, CACHE_KEY_PARTS);
            for (int i = 0; i < tmp.length && i < out.length; i++) {
                out[i] = tmp[i];
            }
        }
        return out;
    }

    /**
     * 通过CacheKey获取基本路径
     * @param input     cache key
     * @return
     */
    public static String getBaseCachePath(String input) {
        String[] parts = splitCacheKey(input);
        return parts == null ? input : parts[0];
    }
}

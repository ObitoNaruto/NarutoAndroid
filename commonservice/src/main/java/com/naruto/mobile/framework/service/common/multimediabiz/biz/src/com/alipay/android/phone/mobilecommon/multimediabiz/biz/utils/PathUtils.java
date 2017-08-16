package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils;

import android.net.Uri;
import android.text.TextUtils;

import java.io.File;

import com.naruto.mobile.framework.service.common.multimedia.graphics.data.Size;

/**
 * Path 工具类
 * Created by jinmin on 15/5/14.
 */
public class PathUtils {
    private static final String TAG = "PathUtils";

    public final static String ASSET_SCHEMA = "file:///[asset]/";
    private static final String ASSET_PATH_FLAG = File.separator + "[asset]" + File.separator;

    /**
     * 提取请求路径
     *
     * @param path
     * @return
     */
    public static String extractPath(String path) {
        if (!TextUtils.isEmpty(path)) {
            //获取scheme类型
            switch (Scheme.ofUri(path)) {
                case FILE:
                    path = Scheme.FILE.crop(path);
                    if (!TextUtils.isEmpty(path) && path.startsWith(ASSET_PATH_FLAG)) {
                        path = path.substring(ASSET_PATH_FLAG.length());
                    }
                    break;
                case HTTP:
                case HTTPS:
                case CONTENT:
                case DRAWABLE:
                case ASSETS:
                case UNKNOWN:
                default:
                    break;
            }
        }
        return path;
    }

    /**
     * 提取文件
     *
     * @param path
     * @return
     */
    public static File extractFile(String path) {
        File file = null;
        if (!TextUtils.isEmpty(path)) {
            try {
                Uri uri = Uri.parse(path);
                if (isLocalFile(uri)) {
                    file = new File(extractPath(path));
                } else if (!isHttp(uri)) {
                    file = new File(path);
                }
            } catch (Exception e) {
                file = new File(path);
            }
        }
        if (file != null && (!file.exists() || !file.isFile())) {
            file = null;
        }
        return file;
    }

    public static boolean isLocalFile(Uri uri) {
        String scheme = uri.getScheme();
        return "file".equalsIgnoreCase(scheme) && !hasHost(uri);
    }

    public static boolean isHttp(Uri uri) {
        String scheme = uri.getScheme();
        return ("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme)) && hasHost(uri);
    }

    public static boolean hasHost(Uri uri) {
        String host = uri.getHost();
        return host != null && !"".equals(host);
    }

    public static boolean isAlipayAssetsFile(String input) {
        if (!TextUtils.isEmpty(input)) {
            return input.startsWith(ASSET_SCHEMA);
        }
        return false;
    }

    /*******************************
     * 扩展支持TFS图片规则
     *********************************************/
    public static String preferImageUrl(String orignUrl, int width, int height) {
        if (TextUtils.isEmpty(orignUrl)) {
            return orignUrl;
        }
        String url = orignUrl;
        if (orignUrl.contains("[imgWidth]")) {
            if (width < 0 || height < 0) {
                Logger.W(TAG, "width<0||height<0");
            }
            Size size = getTfsNearestImageSize(new Size(width, height));

            url = orignUrl.replace("[imgWidth]", size.getWidth() + "");
            url = url.replace("[imgHeight]", size.getHeight() + "");
        } else if (orignUrl.contains("[pixelWidth]")) {
            if (width < 0) {
                Logger.W(TAG, "width<0");
            }
            url = orignUrl.replace("[pixelWidth]", width + "");
            if (orignUrl.contains("[pixelHeight]")) {
                if (height < 0) {
                    Logger.W(TAG, "height<0");
                }
                url = url.replace("[pixelHeight]", height + "");
            }
        }

        return url;
    }

    /**
     * TFS可供选择的尺寸
     */
    private static int TFS_IMAGE_SIZE[][] = {
            // Width    Height
            { 40, 40 }, { 80, 80 }, { 160, 160 },
            { -1, -1 } //结尾标志 不能删
    };

    /**
     * django可供选择的尺寸
     */
    private static final int DJANGO_IMAGE_SIZE[][] = {
            // Width    Height
            { 16, 16 }, { 24, 24 }, { 32, 32 },
            { 40, 40 }, { 50, 50 }, { 60, 60 },
            { 72, 72 }, { 80, 80 }, { 90, 90 },

            { 100, 100 }, { 110, 110 }, { 120, 120 },
            { 130, 130 }, { 140, 140 }, { 150, 150 },
            { 160, 160 }, { 170, 170 }, { 180, 180 },
            { 190, 190 },

            { 200, 200 }, { 220, 220 }, { 240, 240 },
            { 250, 250 }, { 270, 270 }, { 290, 290 },

            { 300, 300 }, { 312, 312 }, { 320, 320 },
            { 360, 360 }, { 375, 375 },

            { 400, 400 }, { 430, 430 }, { 460, 460 },
            { 480, 480 },

            { 540, 540 }, { 560, 560 }, { 580, 580 },

            { 600, 600 }, { 640, 640 }, { 670, 670 },

            { 720, 720 }, { 760, 760 },

            { 960, 960 }, { 1136, 1136 }, { 1280, 1280 },

            { -1, -1 } //结尾标志 不能删
    };

    public static Size getTfsNearestImageSize(Size size) {
        return getNearestImageSize(size, TFS_IMAGE_SIZE);
    }

    public static Size getDjangoNearestImageSize(Size size) {
        return getNearestImageSize(size, DJANGO_IMAGE_SIZE);
    }

    /**
     * 获取最接近尺寸的URL，计算方差最小的尺寸
     *
     * @param size
     * @return
     */
    private static Size getNearestImageSize(final Size size, final int[][] sizeList) {
        int width = size.getWidth();
        int height = size.getHeight();

        int variancePre = Integer.MAX_VALUE;
        int varianceCur;
        int index = 0;
        while (sizeList[index][0] > 0) {
            varianceCur = (sizeList[index][0] - size.getWidth())
                    * (sizeList[index][0] - size.getWidth())
                    + (sizeList[index][1] - size.getHeight())
                    * (sizeList[index][1] - size.getHeight());
            if (varianceCur < variancePre) {
                variancePre = varianceCur;
                width = sizeList[index][0];
                height = sizeList[index][1];
            }
            index++;
        }

        return new Size(width, height);
    }
}

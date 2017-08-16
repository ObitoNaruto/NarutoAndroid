/**
 *
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
//import com.alipay.mobile.common.transport.utils.NetworkUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.naruto.mobile.framework.rpc.myhttp.utils.NetworkUtils;
import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.file.APFileUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileReq;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileUploadRsp;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APImageUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.graphics.APImageUploadOption;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageRetMsg;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageUpRequest;
import com.naruto.mobile.framework.service.common.multimedia.graphics.data.APImageUploadRsp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.SetExtReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.SetExtResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.MD5Utils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file.FileUpLoadManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.file.Options;
//import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.image.assist.FalconFacade;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.manager.APMultimediaTaskManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CommonUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.PathUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.UCLogUtil;

/**
 * @author zhenghui
 */
public class ImageUpHandler extends ImageHandler<Void> {

    public final static int TYPE_FILE = 0;
    public final static int TYPE_DATA = 1;
    private static final String TAG = "ImageUpHandler";
    private int progressValue;
    private long totalSize;
    private String mFilePath;
    private File mImageFile;
    private byte[] mFileData;
    private File toUploadFile;
    //    private InputStream imageInputStream;
    private Map<APImageUploadCallback, Object> callbacks;
    private APImageUploadOption uploadOption;
    private APMultimediaTaskModel taskStatus;
    private int upType = -1;
    private int mProgress = -1;
    private APImageRetMsg.RETCODE mCode = APImageRetMsg.RETCODE.UPLOAD_ERROR;

    private Logger logger = Logger.getLogger(TAG);

    private int bRapid = 0;
    private int co = 0;

    /**
     * @param context
     * @param filePath
     * @param cb
     * @param option
     * @param taskStatus
     */
    public ImageUpHandler(Context context, String filePath, APImageUploadCallback cb, APImageUploadOption option,
            APMultimediaTaskModel taskStatus) {
        super(context);
        mFilePath = filePath;
        callbacks = getImageManager().getUpTaskCallback(taskStatus.getTaskId());
        uploadOption = option;
        if (taskStatus == null) {
            taskStatus = new APMultimediaTaskModel();
            taskStatus.setCreatTime(System.currentTimeMillis());
        }
        this.taskStatus = taskStatus;
        this.taskStatus.setSourcePath(filePath);
        upType = TYPE_FILE;

    }

    /**
     * @param context
     * @param fileData
     * @param cb
     * @param option
     * @param taskStatus
     */
    public ImageUpHandler(Context context, byte[] fileData, APImageUploadCallback cb, APImageUploadOption option,
            APMultimediaTaskModel taskStatus) {
        super(context);
        this.mFileData = fileData;
        callbacks = getImageManager().getUpTaskCallback(taskStatus.getTaskId());
        uploadOption = option;
        this.taskStatus = taskStatus;
        upType = TYPE_DATA;

    }

    /**
     * 压缩图片上传到服务端，同时计算压缩后的md5值
     *
     * @return
     * @throws IOException
     */
    private int compressAndGenImage() throws IOException {
        //压缩质量，0-低质量，1-中质量，>=2-高质量
        int quality = 0;
        boolean isOriginal = false;
        String cacheKey = null;
        if (uploadOption != null && uploadOption.getQua() != null) {
            switch (uploadOption.getQua()) {
                case ORIGINAL:
                    isOriginal = true;
                    break;
                case HIGH:
//                    quality = FalconFacade.QUA_HIGH;
                    break;
                case MIDDLE:
//                    quality = FalconFacade.QUA_MIDDLE;
                    break;
                case DEFAULT:
                    quality = getDefaultQua();
                    break;
                case LOW:
                default:
//                    quality = FalconFacade.QUA_LOW;
                    break;
            }
            logger.d("qua: " + uploadOption.getQua() + ", quality: " + quality);
        } else {
            //默认设置根据当前网络状态决定
            quality = getDefaultQua();
            logger.d("quality: " + quality);
        }

        if (upType == TYPE_FILE) {
            logger.d("before compress, size:" + mImageFile.length());
        } else {
            logger.d("before compress, size:" + toUploadFile.length());
        }

        if (isOriginal) {
            if (upType == TYPE_FILE) {
                toUploadFile = mImageFile;
                totalSize = mImageFile.length();
            } else {
                totalSize = toUploadFile.length();
            }
        } else {
            int x = 0;
            int y = 0;
            if (uploadOption != null) {
                x = uploadOption.getImage_x();
                y = uploadOption.getImage_y();
            }
            ByteArrayOutputStream out = null;
            if (upType == TYPE_FILE) {
                out = compressImage(mImageFile, quality, x, y);
            } else {
                out = compressImage(toUploadFile, quality, x, y);
                FileUtils.safeCopyToFile(out.toByteArray(), toUploadFile);
            }

            totalSize = out.size();
            byte[] data = out.toByteArray();
//            imageInputStream = new RepeatableInputStream(data);
//            Bitmap bmp = BitmapFactory.decodeStream(imageInputStream);

            if (upType == TYPE_FILE) {
                //上传时也将缓存一份大图
                int cacheX = x < 0 || x == APImageUpRequest.DEFAULT_UP_W ? 0 : x;
                int cacheY = y < 0 || y == APImageUpRequest.DEFAULT_UP_H ? 0 : y;
                String backupCacheKey = formatCacheKey(null, mFilePath, cacheX, cacheY, mCutScaleType);
                logger.d("backupCacheKey: " + backupCacheKey);
                if (cacheLoader.getDiskCache().save(backupCacheKey, data)) {
                    toUploadFile = cacheLoader.getDiskCache().getFile(backupCacheKey);
                }
//                ImageCacheContext.get().pushTempCache(mFilePath, backupCacheKey);
            }
        }

        //填入totalSize
        taskStatus.setTotalSize(totalSize);

        logger.d("after compressed, size：" + totalSize + ", isOriginal: " + isOriginal);

        //计算md5
        mLocalId = MD5Utils.getFileMD5String(toUploadFile);
        logger.d("calc md5，for rapid transfer...md5: " + mLocalId);
        return quality;
    }

    //根据网络选择不同的压缩率
    private int getDefaultQua() {
//        if (NetworkUtils.isWiFiMobileNetwork(mContext) || NetworkUtils.is4GMobileNetwork(mContext)) {
//            return FalconFacade.QUA_MIDDLE;
//        }
//        return FalconFacade.QUA_LOW;
        return 0;
    }

    private boolean checkParam(APImageUploadRsp uploadRsp) {
        logger.d("upload image check param..");
        APImageRetMsg retMsg = uploadRsp.getRetmsg();
        if (!CommonUtils.isActiveNetwork(mContext)) {
            mCode = APImageRetMsg.RETCODE.INVALID_NETWORK;
            retMsg.setMsg("network isn't ok");
            logger.d("network isn't ok");
            onError(retMsg, null);
            return false;
        }
        switch (upType) {
            case TYPE_FILE:
                if (mFilePath == null) {
                    mCode = APImageRetMsg.RETCODE.PARAM_ERROR;
                    retMsg.setMsg("imagePath isn't set..");
                    logger.d("path isn't set");
                    onError(retMsg, null);
                    return false;
                }

                Uri uri = Uri.parse(mFilePath);
                if ("file".equalsIgnoreCase(uri.getScheme())) {
                    mFilePath = PathUtils.extractPath(mFilePath);
                }

                mImageFile = new File(mFilePath);
                if (!mImageFile.exists() || !mImageFile.isFile() || mImageFile.length() <= 0) {
                    mCode = APImageRetMsg.RETCODE.FILE_NOT_EXIST;
                    retMsg.setMsg(mFilePath + " isn't exist or file");
                    logger.d(mFilePath + " isn't exist or file");
                    onError(retMsg, null);
                    return false;
                }
                break;
            case TYPE_DATA:
                if (mFileData == null || mFileData.length <= 0) {
                    mCode = APImageRetMsg.RETCODE.PARAM_ERROR;
                    retMsg.setMsg("fileData is null..");
                    logger.d("fileData is null");
                    onError(retMsg, null);
                    return false;
                }
                break;
            default:
                logger.d("unknown upload type..");
                mCode = APImageRetMsg.RETCODE.PARAM_ERROR;
                retMsg.setMsg("unknown upload type..");
                onError(retMsg, null);
                return false;
        }
        return true;
    }

    private void onError(APImageRetMsg retMsg, Exception e) {
        taskStatus.setStatus(APMultimediaTaskModel.STATUS_FAIL);
        removeNetTaskTag(mFilePath);
        APMultimediaTaskManager.getInstance(mContext).updateTaskRecord(taskStatus);
        APImageUploadRsp rsp = new APImageUploadRsp();
        retMsg.setCode(mCode);
        rsp.setRetmsg(retMsg);
        rsp.setTaskStatus(taskStatus);
        if (callbacks == null) {
            callbacks = getImageManager().getUpTaskCallback(taskStatus.getTaskId());
        }

        logger.d("uphandler onError mCode=" + mCode);
        if (callbacks != null) {
            for (APImageUploadCallback cb : callbacks.keySet()) {
                cb.onError(rsp, e);
            }
        }

        removeUploadCallBack(taskStatus.getTaskId());
    }

    private void onSuccess(APImageUploadRsp uploadRsp) {
        taskStatus.setStatus(APMultimediaTaskModel.STATUS_SUCCESS);
        removeNetTaskTag(mFilePath);
        taskStatus.setCloudId(mCloudId);
        uploadRsp.setTaskStatus(taskStatus);
        taskStatus.setTotalSize(totalSize);
        APMultimediaTaskManager.getInstance(mContext).updateTaskRecord(taskStatus);

        callbacks = getImageManager().getUpTaskCallback(taskStatus.getTaskId());
        mCode = APImageRetMsg.RETCODE.SUC;

        if (callbacks != null) {
            uploadRsp.getRetmsg().setCode(mCode);
            logger.d("uphandler onSuccess callbacks size=" + callbacks.size());
            for (APImageUploadCallback cb : callbacks.keySet()) {
                cb.onSuccess(uploadRsp);
            }
        }

        removeUploadCallBack(taskStatus.getTaskId());
        removeTaskModel(taskStatus.getTaskId());
        logger.d("uphandler onSuccess end mCloudId=" + mCloudId + ";taskid=" + taskStatus.getTaskId());
    }

    private void onStartUpload() {
        if (callbacks == null) {
            callbacks = getImageManager().getUpTaskCallback(taskStatus.getTaskId());
        }
        if (callbacks != null) {
            for (APImageUploadCallback cb : callbacks.keySet()) {
                cb.onStartUpload(taskStatus);
            }
        }
    }

    private int onProcess(long cSize, long tSize) {
        int progressValue = 0;
        if (tSize > 0) {
            progressValue = (int) ((float) cSize * 100.0f / tSize);
        }

        if (mProgress == progressValue) {
            return progressValue;
        }

        logger.d("已上传：" + cSize + "/" + tSize + ",progress=" + mProgress);

        mProgress = progressValue;
        taskStatus.setCurrentSize(cSize);
        taskStatus.setTotalSize(tSize);
        APMultimediaTaskManager.getInstance(mContext).updateTaskRecord(taskStatus);
        if (callbacks == null) {
            callbacks = getImageManager().getUpTaskCallback(taskStatus.getTaskId());
        }

        if (callbacks != null) {
            for (APImageUploadCallback cb : callbacks.keySet()) {
                cb.onProcess(taskStatus, progressValue);
            }
        }
        return progressValue;
    }

    private void checkAndConvertDataToFile() {
        if (mFileData != null) {
            try {
                toUploadFile = File.createTempFile("image_up_", ".jpg");
                FileUtils.safeCopyToFile(mFileData, toUploadFile);
            } catch (IOException e) {
            }
        }
    }

    /**
     * 上传单张图片 如果秒传成功，进度值不会更新 后续需优化大于10M文件需要分块上传
     *
     * @return APImageUploadRsp
     */
    public APImageUploadRsp upImage() {
        logger.d("start up image...");
        checkAndConvertDataToFile();
        long start = System.currentTimeMillis();
        taskStatus.setStatus(APMultimediaTaskModel.STATUS_RUNNING);
        final APImageUploadRsp uploadRsp = new APImageUploadRsp();
        final APImageRetMsg retMsg = new APImageRetMsg();
        uploadRsp.setRetmsg(retMsg);
        uploadRsp.setTaskStatus(taskStatus);
        int mSerCode = -1;
        String exp = "";

        if (!checkParam(uploadRsp)) {//参数有误直接返回吧？？
            recodeUpImageLog(String.valueOf(mCode.ordinal()),start,exp, null);
            return uploadRsp;
        }

        boolean bCompOk = true;
        try {
            putNetTaskTag(mFilePath, mFilePath);
            co = compressAndGenImage();
        } catch (Exception e) {
            logger.e(e, "compress for uploading failed");
            mCode = APImageRetMsg.RETCODE.COMPRESS_ERROR;
            exp = e.getMessage();
            retMsg.setMsg("compress for uploading failed");
            bCompOk = false;
            onError(retMsg, e);
        } finally {
            UCLogUtil.UC_MM_C07(totalSize, (int) (System.currentTimeMillis() - start), 0);
        }

        if (!bCompOk) {
            recodeUpImageLog(String.valueOf(mCode.ordinal()),start,exp, null);
            return uploadRsp;
        }

        logger.d("after compressed, size=" + totalSize + ";md5=" + mLocalId);
        start = System.currentTimeMillis();
        String traceId = null;
        try {
            String imageType = APFileReq.FILE_TYPE_COMPRESS_IMAGE;
            if (uploadOption == null || APImageUploadOption.QUALITITY.ORIGINAL.equals(uploadOption.getQua())) {
                imageType = APFileReq.FILE_TYPE_IMAGE;
            }
            APFileReq upReq = new APFileReq();
            upReq.setSavePath(toUploadFile.getAbsolutePath());
            upReq.setIsNeedCache(false);
            upReq.setType(imageType);

            final CountDownLatch waitLatch = new CountDownLatch(1);

            Options options = new Options();
            options.uploadType = Options.UPLOAD_TYPE_SLICE;
            FileUpLoadManager.getInstance(mContext).upLoad(upReq, options, new APFileUploadCallback() {
                @Override
                public void onUploadStart(APMultimediaTaskModel taskInfo) {
                    onStartUpload();
                }

                @Override
                public void onUploadProgress(APMultimediaTaskModel taskInfo, int progress, long hasUploadSize, long total) {
                    progressValue = onProcess(hasUploadSize, total);
                }

                @Override
                public void onUploadFinished(APMultimediaTaskModel taskInfo, APFileUploadRsp rsp) {
                    mCloudId = rsp.getFileReq().getCloudId();
                    if (!TextUtils.isEmpty(mFilePath) && !TextUtils.isEmpty(mCloudId)) {
                        cacheLoader.copyDiskCache(mFilePath, mCloudId);
//                        c2lCache.put(mCloudId, mFilePath);
//                        if (tempCache.containsKey(mFilePath)) {
//                            Set<String> set = ImageCacheContext.get().popTempCache(mFilePath);
//                            if (set != null) {
//                                for (String key : set) {
//                                    String local = key;
//                                    String cloud = key.replace(mFilePath, mCloudId);
//                                    Bitmap localCache = cacheLoader.getMemCache(local);
//                                    if (localCache != null) {
//                                        cacheLoader.copyMemCache(local, cloud);
//                                    }
//                                    cacheLoader.copyDiskCache(local, cloud);
//                                }
//                            }
//                        }
                    }

                    onSuccess(uploadRsp);
                    waitLatch.countDown();
                }

                @Override
                public void onUploadError(APMultimediaTaskModel taskInfo, APFileUploadRsp rsp) {

                    onError(retMsg, null);
                    waitLatch.countDown();
                }
            });
            waitLatch.await();

        } catch (Exception ex) {
            logger.e(ex, "getOriginalFromDjango exception");
            mCode = APImageRetMsg.RETCODE.UNKNOWN_ERROR;
            exp = ex.getMessage();
            retMsg.setMsg(ex.getMessage());
            onError(retMsg, null);
        } finally {
            String ret = String.valueOf(mCode.ordinal());
            if(mSerCode != -1){
                ret = "s"+String.valueOf(mSerCode);
            }

            recodeUpImageLog(ret, start, exp, traceId);
//            IOUtils.closeQuietly(imageInputStream);
        }
        return uploadRsp;
    }

    /**
     * 设置文件属性为共享
     *
     * @param fileId
     * @return
     */
    public SetExtResp setImageExtPublic(String fileId) {
        SetExtReq extReq = new SetExtReq(fileId);
        Map<String, Map<String, String>> ext = new HashMap<String, Map<String, String>>();
        Map<String, String> privilege = new HashMap<String, String>(1);
        privilege.put("public", "1");
        ext.put("privilege", privilege);
        extReq.setExt(ext);
        return getDjangoClient().getFileApi().setExt(extReq);
    }

    @Override
    public Void call() {
        upImage();
        return null;
    }

    /**
     * 上传图片网络部分日志埋点
     *
     * @param start
     */
    private void recodeUpImageLog(String ret,long start,String exp, String traceId) {
        int it = 1; //默认大图,0为小图,2原图
        if (uploadOption.getQua() == APImageUploadOption.QUALITITY.ORIGINAL) {
            it = 2;
        }
        UCLogUtil.UC_MM_C01(ret, totalSize, (int) (System.currentTimeMillis() - start), bRapid, co, it,mLocalId,exp, traceId);
    }

}

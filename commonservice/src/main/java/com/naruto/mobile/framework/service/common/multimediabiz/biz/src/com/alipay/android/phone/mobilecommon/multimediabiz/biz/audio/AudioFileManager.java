package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio;

import android.content.Context;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioDownloadCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.APAudioUploadCallback;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioInfo;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioUploadRsp;
import com.naruto.mobile.framework.service.common.multimedia.audio.data.APRequestParam;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.FileUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * 音频文件管理
 * Created by jinmin on 15/3/31.
 */
public class AudioFileManager {
    private static final String TAG = AudioFileManager.class.getSimpleName();

    Logger logger = Logger.getLogger("AudioFileManager");

    private static final String BASE_DIR_NAME = "alipay_audio_files";

    private static AudioFileManager sInstance;

    private Context mContext;
    private String mBaseDir;
    private AudioDjangoExecutor mAudioDjangoExecutor;

    private AudioFileManager(Context context) {
        this.mContext = context;
        this.mAudioDjangoExecutor = AudioDjangoExecutor.getInstance(context);
    }

    public synchronized static AudioFileManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AudioFileManager(context);
        }
        return sInstance;
    }

    public String generateSavePath(APAudioInfo info) {
        logger.d("generateSavePath info: " + info);
        String path;
        if (info != null && !TextUtils.isEmpty(info.getSavePath())) {
            path = info.getSavePath();
        } else if (info != null && !TextUtils.isEmpty(info.getLocalId())) {
            path = getBaseDir() + File.separator + info.getLocalId();
        } else {
            path = getBaseDir() + File.separator + System.currentTimeMillis();
        }
        logger.d("generateSavePath path: " + path);
        return path;
    }

    public String getBaseDir() {
        if (TextUtils.isEmpty(mBaseDir)) {
            try {
                mBaseDir = FileUtils.getMediaDir(BASE_DIR_NAME);
                if (TextUtils.isEmpty(mBaseDir)) {
                    mBaseDir = mContext.getFilesDir().getAbsolutePath() + File.separator + BASE_DIR_NAME;
                }
            } catch (Exception e) {
                mBaseDir = mContext.getFilesDir().getAbsolutePath() + File.separator + BASE_DIR_NAME;
            }
        }
        File baseDir = new File(mBaseDir);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            FileUtils.mkdirs(baseDir);
        }
        logger.d("getBaseDir mBaseDir: " + mBaseDir);
        return mBaseDir;
    }

    public String makeSaveFilePath(APAudioInfo info) {
        if (info != null && !TextUtils.isEmpty(info.getCloudId())) {
            File saveFile = new File(getBaseDir(), info.getCloudId());
            return saveFile.getAbsolutePath();
        }
        return "";
    }

    public void uploadAudio(APAudioInfo info, APRequestParam param, APAudioUploadCallback cb) {
        mAudioDjangoExecutor.uploadDirect(info, param, cb);
    }

    public APAudioUploadRsp uploadAudioSync(APAudioInfo info, APRequestParam param) {
       return mAudioDjangoExecutor.uploadDirectSync(info, param);
    }

    public APMultimediaTaskModel downloadAudio(APAudioInfo info, APRequestParam param, APAudioDownloadCallback listener) {
        return mAudioDjangoExecutor.download(info, param, listener);
    }

    public void cancelDownloadTask(APAudioInfo info) {
        mAudioDjangoExecutor.cancelDownload(info);
    }

    /**
     * 判断音频文件是否已经下载好
     * @param info          音频文件描述
     * @return              true：音频已存在， false：音频不存在
     */
    public boolean checkAudioOk(APAudioInfo info) {
        return mAudioDjangoExecutor.fromCache(info);
    }

    public APAudioDownloadRsp downloadAudio(APAudioInfo info, APRequestParam param) {
        return mAudioDjangoExecutor.download(info, param);
    }

    public int deleteCache(String path) {
        int deleteCount = 0;
        String baseDir = getBaseDir();
        File file = new File(baseDir, path);
        if (FileUtils.delete(file)) {
            deleteCount++;
        }
        return deleteCount;
    }
}

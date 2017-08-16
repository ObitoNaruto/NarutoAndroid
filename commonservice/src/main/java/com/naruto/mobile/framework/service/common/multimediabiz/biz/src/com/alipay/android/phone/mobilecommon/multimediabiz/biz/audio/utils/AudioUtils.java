package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.naruto.mobile.framework.service.common.multimedia.audio.data.APAudioRsp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio.silk.SilkApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.UCLogUtil;

/**
 * 音频工具
 * Created by jinmin on 15/6/23.
 */
public class AudioUtils {

    private static final String TAG = "AudioUtils";

    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPLAY = "play";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";

    public static void pauseSystemAudio() {
        muteAudioFocus(true);
    }

    public static void resumeSystemAudio() {
        muteAudioFocus(false);
    }

    public static void stopSystemAudio() {
//        sendSystemAudioCmd(CMDSTOP);
    }

    public static void sendSystemAudioCmd(String cmd) {
        Intent intent = new Intent();
        intent.putExtra("command", cmd);
        AppUtils.getApplicationContext().sendBroadcast(intent);
    }

    public static boolean isMusicActive() {
        AudioManager audioManager = (AudioManager) AppUtils.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        return audioManager != null && audioManager.isMusicActive();
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public static boolean muteAudioFocus(boolean bMute) {
        Context context = AppUtils.getApplicationContext();
        if (context == null) {
            Logger.E(TAG, "context is null.");
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            // 2.1以下的版本不支持下面的API：requestAudioFocus和abandonAudioFocus
            Logger.E("ANDROID_LAB", "Android 2.1 and below can not stop music");
            return false;
        }
        boolean bool = false;
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (bMute) {
            int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            int result = am.abandonAudioFocus(null);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        Logger.E(TAG, "pauseMusic bMute=" + bMute + " result=" + bool);
        return bool;
    }

    public static boolean checkSilkAudioFile(String path) {
        String cloudId = "";
        if (!TextUtils.isEmpty(path)) {
            cloudId = new File(path).getName();
            RandomAccessFile fis = null;
            try {
                fis = new RandomAccessFile(path, "r");
                byte[] silkHead = SilkApi.SILK_HEAD.getBytes();
                byte[] headData = new byte[silkHead.length];
                int read = fis.read(headData);
//                fis.seek(fis.length()-SilkApi.SILK_END.length);
//                short silkEnd = fis.readShort();
//                Logger.D(TAG, "checkSilkAudioFile path: " + path + ", silkEnd: " + silkEnd);
                if ((silkHead.length == read) /*&& (silkEnd == SilkApi.SILK_END_SHORT)*/) {
                    boolean ret = Arrays.equals(silkHead, headData);
                    if (!ret) {
                        UCLogUtil.UC_MM_C12(APAudioRsp.CODE_ERROR, cloudId, "not silk file");
                    }
                    return ret;
                }
            } catch (Exception e) {
                Logger.E(TAG, e, "checkSilkAudioFile error");
                UCLogUtil.UC_MM_C12(APAudioRsp.CODE_ERROR, cloudId, e.getClass().getSimpleName() + ":" + e.getMessage());
                return false;
            } finally {
                IOUtils.closeQuietly(fis);
            }
        }
        UCLogUtil.UC_MM_C12(APAudioRsp.CODE_ERROR, cloudId, "empty path");
        return false;
    }
}

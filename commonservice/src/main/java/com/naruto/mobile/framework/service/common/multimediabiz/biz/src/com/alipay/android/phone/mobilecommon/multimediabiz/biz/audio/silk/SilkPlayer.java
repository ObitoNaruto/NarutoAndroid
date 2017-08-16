package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio.silk;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.util.Log;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.IOUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * Silk 播放器
 * Created by jinmin on 15/5/21.
 */
public class SilkPlayer {

    private static final String TAG = "SilkPlayer";

    private static final byte[] SILK_HEAD = SilkApi.SILK_HEAD.getBytes();

    public boolean isUsingSpeakerphone() {
        return mCurrentAudioTrack == null || mCurrentAudioTrack == mSpeakerAudioTrack;
    }

    public interface IPlayListener {
        void onStart(SilkPlayer player);
        void onPause(SilkPlayer player);
        void onResume(SilkPlayer player);
        void onStop(SilkPlayer player);
        void onComplete(SilkPlayer player);
        void onError(SilkPlayer player, Exception e);
    }

    public static abstract class AudioParam {
        int streamType = AudioManager.STREAM_MUSIC;
        int sampleRateInHz = 8000;
        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSizeInBytes;
        int mode = AudioTrack.MODE_STREAM;

        public int getStreamType() {
            return streamType;
        }

        public void setStreamType(int streamType) {
            this.streamType = streamType;
        }

        public int getSampleRateInHz() {
            return sampleRateInHz;
        }

        public void setSampleRateInHz(int sampleRateInHz) {
            this.sampleRateInHz = sampleRateInHz;
        }

        public int getChannelConfig() {
            return channelConfig;
        }

        public void setChannelConfig(int channelConfig) {
            this.channelConfig = channelConfig;
        }

        public int getAudioFormat() {
            return audioFormat;
        }

        public void setAudioFormat(int audioFormat) {
            this.audioFormat = audioFormat;
        }

        public int getBufferSizeInBytes() {
            if (bufferSizeInBytes == 0) {
                bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz,
                        channelConfig,
                        audioFormat);
            }
            return bufferSizeInBytes;
        }

        public void setBufferSizeInBytes(int bufferSizeInBytes) {
            this.bufferSizeInBytes = bufferSizeInBytes;
        }

        public int getMode() {
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }
    }

    public static class PathAudioParam extends AudioParam {
        String path;

        public PathAudioParam() {

        }

        public PathAudioParam(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class ByteDataAudioParam extends AudioParam {
        byte[] data;

        public ByteDataAudioParam(byte[] data) {
            this.data = data;
            setMode(AudioTrack.MODE_STATIC);
        }

        public byte[] getData() {
            return data;
        }
    }

    Logger logger = Logger.getLogger(TAG);

    private static AudioTrack mSpeakerAudioTrack;
    private static AudioTrack mEarphoneAudioTrack;
    private volatile AudioTrack mCurrentAudioTrack;
    private AudioParam mAudioParam;
    private IPlayListener mPlayListener;
    private Thread mDecodeThread;
    private List<DataPacket> mDecodedDatas = Collections.synchronizedList(new ArrayList<DataPacket>());
    private Thread mPlayThread;
    private AtomicBoolean mPlaying = new AtomicBoolean(false);
    private AtomicBoolean mDecodeFinished = new AtomicBoolean(false);
    private AtomicBoolean mPlayFinished = new AtomicBoolean(false);
    private AtomicBoolean mWaitChanging = new AtomicBoolean(false);
    private AtomicLong mCurrentPosition = new AtomicLong(-1);
    private long mStartTime = -1;


    public SilkPlayer(AudioParam param) {
        this.mAudioParam = param;
    }

    public long getCurrentPosition() {
        return (isPaused() || isPlaying()) ? mCurrentPosition.get() : -1;
    }

    public void setPlayListener(IPlayListener listener) {
        this.mPlayListener = listener;
    }

    public void prepare() {
        prepareSpeakAudioTrack();
        prepareEarAudioTrack();
        mCurrentAudioTrack = mSpeakerAudioTrack;

        checkAudioTrack();
    }

    private void prepareEarAudioTrack() {
        if (!checkAudioTrack(mEarphoneAudioTrack)) {
            mEarphoneAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
                    mAudioParam.getSampleRateInHz(),
                    mAudioParam.getChannelConfig(),
                    mAudioParam.getAudioFormat(),
                    mAudioParam.getBufferSizeInBytes() * 2,
                    mAudioParam.getMode());
        }
    }

    private void prepareSpeakAudioTrack() {
        if (!checkAudioTrack(mSpeakerAudioTrack)) {
            mSpeakerAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    mAudioParam.getSampleRateInHz(),
                    mAudioParam.getChannelConfig(),
                    mAudioParam.getAudioFormat(),
                    mAudioParam.getBufferSizeInBytes() * 2,
                    mAudioParam.getMode());
        }
    }

    private boolean checkAudioTrack(AudioTrack audioTrack) {
        return audioTrack != null && audioTrack.getState() == AudioTrack.STATE_INITIALIZED;
    }

    private void checkAudioTrack() {
        if (!checkAudioTrack(mSpeakerAudioTrack)) {
            mSpeakerAudioTrack.release();
            prepareSpeakAudioTrack();
        }
        if (!checkAudioTrack(mEarphoneAudioTrack)) {
            mEarphoneAudioTrack.release();
            prepareEarAudioTrack();
        }
        if (!checkAudioTrack(mCurrentAudioTrack)) {
			logger.e("checkAudioTrack error: " + getAudioTrackInfo(mCurrentAudioTrack));
            throw new RuntimeException("play audio exp!!!");
        }
    }

    public void useEarphonePlay(boolean use, boolean noPlay) {
        useEarphonePlay(use, noPlay, false);
    }

    public void useEarphonePlay(boolean use, boolean noPlay, boolean manual) {
        mWaitChanging.set(true);
        boolean changeRet = false;
        if (use && mCurrentAudioTrack != mEarphoneAudioTrack) {
            if (noPlay) {
                mCurrentAudioTrack = mEarphoneAudioTrack;
            } else {
                changeRet = changeAudioTrack(mEarphoneAudioTrack, manual);
            }
        } else if (!use && mCurrentAudioTrack != mSpeakerAudioTrack){
            if (noPlay) {
                mCurrentAudioTrack = mSpeakerAudioTrack;
            } else {
                changeRet = changeAudioTrack(mSpeakerAudioTrack, manual);
            }
        }
        mWaitChanging.set(false);
        if (!changeRet) {
            logger.e("useEarphonePlay changeRet: %s, use: %s, noPlay: %s", changeRet, use, noPlay);
        }
    }

    private boolean changeAudioTrack(AudioTrack dst, boolean manual) {
        try {
            checkAudioTrack();
            if (manual) {
                mCurrentAudioTrack.stop();
            } else {
                mCurrentAudioTrack.pause();
            }
            mCurrentAudioTrack = dst;
            mCurrentAudioTrack.play();
        } catch (Exception e) {
            logger.e(e, "changeAudioTrack dst: " + getAudioTrackInfo(dst));
            notifyPlayError(e);
            return false;
        }
        return true;
    }

    public void start() {
        mStartTime = System.currentTimeMillis();
        if (checkAudioTrack(mCurrentAudioTrack) && isPaused(mCurrentAudioTrack)) {
            mCurrentAudioTrack.play();
            notifyPlayResume();
            return;
        }
        mPlaying.set(true);
        if (mDecodeThread == null || mDecodeThread.isInterrupted() || !mDecodeThread.isAlive()) {
            mDecodeThread = new DecodeThread();
            mDecodeThread.start();
        }
        if (mPlayThread == null || mPlayThread.isInterrupted() || !mPlayThread.isAlive()) {
            mPlayThread = new PlayThread();
            mPlayThread.start();
        }
        notifyPlayStart();
    }

    public void stop() {
//        if (isPlaying(mEarphoneAudioTrack) || isPaused(mEarphoneAudioTrack)) {
//            mEarphoneAudioTrack.stop();
//        }
//        if (isPlaying(mSpeakerAudioTrack) || isPaused(mSpeakerAudioTrack)) {
//            mSpeakerAudioTrack.stop();
//        }
//        mPlaying.set(false);
//        mDecodedDatas.clear();
        reset();
    }

    public void pause() {
        if (checkAudioTrack(mCurrentAudioTrack) && isPlaying(mCurrentAudioTrack)) {
            mCurrentAudioTrack.pause();
            notifyPlayPause();
        }
    }

    public boolean isPlaying() {
        return isPlaying(mCurrentAudioTrack);
    }

    public boolean isPaused() {
        return isPaused(mCurrentAudioTrack);
    }


    private boolean isPlaying(AudioTrack audioTrack) {
        return checkAudioTrack(audioTrack) && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
    }

    private boolean isPaused(AudioTrack audioTrack) {
        return checkAudioTrack(audioTrack) && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED;
    }

    private void stop(AudioTrack audioTrack) {
        if (audioTrack != null) {
            if (isPlaying(audioTrack) || isPaused(audioTrack)) {
                audioTrack.stop();
            }
        }
    }

    public void reset() {
        mPlaying.set(false);
        stop(mSpeakerAudioTrack);
        stop(mEarphoneAudioTrack);
        mCurrentAudioTrack = null;
        if (mPlayThread != null && mPlayThread.isAlive()) {
            mPlayThread.interrupt();
            mPlayThread = null;
        }
        if (mDecodeThread != null && mDecodeThread.isAlive()) {
            mDecodeThread.interrupt();
            mDecodeThread = null;
        }
        mPlaying.set(false);
        mDecodedDatas.clear();
        AppUtils.sleep(20);
    }

    public void release() {
        reset();
        if (mSpeakerAudioTrack != null && mSpeakerAudioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
            mSpeakerAudioTrack.release();
        }
        mSpeakerAudioTrack = null;
        if (mEarphoneAudioTrack != null && mEarphoneAudioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
            mEarphoneAudioTrack.release();
        }
        mEarphoneAudioTrack = null;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private String getAudioTrackInfo(AudioTrack audioTrack) {
        StringBuffer sb = new StringBuffer();
        if (audioTrack != null) {
            sb.append("sessionId: ").append(audioTrack.getAudioSessionId());
            sb.append("streamType: ").append(audioTrack.getStreamType()).append(", ");
            sb.append("state: ").append(audioTrack.getState()).append(", ");
            sb.append("playState: ").append(audioTrack.getPlayState());
        } else {
            sb.append("null");
        }
        return sb.toString();
    }

    private class DataPacket {
        short[] data;
        int size;

        public DataPacket(short[] data, int size) {
            this.data = new short[size];
            System.arraycopy(data, 0, this.data, 0, size);
            this.size = size;
        }

        public short[] getData() {
            return data;
        }

        public void setData(short[] data) {
            this.data = data;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }

    private class DecodeThread extends Thread {

        private static final String TAG = "DecodeThread";

        private SilkApi mSilkApi;

        public DecodeThread() {
            super("Decode-Thread");
            mSilkApi = new SilkApi();
            mDecodedDatas.clear();
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void run() {
            if (mAudioParam instanceof PathAudioParam) {
                String path = ((PathAudioParam)mAudioParam).getPath();
                FileInputStream fis = null;
                int read;

                byte[] lengthBuffer = new byte[2];
                byte[] encodeBuffer = new byte[4096];
                short[] decodeBuffer = new short[960];
                boolean headError = false;
                try {
                    mDecodeFinished.set(false);
                    fis = new FileInputStream(path);
                    //check file head
                    read = fis.read(encodeBuffer, 0, SILK_HEAD.length);
                    if (!Arrays.equals(Arrays.copyOf(encodeBuffer, read), SILK_HEAD)) {
                        logger.d("DecodeThread not silk file...");
                        IOUtils.closeQuietly(fis);
                        headError = true;
						SilkPlayer.this.reset();
                        if (mPlayListener != null) {
                            logger.e("Not supported file: " + path);
                            mPlayListener.onError(SilkPlayer.this, new IllegalArgumentException("Not supported file.."));
                        }
                        return;
                    }
                    int openRet = mSilkApi.openDecoder(mAudioParam.getSampleRateInHz());
                    logger.d("openDecoder openRet = %d", openRet);
                    while (mPlaying.get()) {
                        read = fis.read(lengthBuffer, 0, 2);
                        if (read == -1) {
                            logger.d("read end");
                            break;
                        }
                        short length = SilkUtils.getLittleEndianShort(lengthBuffer);
                        if (length < 0) {
                            logger.d("getLength: " + length);
                            break;
                        }
                        read = fis.read(encodeBuffer, 0, length);
                        logger.d("read: " + read);
                        if (read == -1) break;
                        int decodeLength = mSilkApi.decode(encodeBuffer, decodeBuffer, read);
                        logger.p("decodeLength: " + decodeLength);
                        DataPacket packet = new DataPacket(decodeBuffer, decodeLength);
                        mDecodedDatas.add(packet);
                    }
                    //先粗暴处理，统一到finally里设置解码完成（解码失败也当解码成功）
//                    mDecodeFinished.set(true);
                } catch (Exception e) {
                    logger.e(e, "DecodeThread error, path: " + path);
//                    notifyPlayError(e);
                } finally {
                    IOUtils.closeQuietly(fis);
                    logger.d("go into finally headError? %s", headError);
                    if (!headError) {
                        mSilkApi.closeDecoder();
                        logger.d("silkApi closeDecoder");
                    }
                    //此处修改粗暴，考虑回调error
                    mDecodeFinished.set(true);
                }
            }
        }
    }

    private class PlayThread extends Thread {

        public PlayThread() {
            super("Play-Thread");
        }

        @Override
        public void run() {
            mCurrentAudioTrack.play();
            mPlayFinished.set(false);
            if (mAudioParam instanceof ByteDataAudioParam) {
                ByteDataAudioParam param = (ByteDataAudioParam)mAudioParam;
                byte[] data = param.getData();
                mCurrentAudioTrack.write(data, 0, data.length);
            } else if (mAudioParam instanceof PathAudioParam) {
                while (mWaitChanging.get() || (isPlaying() || isPaused())) {
                    if (mWaitChanging.get() || isPaused() || (!mDecodeFinished.get() && mDecodedDatas.isEmpty())) {
                        logger.d("waitChanging? %s datas.empty? %s", mWaitChanging.get(), mDecodedDatas.isEmpty());
                        try {
                            sleep(50);
                        } catch (InterruptedException e) {
                        }
                        continue;
                    }
                    mCurrentPosition.addAndGet(System.currentTimeMillis()-mStartTime);
                    mStartTime = System.currentTimeMillis();
                    if (mDecodedDatas.isEmpty() && mDecodeFinished.get()) {
                        logger.d("decode finished and all data has been played");
                        //已完成全部解码了，播放该结束了
                        SilkPlayer.this.stop();
                        mPlayFinished.set(true);
                        break;
                    }
                    logger.p("remain data.size: %d, currentPosition: %d", mDecodedDatas.size(), getCurrentPosition());
                    DataPacket packet = mDecodedDatas.remove(0);
                    mCurrentAudioTrack.write(packet.getData(), 0, packet.getSize());
                }
            }
            mCurrentPosition.set(-1);
            if (mPlayFinished.get()) {
                notifyPlayFinish();
            } else {
                notifyPlayStop();
            }
        }
    }

    private void notifyPlayStart() {
        if (mPlayListener != null) {
            mPlayListener.onStart(this);
        }
    }

    private void notifyPlayPause() {
        if (mPlayListener != null) {
            mPlayListener.onPause(this);
        }
    }

    private void notifyPlayResume() {
        if (mPlayListener != null) {
            mPlayListener.onResume(this);
        }
    }

    private void notifyPlayStop() {
        if (mPlayListener != null) {
            mPlayListener.onStop(this);
        }
    }

    private void notifyPlayFinish() {
        if (mPlayListener != null) {
            mPlayListener.onComplete(this);
        }
    }

    private void notifyPlayError(Exception e) {
        if (mPlayListener != null) {
            mPlayListener.onError(this, e);
        }
    }

}

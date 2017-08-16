package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode;

import java.io.IOException;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.naruto.mobile.framework.service.common.multimedia.widget.SightCameraView;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.widget.CameraView;


/**
 * @hide
 */
public class AVRecorder {

    protected CameraEncoder mCamEncoder;
    protected MicrophoneEncoder mMicEncoder;
    private SessionConfig mConfig;
    private boolean mIsRecording;

    public AVRecorder(SessionConfig config) throws IOException {
        init(config);
    }

    private void init(SessionConfig config) throws IOException {
        mCamEncoder = new CameraEncoder(config);
        mMicEncoder = new MicrophoneEncoder(config);
        mConfig = config;
        mIsRecording = false;
    }

    public void setRecordListener(SightCameraView.OnRecordListener recordListener) {
        if (mMicEncoder != null) {
            mMicEncoder.setRecordListener(recordListener);
        }
    }

    public void setPreviewDisplay(CameraView display, SurfaceTexture surfaceTexture){
        mCamEncoder.setPreviewDisplay(display);
        mCamEncoder.onSurfaceTextureAvailable(surfaceTexture);
    }


    public void setCamera(Camera camera){
        mCamEncoder.setCamera(camera);
    }

    public void startRecording(){
        mIsRecording = true;
        
        mMicEncoder.startRecording();
        mCamEncoder.startRecording();
    }

    public boolean isRecording(){
        return mIsRecording;
    }

    public void stopRecording(){
        mIsRecording = false;
        mMicEncoder.stopRecording();
        mCamEncoder.stopRecording();
        //release();
    }

    /**
     * Release resources. Must be called after {@link #stopRecording()} After this call
     * this instance may no longer be used.
     */
    public void release() {
        mCamEncoder.release();
        mMicEncoder.stopRecording();
        // MicrophoneEncoder releases all it's resources when stopRecording is called
        // because it doesn't have any meaningful state
        // between recordings. It might someday if we decide to present
        // persistent audio volume meters etc.
        // Until then, we don't need to write MicrophoneEncoder.release()
    }

}

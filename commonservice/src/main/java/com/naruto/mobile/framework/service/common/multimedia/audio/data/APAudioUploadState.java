package com.naruto.mobile.framework.service.common.multimedia.audio.data;

/**
 * 语音上传状态
 * Created by jinmin on 15/7/21.
 */
public class APAudioUploadState {

    public static final int STATE_UNKNOWN = -1;

    public static final int STATE_SUCCESS = 0;

    public static final int STATE_ERROR = 1;

    public static final int STATE_UPLOADING = 2;

    public static final int STATE_UPLOAD_CANCEL = 3;

    private int state = STATE_UNKNOWN;

    public APAudioUploadState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

//    public boolean isSuccess() {
//        return STATE_SUCCESS == state;
//    }
//
//    public boolean isUploading() {
//        return STATE_UPLOADING == state;
//    }
//
//    public boolean isError() {
//        return STATE_ERROR == state;
//    }
//
//    public boolean isCanceled() {
//        return STATE_UPLOAD_CANCEL == state;
//    }

    @Override
    public String toString() {
        return "APAudioUploadState{" +
                "state=" + state +
                '}';
    }
}

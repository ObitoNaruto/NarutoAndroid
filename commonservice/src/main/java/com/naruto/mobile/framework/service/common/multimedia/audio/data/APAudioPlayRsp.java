package com.naruto.mobile.framework.service.common.multimedia.audio.data;

/**
 * 音频播放回调数据结构
 * Created by jinmin on 15/3/31.
 */
public class APAudioPlayRsp extends APAudioRsp {
    private int what;
    private int extra;

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public int getExtra() {
        return extra;
    }

    public void setExtra(int extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "APAudioPlayRsp{" +
                "what=" + what +
                ", extra=" + extra +
                ", super=" + super.toString() +
                '}';
    }
}

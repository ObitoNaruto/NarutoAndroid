package com.naruto.mobile.framework.service.common.multimedia.graphics.data;

/**
 * 裁切模式
 * Created by jinmin on 15/5/29.
 */
public enum CutScaleType {
    /**
     * 智能裁切
     */
    CENTER_CROP(0),
    /**
     * 保持比例
     */
    KEEP_RATIO(1),
    /**
     * 不缩放
     */
    NONE(2),
    /**
     * 如果Bitmap的尺寸大于 javax.microedition.khronos.opengles.GL10#GL_MAX_TEXTURE_SIZE，缩放至最大允许尺寸
     */
    NONE_SAFE(3),
    /**
     * 按Sample 2的倍数缩放，1，2，4，8……
     */
    IN_SAMPLE_POWER_OF_2(4),
    /**
     * Sample倍数缩放，1，2，3，4……
     */
    IN_SAMPLE_INT(5),
    /**
     * 精准缩放
     */
//    EXACTLY(6),
    /**
     *
     */
    EXACTLY_STRETCHED(7),
    /**
     * 保持最小边缩放
     */
    SCALE_KEEP_SMALL(8),
    /**
     * 自动限制缩放，最大不超过屏幕宽的1/4，不到屏幕宽1/4, 保持原有尺寸
     */
    SCALE_AUTO_LIMIT(9),
    CutScaleType(10);

    private int value;

    CutScaleType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

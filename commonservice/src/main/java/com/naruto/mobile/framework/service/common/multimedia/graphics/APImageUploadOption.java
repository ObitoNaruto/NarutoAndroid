package com.naruto.mobile.framework.service.common.multimedia.graphics;

/**
 * 下载可选项
 *
 * @author xiaofeng.dxf
 */
public class APImageUploadOption {
    /**
     * 上传图片x宽度
     */
    private int image_x;
    /**
     * 上传图片y宽度
     */
    private int image_y;

    /**
     * 显示缩略图x宽度
     */
    private int drawable_x;

    /**
     * 显示缩略图y宽度
     */
    private int drawable_y;

    public int getDrawable_y() {
        return drawable_y;
    }

    public void setDrawable_y(int drawable_y) {
        this.drawable_y = drawable_y;
    }

    public int getDrawable_x() {

        return drawable_x;
    }

    public void setDrawable_x(int drawable_x) {
        this.drawable_x = drawable_x;
    }

    /**
     * 图片压缩质量
     */
    private QUALITITY qua;

    public int getImage_x() {
        return image_x;
    }

    public void setImage_x(int image_x) {
        this.image_x = image_x;
    }

    public int getImage_y() {
        return image_y;
    }

    public void setImage_y(int image_y) {
        this.image_y = image_y;
    }

    public QUALITITY getQua() {
        return qua;
    }

    public void setQua(QUALITITY qua) {
        this.qua = qua;
    }

    /**
     * 图片压缩质量
     *
     * @author xiaofeng.dxf
     */
    public static enum QUALITITY {
		ORIGINAL, HIGH, MIDDLE, LOW, DEFAULT
    }
}

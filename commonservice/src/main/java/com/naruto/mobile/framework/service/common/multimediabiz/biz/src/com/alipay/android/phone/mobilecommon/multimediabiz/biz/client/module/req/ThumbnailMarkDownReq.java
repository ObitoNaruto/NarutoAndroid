package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req;


public class ThumbnailMarkDownReq extends ThumbnailsDownReq{
    /**
     * @param fileIds
     * @param zoom    请求图片缩略图的大小，图片缩放格式，100x100 请参考:http://baike.corp.taobao.com/index.php/CS_RD/
     */
    public ThumbnailMarkDownReq(String fileIds, String zoom) {
        super(fileIds, zoom);
    }

    /**
     * 水印来源图文件ID
     */
    private String markId;

    /**
     * position:水印位置，参考以下九宫格方位，[1-9]，默认值：5
     1左上	2中上	3右上
     4左中	5中部	6右中
     7左下	8中下	9右下
     */
    private int position = 5;

    /**
     * 水印来源图透明度，(0,100]，默认：80，如果设为100则不透明
     */
    private int transparency = 80;

    /**
     * 水印来源图宽
     */
    private int markWidth = 100;

    /**
     * 水印来源图高
     */
    private int markHeight = 100;

    /**
     * 水平方向缩进, 中部无效
     */
    private Integer paddingX;

    /**
     * 垂直方向缩进, 中部无效
     */
    private Integer paddingY;

    /**
     * P: 对当前水印来源图进行按主图的比例缩放，如10表示基于主图的10%进行处理，
     * 比如主图尺寸为1000x1000，则P=10表示把水印来源图缩放成100x100,取值范围(0,100]
     */
    private Integer percent;

    public String getMarkId() {
        return markId;
    }

    public void setMarkId(String markId) {
        this.markId = markId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTransparency() {
        return transparency;
    }

    public void setTransparency(int transparency) {
        this.transparency = transparency;
    }

    public int getMarkWidth() {
        return markWidth;
    }

    public void setMarkWidth(int markWidth) {
        this.markWidth = markWidth;
    }

    public int getMarkHeight() {
        return markHeight;
    }

    public void setMarkHeight(int markHeight) {
        this.markHeight = markHeight;
    }

    public Integer getPaddingX() {
        return paddingX;
    }

    public void setPaddingX(Integer paddingX) {
        this.paddingX = paddingX;
    }

    public Integer getPaddingY() {
        return paddingY;
    }

    public void setPaddingY(Integer paddingY) {
        this.paddingY = paddingY;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }
}

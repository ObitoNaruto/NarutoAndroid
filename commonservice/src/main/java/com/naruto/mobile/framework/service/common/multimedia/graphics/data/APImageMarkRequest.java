package com.naruto.mobile.framework.service.common.multimedia.graphics.data;

public class APImageMarkRequest {

    public static final int POS_LEFT_TOP = 1;
    public static final int POS_CENTER_TOP = 2;
    public static final int POS_RIGHT_TOP = 3;
    public static final int POS_LEFT_CENTER = 4;
    public static final int POS_CENTER_CENTER = 5;
    public static final int POS_RIGHT_CENTER = 6;
    public static final int POS_LEFT_BOTTOM = 7;
    public static final int POS_CENTER_BOTTOM = 8;
    public static final int POS_RIGHT_BOTTOM = 9;


    public static final int TRANSPARENCY_MIN = 1;
    public static final int TRANSPARENCY_MAX = 100;

    public static final int PERCENT_MIN = 1;
    public static final int PERCENT_MAX = 100;

    /**
     * 水印来源图文件ID
     */
    private String markId;

    /**
     * position:水印位置，参考以下九宫格方位，[1-9]
     1左上	2中上	3右上
     4左中	5中部	6右中
     7左下	8中下	9右下
     */
    private Integer position;

    /**
     * 水印来源图透明度，(0,100]，如果设为100则不透明
     */
    private Integer transparency;

    /**
     * 水印来源图宽
     */
    private Integer markWidth;

    /**
     * 水印来源图高
     */
    private Integer markHeight;

    /**
     * 水平方向缩进, 中部无效
     */
    private Integer paddingX;

    /**
     * 垂直方向缩进, 中部无效
     */
    private Integer paddingY;

    /**
     * 百分比,(0,100] 如果传了percent, 宽高就无效了
     */
    private Integer percent;

    public String getMarkId() {
        return markId;
    }

    public Integer getPosition() {
        return position;
    }

    public Integer getTransparency() {
        return transparency;
    }

    public Integer getMarkWidth() {
        return markWidth;
    }

    public Integer getMarkHeight() {
        return markHeight;
    }

    public Integer getPaddingX() {
        return paddingX;
    }

    public Integer getPaddingY() {
        return paddingY;
    }

    public Integer getPercent() {
        return percent;
    }

    public APImageMarkRequest(Builder builder) {
        this.markId = builder.markId;
        this.position = builder.position;
        this.transparency = builder.transparency;
        this.markWidth = builder.markWidth;
        this.markHeight = builder.markHeight;
        this.paddingX = builder.paddingX;
        this.paddingY = builder.paddingY;
        this.percent = builder.percent;
    }

    @Override
    public String toString() {
        return "APImageMarkRequest{" +
                "markId='" + markId + '\'' +
                "position='" + position + '\'' +
                "transparency='" + transparency + '\'' +
                "markWidth='" + markWidth + '\'' +
                "markHeight='" + markHeight + '\'' +
                "paddingX='" + paddingX + '\'' +
                "paddingY='" + paddingY + '\'' +
                "percent='" + percent + '\'' +
                '}';
    }

    public static class Builder {
        private String markId;
        private Integer position;
        private Integer transparency;
        private Integer markWidth;
        private Integer markHeight;
        private Integer paddingX;
        private Integer paddingY;
        private Integer percent;

        public Builder() {
        }

        public Builder markId(String markId) {
            this.markId = markId;
            return this;
        }

        public Builder position(Integer position) {
            this.position = position;
            return this;
        }

        public Builder transparency(Integer transparency) {
            this.transparency = transparency;
            return this;
        }

        public Builder markWidth(Integer markWidth) {
            this.markWidth = markWidth;
            return this;
        }

        public Builder markHeight(Integer markHeight) {
            this.markHeight = markHeight;
            return this;
        }

        public Builder paddingX(Integer paddingX) {
            this.paddingX = paddingX;
            return this;
        }

        public Builder paddingY(Integer paddingY) {
            this.paddingY = paddingY;
            return this;
        }

        public Builder percent(Integer percent) {
            this.percent = percent;
            return this;
        }

        public APImageMarkRequest build() {
            return new APImageMarkRequest(this);
        }
    }
}

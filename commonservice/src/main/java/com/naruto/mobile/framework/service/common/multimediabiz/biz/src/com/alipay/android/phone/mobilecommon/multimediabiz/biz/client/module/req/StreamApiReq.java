package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req;

/**
 * 请求流内容的参数封装类
 */
public class StreamApiReq {

    /**
     * 获取转换后的视频地址
     */
    public static final String MODULE_VIDEO = "mts";
    /**
     * 获取转换后的文档地址
     */
    public static final String MODULE_DOCUMENT = "clouddrive";

    /**
     * 用于指定视频格式为MP4
     */
    public static final String FORMAT_VIDEO_MP4 = "mp4";

    private String fileId;
    private String module;
    private String format;
    //文档预览 必填
    private String fileName;
    //非django文件ID 必填
    private String source;
    private long size;
    private String ext;
    private long createTime;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取默认格式的转换后的内容或内容地址，视频为 m3u8 文件内容。
     * @param fileId
     * @param module 文件播放模块名, 例如: 视频播放 {@link #MODULE_VIDEO}, 文档预览 {@link #MODULE_DOCUMENT}
     */
    public StreamApiReq(String fileId, String module) {
        this.fileId = fileId;
        this.module = module;
    }

    /**
     * 获取指定格式的转换后的内容或内容地址
     * @param fileId
     * @param module 文件播放模块名, 例如: 视频播放 {@link #MODULE_VIDEO}, 文档预览 {@link #MODULE_DOCUMENT}
     * @param format 提供给转换服务使用的format参数，比如 module 参数指定为mts时，这里可以指定为{@link #FORMAT_VIDEO_MP4}，
     *               那么转换服务会返回一个或两个MP4格式的文件地址。
     */
    public StreamApiReq(String fileId, String module,String format) {
        this.fileId = fileId;
        this.module = module;
        this.format = format;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "StreamApiReq{" +
                "fileId='" + fileId + '\'' +
                ", module='" + module + '\'' +
                ", format='" + format + '\'' +
                '}';
    }
}

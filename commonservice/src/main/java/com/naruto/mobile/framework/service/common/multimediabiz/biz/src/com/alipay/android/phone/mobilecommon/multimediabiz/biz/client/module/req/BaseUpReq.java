package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req;


import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.TransferredListener;

/**
 * 上传请求基类
 * Created by jinmin on 15/8/7.
 */
public class BaseUpReq<T> {

    public static final int DEFAULT_SLICE_LENGTH = 32 << 10;

    protected String md5;
    protected String gcid;
    protected String ext;
    protected T inputSource;
    protected String fileName;
    protected int startPos;
    protected int endPos = -1;
    protected long totalLength;
    protected boolean skipRapid;
    protected TransferredListener transferedListener;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public T getInputSource() {
        return inputSource;
    }

    public void setInputSource(T inputSource) {
        this.inputSource = inputSource;
    }

    public TransferredListener getTransferedListener() {
        return transferedListener;
    }

    public void setTransferedListener(TransferredListener transferedListener) {
        this.transferedListener = transferedListener;
    }

    public String getGcid() {
        return gcid;
    }

    public void setGcid(String gcid) {
        this.gcid = gcid;
    }

    /**
     * 文件扩展名
     * @return
     */
    public String getExt() {
        return ext;
    }

    /**
     * 可选,文件扩展名
     * @return
     */
    public void setExt(String ext) {
        this.ext = ext;
    }

    /**
     * 获取断点续上传开始位置
     * @return
     */
    public int getStartPos() {
        return startPos;
    }

    /**
     * 设置断点续上传位置
     * @param startPos
     */
    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    /**
     * 是否跳过秒传检测
     * @return
     */
    public boolean isSkipRapid() {
        return skipRapid;
    }

    /**
     * 设置是否跳过秒传检测
     * @param skipRapid
     */
    public void setSkipRapid(boolean skipRapid) {
        this.skipRapid = skipRapid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

    @Override
    public String toString() {
        return "BaseUpReq{" +
                "md5='" + md5 + '\'' +
                ", gcid='" + gcid + '\'' +
                ", ext='" + ext + '\'' +
                ", inputSource=" + inputSource +
                ", fileName='" + fileName + '\'' +
                ", startPos=" + startPos +
                ", endPos=" + endPos +
                ", totalLength=" + totalLength +
                ", skipRapid=" + skipRapid +
                ", transferedListener=" + transferedListener +
                '}';
    }

}

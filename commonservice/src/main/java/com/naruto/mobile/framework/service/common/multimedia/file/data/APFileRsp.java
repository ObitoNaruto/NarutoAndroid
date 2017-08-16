package com.naruto.mobile.framework.service.common.multimedia.file.data;


public class APFileRsp {

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_ERR_EXCEPTION = 1;
    public static final int CODE_ERR_RSP_NULL = 2;
    public static final int CODE_ERR_FILE_SIZE_ZERO = 3;
    public static final int CODE_ERR_FILE_MD5_WRONG = 4;
    public static final int CODE_ERR_TASK_CANCELED = 5;
    public static final int CODE_ERR_FILE_SIZE_WRONG = 6;
    public static final int CODE_ERR_PATH_EMPTY = 7;
    public static final int CODE_ERR_VIEW_REUSED = 8;

    private APFileReq fileReq;
    private int retCode;
    private String msg;
    private String traceId;

    public int getRetCode() {
        return retCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public APFileReq getFileReq() {
        return fileReq;
    }

    public void setFileReq(APFileReq fileReq) {
        this.fileReq = fileReq;
    }

    @Override
    public String toString() {
        return "APFileRsp {" +
                "fileReq='" + fileReq + '\'' +
                ", retCode=" + retCode +
                ", msg=" + msg +
                '}';
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}

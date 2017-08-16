package com.naruto.mobile.framework.service.common.multimedia.graphics.data;

/**
 * 结构返回体
 *
 */
public class APImageRetMsg {
    /**
     * 返回码
     */
    private RETCODE code;
    /**
     * 返回消息
     */
    private String msg;

    public RETCODE getCode() {
        return code;
    }

    public void setCode(RETCODE code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public enum RETCODE {
        SUC(0),
        CONNTIMEOUT(1),
        DNSTIMEOUT(2),
        STREAMERROR(3),
        COMPRESS_ERROR(4),
        PARAM_ERROR(5),
        INVALID_ACL(6),
        INVALID_TOKEN(7),
        GET_TOKEN_FAILED(8),
        NO_PRIVILEGE(9),
        INVALID_CODE(10),
        UPLOAD_ERROR(11),
        MD5_FAILED(12),
        STORE_FAILED(13),
        INVALID_DJANGO(14),
        INCONSISTENT_CHUNK_NUM(15),
        INCONSISTENT_SIZE(16),
        INVALID_APPKEY(17),
        FILE_IS_EXISTED(18),
        FILE_NOT_EXIST(19),
        DB_FAILED(20),
        CACHE_FAILED(21),
        TFS_READ_FAILED(22),
        TAIR_READ_FAILED(23),
        DOWNLOAD_FAILED(24),
        UNKNOWN_ERROR(25),
        CANCEL(26),
        REUSE(27),
        INVALID_NETWORK(28);

        int code;
        RETCODE(int code) {
            this.code = code;
        }
    }
}

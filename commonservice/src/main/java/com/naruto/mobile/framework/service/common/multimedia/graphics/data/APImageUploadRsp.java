package com.naruto.mobile.framework.service.common.multimedia.graphics.data;


import com.naruto.mobile.framework.service.common.multimedia.api.data.APMultimediaTaskModel;

/**
 * 文件上传工程返回结构
 * 
 * @author xiaofeng.dxf
 * 
 */
public class APImageUploadRsp
{
	/**
	 * 返回码跟提示消息
	 */
	private APImageRetMsg retmsg;
	/**
	 * 创建时间
	 */
	private long createTime;

	/**
	 * 文件md5值
	 */
	private String md5;

	/**
	 * 文件类型
	 */
	private int filetype;// type 109为图片
	/**
	 * 文件名
	 */
	private String filename;// name,文件名

	/**
	 * 原始文件路径,就是上传的时候传进来的filepath
	 */
	private String orgFilePath;
	/**
	 * 原始文件MD5,是压缩前的MD5值
	 */
	private String orgMd5;

    //django返回的上传文件大小
    private long fileSize;

    /**
     * 上传的状态和进度
     */
    private APMultimediaTaskModel taskStatus;

	// ////////////////get and set方法//////////////////////


    public APImageRetMsg getRetmsg() {
        return retmsg;
    }

    public void setRetmsg(APImageRetMsg retmsg) {
        this.retmsg = retmsg;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getFiletype() {
        return filetype;
    }

    public void setFiletype(int filetype) {
        this.filetype = filetype;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getOrgFilePath() {
        return orgFilePath;
    }

    public void setOrgFilePath(String orgFilePath) {
        this.orgFilePath = orgFilePath;
    }

    public String getOrgMd5() {
        return orgMd5;
    }

    public void setOrgMd5(String orgMd5) {
        this.orgMd5 = orgMd5;
    }

    public long getFileSize(){
        return this.fileSize;
    }

    public void setFileSize(long size){
        this.fileSize = size;
    }

    public APMultimediaTaskModel getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(APMultimediaTaskModel taskStatus) {
        this.taskStatus = taskStatus;
    }
}

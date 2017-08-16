package com.naruto.mobile.framework.service.common.multimedia.graphics.data;

public class APImageDeleteRsp
{
	/**
	 * 返回码跟提示消息
	 */
	private APImageRetMsg retmsg;

	/**
	 * 删除失败的时候,返回删除失败的fileid,成功的时候为空
	 */
	private String fileId;

	public APImageRetMsg getRetmsg()
	{
		return retmsg;
	}

	public void setRetmsg(APImageRetMsg retmsg)
	{
		this.retmsg = retmsg;
	}

	public String getFileId()
	{
		return fileId;
	}

	public void setFileId(String fileId)
	{
		this.fileId = fileId;
	}

}

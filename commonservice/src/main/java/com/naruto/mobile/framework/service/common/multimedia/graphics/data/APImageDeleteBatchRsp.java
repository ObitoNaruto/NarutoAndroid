package com.naruto.mobile.framework.service.common.multimedia.graphics.data;

/**
 * 删除失败的时候返回
 * 
 * @author xiaofeng.dxf
 * 
 */
public class APImageDeleteBatchRsp
{
	/**
	 * 返回码跟提示消息
	 */
	private APImageRetMsg retmsg;

	/**
	 * 删除失败的时候,返回删除失败的fileid列表,| 分隔,如果没有失败的,则为空
	 */
	private String fileIds;

	public APImageRetMsg getRetmsg()
	{
		return retmsg;
	}

	public void setRetmsg(APImageRetMsg retmsg)
	{
		this.retmsg = retmsg;
	}

	public String getFileIds()
	{
		return fileIds;
	}

	public void setFileIds(String fileIds)
	{
		this.fileIds = fileIds;
	}

}

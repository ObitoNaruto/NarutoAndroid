package com.naruto.mobile.framework.service.common.multimedia.graphics.data;

public class APImageDownloadBatchRsp
{
	/**
	 * 返回码跟提示消息
	 */
	private APImageRetMsg retmsg;

	private singleImage images[];

	public APImageRetMsg getRetmsg()
	{
		return retmsg;
	}

	public void setRetmsg(APImageRetMsg retmsg)
	{
		this.retmsg = retmsg;
	}

	public singleImage[] getImages()
	{
		return images;
	}

	public void setImages(singleImage[] images)
	{
		this.images = images;
	}

	class singleImage
	{
		/**
		 * 上传下载系统中的FileId
		 */
		private String FileId;
		/**
		 * 下载后保存文件的本地路径
		 */
		private String DFilePath;

		public String getFileId()
		{
			return FileId;
		}

		public void setFileId(String fileId)
		{
			FileId = fileId;
		}

		public String getDFilePath()
		{
			return DFilePath;
		}

		public void setDFilePath(String dFilePath)
		{
			DFilePath = dFilePath;
		}

	}
}

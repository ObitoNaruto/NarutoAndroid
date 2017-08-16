package com.naruto.mobile.framework.service.common.multimedia.video.data;


import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileReq;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileUploadRsp;

public class APVideoUploadRsp {
	public String mVideoId;
	public String mThumbId;
	public String mId;
	private APFileUploadRsp mRsp;
	
	public void setRsp(APFileUploadRsp rsp)
	{
		mRsp = rsp;
	}
    public int getRetCode() {
        return mRsp.getRetCode();
    }

    public String getMsg() {
        return mRsp.getMsg();
    }

    public APFileReq getFileReq() {
        return mRsp.getFileReq();
    }
    public APFileUploadRsp getRsp()
    {
    	return mRsp;
    }
    @Override
    public String toString() {
        return "APVideoUploadRsp {" +
                "fileReq='" + mRsp.getFileReq() + '\'' +
                ", retCode=" + mRsp.getRetCode() +
                ", msg=" + mRsp.getMsg() +
                '}';
    }
}

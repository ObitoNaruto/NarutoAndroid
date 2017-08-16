package com.naruto.mobile.framework.service.common.multimedia.video.data;


import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileDownloadRsp;
import com.naruto.mobile.framework.service.common.multimedia.file.data.APFileReq;

public class APVideoDownloadRsp {
	private APFileDownloadRsp mRsp;
	
	public void setRsp(APFileDownloadRsp rsp)
	{
		mRsp = rsp;
	}

    public void setRetCode(int ret) {
        mRsp.setRetCode(ret);
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

    @Override
    public String toString() {
        return "APFileDownloadRsp {" +
                "fileReq='" + mRsp.getFileReq() + '\'' +
                ", retCode=" + mRsp.getRetCode() +
                ", msg=" + mRsp.getMsg() +
                '}';
    }
}

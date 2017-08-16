/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import android.util.Log;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.apache.entity.mine.content.FileBody;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;


/**
 * 支持文件分块上传的FileBody
 * @author jinzhaoyu
 * <br>Modify Information:
 * <ol> 
 * 	<li>jinzhaoyu created 2014-3-21 下午3:34:04</li>
 * </ol>
 */
public class ChunkFileBody extends FileBody {
	/**
	 * 
	 */
	private static final int BUFFER_SIZE = 4096;
	private int chunkSequence;
	private long chunkSize;
	private long fileSize;
	private long chunkNumber; //总分块数
	private long readOffset;
	private long contentLength;
	
	/**
	 * @param file
	 * @param chunkSequence 要上传的块索引，从 1 开始
	 * @param chunkSize 文件分块，每块的大小
	 */
	public ChunkFileBody(File file,int chunkSequence,long chunkSize) {
		super(file);
		setChunkInfo(chunkSequence,chunkSize);
	}
	
	private void setChunkInfo(int chunkSequence,long chunkSize){
		if(chunkSize <= 0 || chunkSequence < 1){
			throw new IllegalArgumentException("Pls check parameter chunkSize ["+chunkSize+"] and chunkSequence ["+chunkSequence+"] !");
		}
		this.chunkSequence = chunkSequence;
		this.chunkSize = chunkSize;
		
		fileSize  = getFile().length();
		chunkNumber = fileSize / chunkSize ;
		if(fileSize % chunkSize != 0) chunkNumber++;
		
		//计算需要读取的字节数
		readOffset = (chunkSequence-1) * chunkSize ;
		contentLength = readOffset+chunkSize > fileSize ? fileSize - readOffset : chunkSize ;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.http.entity.mime.content.FileBody#writeTo(java.io.OutputStream)
	 */
	@Override
	public void writeTo(OutputStream out) throws IOException {
		if(chunkSequence > chunkNumber){
			Logger.W(DjangoClient.LOG_TAG, "ChunkSequence greater than ChunkNumber,quit !");
			return;
		}
		
		RandomAccessFile randomFile = null;
		try{
			randomFile = new RandomAccessFile(getFile(), "r"); 
			randomFile.seek(readOffset);
			
			//如果块大小小于Buffer_size，则使用块大小
			int readLength = (int)(contentLength > BUFFER_SIZE ? BUFFER_SIZE : contentLength);
			byte[] tmp = new byte[BUFFER_SIZE];
			int actulReadedLenght;
			long readCount = 0;
			while((actulReadedLenght = randomFile.read(tmp,0,readLength)) != -1){
				//actulReadedLenght shoud be equals readLength
				out.write(tmp, 0, actulReadedLenght);
				readCount+= actulReadedLenght;
				//防止读取字节数大于块大小
				if(readCount + BUFFER_SIZE > contentLength){
					readLength = (int)(contentLength - readCount);
					if(readLength <= 0) break;
				}
			}
			out.flush();
			tmp = null;
		}finally{
			if(randomFile != null){
				randomFile.close();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.apache.http.entity.mime.content.FileBody#getContentLength()
	 */
	@Override
	public long getContentLength() {
		return contentLength;
	}

	/**
	 * @return the chunkSize
	 */
	public long getChunkSize() {
		return chunkSize;
	}

	/**
	 * @return the chunkSequence
	 */
	public int getChunkSequence() {
		return chunkSequence;
	}

}

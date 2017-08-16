/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.impl;

import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.DjangoClient;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.FileApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.TokenApi;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.api.infos.FileApiInfo;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.config.ConnectionManager;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.exception.DjangoClientException;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.ProgressChunkFileBody;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.SliceProgressFileBody;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.io.SliceProgressInputStreamBody;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.BaseResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.BaseUpReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ChunkUpTxnCommitReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ChunkUpTxnOpenReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.ChunkUpTxnProcessReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FileOfflineUploadReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FileRapidUpReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FileUpReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FilesDelReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.FilesDownReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.GetFilesMetaReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.InputStreamUpReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.req.SetExtReq;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ChunkUpTxnCommitResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ChunkUpTxnOpenResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.ChunkUpTxnProcessResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.DjangoFileInfoResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FileOfflineUploadResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FileUpResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FilesDelResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.FilesDownResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.GetFilesMetaResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.module.resp.SetExtResp;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoConstant;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.DjangoUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.HttpClientUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util.LiteStringUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.apache.entity.mine.HttpMultipartMode;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.apache.entity.mine.MultipartEntityBuilder;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.http.apache.entity.mine.content.ContentBody;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.CommonUtils;
import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文件相关API具体操作类
 *
 * @author jinzhaoyu
 */
public class FileApiImpl extends AbstractApiImpl implements FileApi
{

	protected TokenApi tokenApi;

	public FileApiImpl(DjangoClient djangoClient, ConnectionManager<HttpClient> connectionManager)
	{
		super(djangoClient, connectionManager);
		tokenApi = djangoClient.getTokenApi();
	}

	/**
	 * 解析统一格式的文件信息Json
	 *
	 * @param responseClass
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	private <T extends BaseResp> T parseDjangoFileInfoResp(Class<T> responseClass, HttpResponse resp) throws Exception
	{
		T response;
		if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
		{
			HttpEntity entity = resp.getEntity();
			String content = EntityUtils.toString(entity, DjangoConstant.DEFAULT_CHARSET_NAME);

			if (DjangoClient.DEBUG)
			{

				Logger.D(DjangoClient.LOG_TAG, "DjangoFileInfoResp:" + content);
			}
			response = JSON.parseObject(content, responseClass);
		} else
		{
			response = responseClass.newInstance();
			response.setCode(resp.getStatusLine().getStatusCode());
			response.setMsg("http invoker error!");
		}
		return response;
	}

	/**
	 * 上传前进行秒传查询
	 */
	@Override
	public FileUpResp uploadDirectRapid(FileRapidUpReq fileRapidUpReq)
	{
		FileUpResp response = null;
		HttpGet method = null;
		HttpResponse resp = null;
		String traceId = null;
		try
		{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
			if (LiteStringUtils.isNotBlank(fileRapidUpReq.getMd5()))
			{
				params.add(new BasicNameValuePair("md5", fileRapidUpReq.getMd5()));
			}
			if (LiteStringUtils.isNotBlank(fileRapidUpReq.getGcid()))
			{
				params.add(new BasicNameValuePair("gcid", fileRapidUpReq.getGcid()));
			}
			if (LiteStringUtils.isNotBlank(fileRapidUpReq.getExt()))
			{
				params.add(new BasicNameValuePair("ext", fileRapidUpReq.getExt()));
			}
			traceId = getTraceId();
			if (LiteStringUtils.isNotBlank(traceId)) {
				params.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, traceId));
			}

            String api = FileApiInfo.UPLOAD_DIRECT_RAPID.getApi();
            if(CommonUtils.isWapNetWork()){
                api = FileApiInfo.UPLOAD_DIRECT_RAPID.getUrlApi();
                connectionManager.setProxy(FileApiInfo.UPLOAD_DIRECT_RAPID.getIp(),FileApiInfo.UPLOAD_DIRECT_RAPID.getHost());
            }
			method = new HttpGet(HttpClientUtils.urlAppendParams(api, params));
            method.addHeader("Host", FileApiInfo.UPLOAD_DIRECT_RAPID.getHost());
			// debug 信息
			if (DjangoClient.DEBUG)
			{
				//Log.d(DjangoClient.LOG_TAG, "cookie:" + getCookieString());
				Logger.D(DjangoClient.LOG_TAG, Arrays.toString(method.getAllHeaders()));
			}
			resp = connectionManager.getConnection().execute(method);

			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				HttpEntity entity = resp.getEntity();
				String content = EntityUtils.toString(entity, DjangoConstant.DEFAULT_CHARSET_NAME);
				if (DjangoClient.DEBUG)
				{
					Logger.D(DjangoClient.LOG_TAG, "uploadDirectRapid() :" + content);
				}
				response = JSON.parseObject(content, FileUpResp.class);
				if (response != null && response.isSuccess())
				{
					response.setRapid(true);
				}
			} else
			{
				response = new FileUpResp();
				response.setCode(resp.getStatusLine().getStatusCode());
				response.setMsg("Http invoker error: " + response.getCode());
			}
		} catch (Exception e)
		{
			Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
			response = new FileUpResp();
			response.setCode(DjangoConstant.DJANGO_400);
			response.setMsg(e.getMessage());
		} finally
		{
			if(response != null && !TextUtils.isEmpty(traceId)) {
				response.setTraceId(traceId);
			}
			DjangoUtils.releaseConnection(method, resp);
		}

		return response;
	}

//	@Override
//	public FileUpResp uploadDirect(FileUpReq fileUpReq) {
//		return uploadDirect(fileUpReq, false);
//	}

	/**
	 * 直接上传一个文件
	 */
	@Override
	public FileUpResp uploadDirect(FileUpReq fileUpReq)
	{
		FileUpResp response = null;
		HttpRequestBase method = null;
		HttpResponse resp = null;
		String traceId = null;
		try
		{
			if (fileUpReq.getFile() == null)
			{
				throw new DjangoClientException("Field[file] is null!");
			}
			// 先秒传查询
			boolean rapidSuccess = false;
            String md5 = LiteStringUtils.trimToEmpty(fileUpReq.getMd5());
            boolean isNotBlankMd5 = LiteStringUtils.isNotBlank(md5);
			if (!fileUpReq.isSkipRapid() && //先判断是否跳过秒传检测
					(isNotBlankMd5 || LiteStringUtils.isNotBlank(fileUpReq.getGcid()))) {
				FileRapidUpReq rapidUpReq = new FileRapidUpReq(fileUpReq.getMd5(), fileUpReq.getGcid());
				rapidUpReq.setExt(fileUpReq.getExt());
				response = uploadRapidRange(rapidUpReq);//更换为秒传和断点续上传查询接口， uploadDirectRapid -> uploadRapidRange
				rapidSuccess = response != null && response.isSuccess();
			}
			// 非秒传,上文件
			if (!rapidSuccess) {
                int startPos = response == null ? 0 : response.getRange();
                FileUpReq newReq = new FileUpReq(fileUpReq);
                newReq.setStartPos(startPos);
                response = uploadRange(newReq);
			}else{
                response.setRapid(true);
            }
		} catch (Exception e) {
			Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
			response = new FileUpResp();
			response.setCode(DjangoConstant.DJANGO_400);
            String msg = e.getMessage();
            if(TextUtils.isEmpty(msg)){
                msg = e.getClass().getSimpleName();
            }
			response.setMsg(msg);
		}

		return response;
	}

	/**
	 * 上传文件
	 */
	@Override
	public FileUpResp uploadDirect(InputStreamUpReq inputStreamUpReq)
	{
		FileUpResp response = null;
		try
		{
			if (inputStreamUpReq.getInputStream() == null || LiteStringUtils.isBlank(inputStreamUpReq.getFileName()))
			{
				throw new DjangoClientException("Field[inputStream] or [fileName] is null!");
			}
			// 先秒传查询
			boolean rapidSuccess = false;
            String md5 = LiteStringUtils.trimToEmpty(inputStreamUpReq.getMd5());
            boolean isNotBlankMd5 = LiteStringUtils.isNotBlank(md5);
			if (!inputStreamUpReq.isSkipRapid() && (isNotBlankMd5
					|| LiteStringUtils.isNotBlank(inputStreamUpReq.getGcid())))
			{
				FileRapidUpReq rapidUpReq = new FileRapidUpReq(md5, inputStreamUpReq.getGcid());
				rapidUpReq.setExt(inputStreamUpReq.getExt());
				response = uploadRapidRange(rapidUpReq);//更换为秒传和断点续上传查询接口， uploadDirectRapid -> uploadRapidRange
				rapidSuccess = response != null && response.isSuccess();
			}
			// 非秒传,上文件
			if (!rapidSuccess)
			{
                int offset = response != null ? response.getRange() : 0;
                inputStreamUpReq.setStartPos(offset);
                response = uploadRange(inputStreamUpReq);
			}
		} catch (Exception e) {
			Logger.E(DjangoClient.LOG_TAG, e,e.getMessage());
			response = new FileUpResp();
			response.setCode(DjangoConstant.DJANGO_400);
            String msg = e.getMessage();
            if(TextUtils.isEmpty(msg)){
                msg = e.getClass().getSimpleName();
            }
			response.setMsg(msg);
		}

		return response;
	}

	@Override
	public FileUpResp uploadRange(FileUpReq fileUpReq) throws DjangoClientException {
		HttpRequestBase method = null;
		HttpResponse resp = null;
		FileUpResp response = null;
		if (fileUpReq == null || fileUpReq.getFile() == null) {
			throw new DjangoClientException("Field[file] is null!");
		}
		String traceId = "";
		try {
			HttpEntity entity = createUploadEntity(fileUpReq,
					new SliceProgressFileBody(fileUpReq.getFile(), fileUpReq.getFileName(),
							fileUpReq.getStartPos(), fileUpReq.getEndPos(), fileUpReq.getTransferedListener()));
			traceId = getTraceId();
			method = createHttpRequest(fileUpReq, entity, traceId);
			resp = connectionManager.getConnection().execute(method);

			response = parseRangeResponse(resp);
		} catch (Exception e) {
			Logger.E(DjangoClient.LOG_TAG, "uploadRange req: " + fileUpReq, e);
		} finally {
			if(response != null && !TextUtils.isEmpty(traceId)) {
				response.setTraceId(traceId);
			}
			DjangoUtils.releaseConnection(method, resp);
		}
		return response;
	}

	@Override
	public FileUpResp uploadRange(InputStreamUpReq upReq) throws DjangoClientException {
		HttpRequestBase method = null;
		HttpResponse resp = null;
		FileUpResp response = null;
		if (upReq == null || upReq.getInputStream() == null) {
			throw new DjangoClientException("Field[file] is null!");
		}
		String traceId = "";
		try {
			HttpEntity entity = createUploadEntity(upReq,
					new SliceProgressInputStreamBody(upReq.getInputStream(),
							upReq.getFileName(), upReq.getStartPos(), upReq.getEndPos(),
							upReq.getTransferedListener()));
			traceId = getTraceId();
			method = createHttpRequest(upReq, entity, traceId);
			resp = connectionManager.getConnection().execute(method);

			response = parseRangeResponse(resp);
		} catch (Exception e) {
			Logger.E(DjangoClient.LOG_TAG, "uploadRange req: " + upReq, e);
		} finally {
			if(response != null && !TextUtils.isEmpty(traceId)) {
				response.setTraceId(traceId);
			}
			DjangoUtils.releaseConnection(method, resp);
		}
		return response;
	}

	private FileUpResp parseRangeResponse(HttpResponse response) throws Exception {
		FileUpResp resp = null;
		if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity entity = response.getEntity();
			String content = EntityUtils.toString(entity, DjangoConstant.DEFAULT_CHARSET_NAME);
			if (DjangoClient.DEBUG) {
				Logger.D(DjangoClient.LOG_TAG, "uploadRapidRange() :" + content);
			}
			resp = new FileUpResp();
			JSONObject js = new JSONObject(content);
			int code = js.getInt("code");
			if (code == DjangoConstant.DJANGO_OK) {//正常命中秒传
				DjangoFileInfoResp fileInfoResp = JSON.parseObject(js.getString("data"), DjangoFileInfoResp.class);
				resp.setFileInfo(fileInfoResp);
				resp.setRapid(true);
				resp.setCode(DjangoConstant.DJANGO_OK);
			} else if (code == HttpStatus.SC_PARTIAL_CONTENT) {//断点续上传结果返回
				resp.setCode(code);
				resp.setRapid(false);
				resp.setRange(js.getInt("data"));
			} else {//发生异常了
				resp.setCode(code);
				resp.setRapid(false);
			}
		} else {
			resp = new FileUpResp();
			resp.setCode(response == null ? DjangoConstant.DJANGO_400 : response.getStatusLine().getStatusCode());
			resp.setMsg("Http invoker error: " + resp.getCode());
		}
		Logger.D(DjangoClient.LOG_TAG, "uploadRsp: " + response);
		return resp;
	}

	private HttpEntity createUploadEntity(BaseUpReq req, ContentBody filePartBody) throws DjangoClientException {
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
		multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		multipartEntityBuilder.setCharset(DjangoConstant.DEFAULT_CHARSET);
		multipartEntityBuilder.addTextBody("token", tokenApi.getTokenString());
		if (!TextUtils.isEmpty(req.getMd5())) {
			multipartEntityBuilder.addTextBody("md5", req.getMd5());
		}

		multipartEntityBuilder.addPart("file", filePartBody);
		return multipartEntityBuilder.build();
	}

	private HttpRequestBase createHttpRequest(BaseUpReq upReq, HttpEntity entity, String traceId) {
		long offset = upReq.getStartPos();
		String md5 = upReq.getMd5();
		boolean isNotBlankMd5 = LiteStringUtils.isNotBlank(md5);
		List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
		if (LiteStringUtils.isNotBlank(traceId)) {
			urlParams.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, traceId));
		}
		if (isNotBlankMd5) {
			urlParams.add(new BasicNameValuePair("md5", md5));
		}

		FileApiInfo apiInfo = isNotBlankMd5 ? FileApiInfo.UPLOAD_FILE_RANGE : FileApiInfo.UPLOAD_DIRECT;
		String api = apiInfo.getApi();
		if (CommonUtils.isWapNetWork()) {
			api = apiInfo.getUrlApi();
			connectionManager.setProxy(apiInfo.getIp(), apiInfo.getHost());
		}
		HttpPost method = new HttpPost(HttpClientUtils.urlAppendParams(api, urlParams));
		method.expectContinue();
		method.setEntity(entity);
		method.addHeader("Host", apiInfo.getHost());
		method.addHeader("Cookie", getCookieString());
		if (isNotBlankMd5 && (offset > 0 || upReq.getEndPos() > 0)) {
			long length = upReq.getTotalLength();
			StringBuilder rangeBuilder = new StringBuilder("bytes=").append(offset).append("-");
			if (upReq.getEndPos() > 0 && upReq.getEndPos() > offset) {
				rangeBuilder.append(upReq.getEndPos());
			}
			rangeBuilder.append("/").append(length);
			method.addHeader("Range", rangeBuilder.toString());
		}
		// debug 信息
		if (DjangoClient.DEBUG) {
			//Logger.D(DjangoClient.LOG_TAG, "cookie:" + getCookieString());
			Logger.D(DjangoClient.LOG_TAG, "createHttpRequest: " + Arrays.toString(method.getAllHeaders()));
		}
		return method;
	}


	@Override
	public ChunkUpTxnOpenResp uploadChunkOpen(ChunkUpTxnOpenReq openReq)
	{
		ChunkUpTxnOpenResp response = null;
		HttpGet method = null;
		HttpResponse resp = null;
		String traceId = "";
		try
		{
			long fileSize = openReq.getSize();
			if (fileSize <= 0)
			{
				throw new DjangoClientException("file is empty");
			}
			long chunkSize = openReq.getChunkSize();
			long chunkNumber = openReq.getNumber();
			if (chunkSize <= 0 && chunkNumber <= 0)
			{
				throw new DjangoClientException("Must give chunk size or chunk number");
			}

			if (chunkSize > 0)
			{
				chunkNumber = fileSize / chunkSize;
				if (fileSize % chunkSize != 0)
					chunkNumber++;
			}

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
			params.add(new BasicNameValuePair("md5", LiteStringUtils.trimToEmpty(openReq.getMd5())));
			params.add(new BasicNameValuePair("size", String.valueOf(fileSize)));
			params.add(new BasicNameValuePair("number", String.valueOf(chunkNumber)));
			params.add(new BasicNameValuePair("ext", LiteStringUtils.trimToEmpty(openReq.getExtension())));
			traceId = getTraceId();
			if (LiteStringUtils.isNotBlank(traceId)) {
				params.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, traceId));
			}

			// URI uri = new
			// URIBuilder(FileApiInfo.UPLOAD_CHUNK_OPEN.getApi()).setParameters(params).build();
            String api = FileApiInfo.UPLOAD_CHUNK_OPEN.getApi();
            if(CommonUtils.isWapNetWork()){
                api = FileApiInfo.UPLOAD_CHUNK_OPEN.getUrlApi();
                connectionManager.setProxy(FileApiInfo.UPLOAD_CHUNK_OPEN.getIp(),FileApiInfo.UPLOAD_CHUNK_OPEN.getHost());
            }
			method = new HttpGet(HttpClientUtils.urlAppendParams(api, params));
            method.addHeader("Host", FileApiInfo.UPLOAD_CHUNK_OPEN.getHost());
			// debug 信息
			if (DjangoClient.DEBUG)
			{
				//Logger.D(DjangoClient.LOG_TAG, "cookie:" + getCookieString());
				Logger.D(DjangoClient.LOG_TAG, Arrays.toString(method.getAllHeaders()));
			}
			resp = connectionManager.getConnection().execute(method);
			response = parseDjangoFileInfoResp(ChunkUpTxnOpenResp.class, resp);
		} catch (Exception e)
		{
			Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
			response = new ChunkUpTxnOpenResp();
			response.setCode(DjangoConstant.DJANGO_400);
			response.setMsg(e.getMessage());
		} finally
		{
			if (response != null) {
				response.setTraceId(traceId);
			}
			DjangoUtils.releaseConnection(method, resp);
		}

		return response;
	}

	@Override
	public ChunkUpTxnProcessResp uploadChunkProcessRapid(ChunkUpTxnProcessReq processReq)
	{
		ChunkUpTxnProcessResp response = null;
		HttpGet method = null;
		HttpResponse resp = null;
		String traceId = "";
		try
		{
			if (LiteStringUtils.isBlank(processReq.getMd5()) && LiteStringUtils.isBlank(processReq.getGcid()))
			{
				throw new IllegalArgumentException(
						"Parameter processReq.getMd5() or processReq.getGcid() can not be null !");
			}

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
			params.add(new BasicNameValuePair("fileId", processReq.getFileId()));
			params.add(new BasicNameValuePair("sequence", String.valueOf(processReq.getSequence())));
			if (LiteStringUtils.isNotBlank(processReq.getMd5()))
			{
				params.add(new BasicNameValuePair("md5", processReq.getMd5()));
			}
			if (LiteStringUtils.isNotBlank(processReq.getGcid()))
			{
				params.add(new BasicNameValuePair("gcid", processReq.getGcid()));
			}
			traceId = getTraceId();
			if (LiteStringUtils.isNotBlank(traceId)) {
				params.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, traceId));
			}

			// URI uri = new
			// URIBuilder(FileApiInfo.UPLOAD_CHUNK_PROCESS_RAPID.getApi()).setParameters(params).build();
            String api = FileApiInfo.UPLOAD_CHUNK_PROCESS_RAPID.getApi();
            if(CommonUtils.isWapNetWork()){
                api = FileApiInfo.UPLOAD_CHUNK_PROCESS_RAPID.getUrlApi();
                connectionManager.setProxy(FileApiInfo.UPLOAD_CHUNK_PROCESS_RAPID.getIp(),FileApiInfo.UPLOAD_CHUNK_PROCESS_RAPID.getHost());
            }
            method = new HttpGet(HttpClientUtils.urlAppendParams(api, params));

            method.addHeader("Host", FileApiInfo.UPLOAD_CHUNK_PROCESS_RAPID.getHost());
			// debug 信息
			if (DjangoClient.DEBUG)
			{
				//Logger.D(DjangoClient.LOG_TAG, "cookie:" + getCookieString());
				Logger.D(DjangoClient.LOG_TAG, Arrays.toString(method.getAllHeaders()));
			}
			resp = connectionManager.getConnection().execute(method);

			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				HttpEntity entity = resp.getEntity();
				String content = EntityUtils.toString(entity, DjangoConstant.DEFAULT_CHARSET_NAME);
				if (DjangoClient.DEBUG)
				{
					Logger.D(DjangoClient.LOG_TAG, "uploadChunkProcessRapid() :" + content);
				}
				response = JSON.parseObject(content, ChunkUpTxnProcessResp.class);
				if (response != null && response.isSuccess())
				{
					response.setRapid(true);
				}
			} else
			{
				response = new ChunkUpTxnProcessResp();
				response.setCode(resp.getStatusLine().getStatusCode());
				response.setMsg("Http invoker error: " + response.getCode());
			}
		} catch (Exception e)
		{
			Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
			response = new ChunkUpTxnProcessResp();
			response.setCode(DjangoConstant.DJANGO_400);
			response.setMsg(e.getMessage());
		} finally
		{
			if (response != null) {
				response.setTraceId(traceId);
			}
			DjangoUtils.releaseConnection(method, resp);
		}

		return response;
	}

	@Override
	public ChunkUpTxnProcessResp uploadChunkProcess(ChunkUpTxnProcessReq processReq)
	{
		ChunkUpTxnProcessResp response = null;
		HttpPost method = null;
		HttpResponse resp = null;
		String traceId = "";
		try
		{
			int sequence = processReq.getSequence();

			if (LiteStringUtils.isBlank(processReq.getFileId()))
			{
				throw new DjangoClientException("field[fileId] is null");
			} else if (sequence < 0)
			{
				throw new DjangoClientException("field[sequence] must greater than 0");
			} else if (processReq.getFile() == null)
			{
				throw new DjangoClientException("field[file] is null");
			} else if (processReq.getChunkSize() < 1)
			{
				throw new DjangoClientException("field[chunkSize] must greater than 0");
			}

			boolean isRapid = false;
			if (LiteStringUtils.isNotBlank(processReq.getMd5()) || LiteStringUtils.isNotBlank(processReq.getGcid()))
			{
				response = uploadChunkProcessRapid(processReq);
				isRapid = response != null && response.isSuccess();
			}

			if (!isRapid)
			{
				String token = tokenApi.getTokenString();
				List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
				traceId = getTraceId();
				if (LiteStringUtils.isNotBlank(traceId)) {
					urlParams.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, traceId));
				}
				
				String api = FileApiInfo.UPLOAD_CHUNK_PROCESS.getApi();
                if(CommonUtils.isWapNetWork()){
                    api = FileApiInfo.UPLOAD_CHUNK_PROCESS.getUrlApi();
                    connectionManager.setProxy(FileApiInfo.UPLOAD_CHUNK_PROCESS.getIp(),FileApiInfo.UPLOAD_CHUNK_PROCESS.getHost());
                }
				
				method = new HttpPost(HttpClientUtils.urlAppendParams(api, urlParams));
                method.addHeader("Host", FileApiInfo.UPLOAD_CHUNK_PROCESS.getHost());
				MultipartEntityBuilder multipartEntityBuilder = genMultipartEntityBuilder();
				multipartEntityBuilder.addTextBody("token", token);
				multipartEntityBuilder.addTextBody("md5", LiteStringUtils.trimToEmpty(processReq.getMd5()));
				multipartEntityBuilder.addTextBody("fileId", processReq.getFileId());
				multipartEntityBuilder.addTextBody("sequence", String.valueOf(sequence));
				long chunkSize = processReq.getChunkSize();
				ProgressChunkFileBody fileBody = new ProgressChunkFileBody(processReq.getFile(), sequence, chunkSize,
						processReq.getChunkTransListener());
				multipartEntityBuilder.addPart("file", fileBody);
				method.setEntity(multipartEntityBuilder.build());
				// debug 信息
				if (DjangoClient.DEBUG)
				{
					//Logger.D(DjangoClient.LOG_TAG, "cookie:" + getCookieString());
					Logger.D(DjangoClient.LOG_TAG, Arrays.toString(method.getAllHeaders()));
				}
				resp = connectionManager.getConnection().execute(method);

				if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				{
					HttpEntity entity = resp.getEntity();
					String content = EntityUtils.toString(entity, DjangoConstant.DEFAULT_CHARSET_NAME);
					if (DjangoClient.DEBUG)
					{
						Logger.D(DjangoClient.LOG_TAG, "chunkUpTxnProcess() :" + content);
					}
					response = JSON.parseObject(content, ChunkUpTxnProcessResp.class);
				} else
				{
					response = new ChunkUpTxnProcessResp();
					response.setCode(resp.getStatusLine().getStatusCode());
					response.setMsg("Http invoker error: " + response.getCode());
				}
			} else if(processReq.getChunkTransListener() != null) {
				processReq.getChunkTransListener().onChunkTransferred(processReq.getSequence(), processReq.getRealChunkSize());
			}
		} catch (Exception e)
		{
			Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
			response = new ChunkUpTxnProcessResp();
			response.setCode(DjangoConstant.DJANGO_400);
			response.setMsg(e.getMessage());
		} finally
		{
			if (response != null) {
				response.setTraceId(traceId);
			}
			DjangoUtils.releaseConnection(method, resp);
		}

		return response;
	}

	@Override
	public ChunkUpTxnCommitResp uploadChunkCommit(ChunkUpTxnCommitReq commitReq)
	{
		ChunkUpTxnCommitResp response = null;
		HttpPost method = null;
		HttpResponse resp = null;
		String traceId = "";
		try
		{
			if (LiteStringUtils.isBlank(commitReq.getFileId()))
			{
				throw new DjangoClientException("field[fileId] is null");
			}

			String timestampStr = String.valueOf(System.currentTimeMillis());
			String acl = genAclString(commitReq.getFileId(), timestampStr);

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
			params.add(new BasicNameValuePair("fileId", commitReq.getFileId()));
			params.add(new BasicNameValuePair("timestamp", timestampStr));
			params.add(new BasicNameValuePair("acl", acl));

			List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
			traceId = getTraceId();
			if (LiteStringUtils.isNotBlank(traceId)) {
				urlParams.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, traceId));
			}

			// URI uri = new
			// URIBuilder(FileApiInfo.UPLOAD_CHUNK_COMMIT.getApi()).setParameters(params).build();
			String api = FileApiInfo.UPLOAD_CHUNK_COMMIT.getApi();
            if(CommonUtils.isWapNetWork()){
                api = FileApiInfo.UPLOAD_CHUNK_COMMIT.getUrlApi();
                connectionManager.setProxy(FileApiInfo.UPLOAD_CHUNK_COMMIT.getIp(),FileApiInfo.UPLOAD_CHUNK_COMMIT.getHost());
            }

			method = new HttpPost(HttpClientUtils.urlAppendParams(api, urlParams));
			method.setEntity(new UrlEncodedFormEntity(params));

			// MultipartEntityBuilder entityBuilder =
			// genMultipartEntityBuilder();
			// entityBuilder.addTextBody("token",tokenApi.getTokenString());
			// entityBuilder.addTextBody("fileId",commitReq.getFileId());
			// entityBuilder.addTextBody("timestamp",timestampStr);
			// entityBuilder.addTextBody("acl",acl);
			// method.setEntity(entityBuilder.build());
            method.addHeader("Host", FileApiInfo.UPLOAD_CHUNK_COMMIT.getHost());
			method.addHeader("Cookie", getCookieString());
			// debug 信息
			if (DjangoClient.DEBUG)
			{
				//Logger.D(DjangoClient.LOG_TAG, "cookie:" + getCookieString());
				Logger.D(DjangoClient.LOG_TAG, Arrays.toString(method.getAllHeaders()));
			}
			resp = connectionManager.getConnection().execute(method);
			response = parseDjangoFileInfoResp(ChunkUpTxnCommitResp.class, resp);
		} catch (Exception e)
		{
			Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
			response = new ChunkUpTxnCommitResp();
			response.setCode(DjangoConstant.DJANGO_400);
			response.setMsg(e.getMessage());
		} finally
		{
			if (response != null) {
				response.setTraceId(traceId);
			}
			DjangoUtils.releaseConnection(method, resp);
		}
		return response;
	}

	/**
	 * 获取文件的元数据信息
	 */
	@Override
	public GetFilesMetaResp getFilesMeta(GetFilesMetaReq req)
	{
		GetFilesMetaResp response = null;
		HttpGet method = null;
		HttpResponse resp = null;

		try
		{
			if (LiteStringUtils.isBlank(req.getFileIds()))
			{
				throw new DjangoClientException("field[fileIds] is null");
			}

			String timestampStr = String.valueOf(System.currentTimeMillis());
			String acl = genAclString(req.getFileIds(), timestampStr);

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
			params.add(new BasicNameValuePair("fileIds", req.getFileIds()));
			params.add(new BasicNameValuePair("timestamp", timestampStr));
			params.add(new BasicNameValuePair("acl", acl));
			String traceId = getTraceId();
			if (LiteStringUtils.isNotBlank(traceId)) {
				params.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, traceId));
			}

			// URI uri = new
			// URIBuilder(FileApiInfo.GET_FILES_META.getApi()).setParameters(params).build();
            String api = FileApiInfo.GET_FILES_META.getApi();
            if(CommonUtils.isWapNetWork()){
                api = FileApiInfo.GET_FILES_META.getUrlApi();
                connectionManager.setProxy(FileApiInfo.GET_FILES_META.getIp(),FileApiInfo.GET_FILES_META.getHost());
            }
			method = new HttpGet(HttpClientUtils.urlAppendParams(api, params));
            method.addHeader("Host", FileApiInfo.GET_FILES_META.getHost());
            method.addHeader("Cookie", getCookieString());
			// debug 信息
			if (DjangoClient.DEBUG)
			{
				//Logger.D(DjangoClient.LOG_TAG, "cookie:" + getCookieString());
				Logger.D(DjangoClient.LOG_TAG, Arrays.toString(method.getAllHeaders()));
			}
			resp = connectionManager.getConnection().execute(method);

			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				HttpEntity entity = resp.getEntity();
				String content = EntityUtils.toString(entity, DjangoConstant.DEFAULT_CHARSET_NAME);
				if (DjangoClient.DEBUG)
				{
					Logger.D(DjangoClient.LOG_TAG, "getFilesMeta() :" + content);
				}
				response = JSON.parseObject(content, GetFilesMetaResp.class);
			} else
			{
				response = new GetFilesMetaResp();
				response.setCode(resp.getStatusLine().getStatusCode());
				response.setMsg("http invoker error!");
			}
		} catch (Exception e)
		{
			Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
			response = new GetFilesMetaResp();
			response.setCode(DjangoConstant.DJANGO_400);
			response.setMsg(e.getMessage());
		} finally
		{
			DjangoUtils.releaseConnection(method, resp);
		}

		return response;
	}

	/**
	 * 下载文件
	 */
	@Override
	public FilesDownResp downloadBatch(FilesDownReq req)
	{
		FilesDownResp response = new FilesDownResp();
		HttpGet method = null;
		HttpResponse resp = null;
		String traceId = null;
		try
		{
			if (LiteStringUtils.isBlank(req.getFileIds()))
			{
				throw new DjangoClientException("field[fileIds] is null");
			}

			String timestampStr = String.valueOf(System.currentTimeMillis());
			String acl = genAclString(req.getFileIds(), timestampStr);

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
			params.add(new BasicNameValuePair("fileIds", req.getFileIds()));
			params.add(new BasicNameValuePair("timestamp", timestampStr));
			params.add(new BasicNameValuePair("acl", acl));
			if (LiteStringUtils.isNotBlank(req.getSource()))
			{
				params.add(new BasicNameValuePair("source", req.getSource()));
			}
			traceId = getTraceId();
			if (LiteStringUtils.isNotBlank(traceId)) {
				params.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, traceId));
			}

			// URI uri = new URIBuilder(FileApiInfo.DOWNLOAD_BATCH.getApi()+ "?"
			// + req.getUrlParameter()).setParameters(params).build();
            String api = FileApiInfo.DOWNLOAD_BATCH.getApi();
            if(CommonUtils.isWapNetWork()){
                api = FileApiInfo.DOWNLOAD_BATCH.getUrlApi();
                connectionManager.setProxy(FileApiInfo.DOWNLOAD_BATCH.getIp(),FileApiInfo.DOWNLOAD_BATCH.getHost());
            }
			method = new HttpGet(HttpClientUtils.urlAppendParams(api, params));
            method.addHeader("Host", FileApiInfo.DOWNLOAD_BATCH.getHost());
            method.addHeader("Cookie", getCookieString());

			if (LiteStringUtils.isNotBlank(req.getRange()))
			{
				method.addHeader("Range", req.getRange());
			}
			// debug 信息
			if (DjangoClient.DEBUG)
			{
				//Logger.D(DjangoClient.LOG_TAG, "cookie:" + getCookieString());
				Logger.D(DjangoClient.LOG_TAG, Arrays.toString(method.getAllHeaders()));
			}
			resp = connectionManager.getConnection().execute(method);
			int statusCode = resp.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_PARTIAL_CONTENT)
			{
				response.setResp(resp);
				response.setCode(DjangoConstant.DJANGO_OK);
			} else
			{
				response.setCode(statusCode);
				response.setMsg("http invoker error!");
			}
			response.setMethod(method);
		} catch (Exception e)
		{
			Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
			response.setCode(DjangoConstant.DJANGO_400);
			response.setMsg(e.getMessage());
			DjangoUtils.releaseConnection(method, resp);
		} finally {
			if(response != null && !TextUtils.isEmpty(traceId)) {
				response.setTraceId(traceId);
			}
		}

		return response;
	}

	/**
	 * 删除文件
	 */
	@Override
	public FilesDelResp deleteBatch(FilesDelReq req)
	{
		FilesDelResp response = null;
		HttpDelete method = null;
		HttpResponse resp = null;

		try
		{
			if (LiteStringUtils.isBlank(req.getFileIds()))
			{
				throw new DjangoClientException("field[fileIds] is null");
			}

			String timestampStr = String.valueOf(System.currentTimeMillis());
			String acl = genAclString(req.getFileIds(), timestampStr);

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
			params.add(new BasicNameValuePair("fileIds", req.getFileIds()));
			params.add(new BasicNameValuePair("timestamp", timestampStr));
			params.add(new BasicNameValuePair("acl", acl));
			String traceId = getTraceId();
			if (LiteStringUtils.isNotBlank(traceId)) {
				params.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, traceId));
			}

			// URI uri = new
			// URIBuilder(FileApiInfo.DELETE_BATCH.getApi()).setParameters(params).build();
            String api = FileApiInfo.DELETE_BATCH.getApi();
            if(CommonUtils.isWapNetWork()){
                api = FileApiInfo.DELETE_BATCH.getUrlApi();
				connectionManager.setProxy(FileApiInfo.DELETE_BATCH.getIp(), FileApiInfo.DELETE_BATCH.getHost());
			}
			method = new HttpDelete(HttpClientUtils.urlAppendParams(api, params));
            method.addHeader("Host", FileApiInfo.DELETE_BATCH.getHost());
            method.addHeader("Cookie", getCookieString());
			// debug 信息
			if (DjangoClient.DEBUG)
			{
				//Logger.D(DjangoClient.LOG_TAG, "cookie:" + getCookieString());
				Logger.D(DjangoClient.LOG_TAG, Arrays.toString(method.getAllHeaders()));
			}
			resp = connectionManager.getConnection().execute(method);

			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				HttpEntity entity = resp.getEntity();
				String content = EntityUtils.toString(entity, DjangoConstant.DEFAULT_CHARSET_NAME);
				if (DjangoClient.DEBUG)
				{
					Logger.D(DjangoClient.LOG_TAG, "filesDel() :" + content);
				}
				response = JSON.parseObject(content, FilesDelResp.class);
			} else
			{
				response = new FilesDelResp();
				response.setCode(resp.getStatusLine().getStatusCode());
				response.setMsg("http invoker error!");
			}
		} catch (Exception e)
		{
			Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
			response = new FilesDelResp();
			response.setCode(DjangoConstant.DJANGO_400);
			response.setMsg(e.getMessage());
		} finally
		{
			DjangoUtils.releaseConnection(method, resp);
		}

		return response;
	}

	/**
	 * 设置扩展属性
	 */
	@Override
	public SetExtResp setExt(SetExtReq req)
	{
		SetExtResp response = null;
		HttpPost method = null;
		HttpResponse resp = null;

		try
		{
			if (LiteStringUtils.isBlank(req.getFileId()))
			{
				throw new DjangoClientException("field[fileId] is null");
			} else if (req.getExt() == null || req.getExt().isEmpty())
			{
				throw new DjangoClientException("field[ext] is null");
			}

			String timestampStr = String.valueOf(System.currentTimeMillis());
			String acl = genAclString(req.getFileId(), timestampStr);
			// String extJson = gson.toJson(req.getExt());
			//
			// method = new HttpPost(FileApiInfo.SET_EXT.getApi());
			// MultipartEntityBuilder entityBuilder =
			// genMultipartEntityBuilder();
			// entityBuilder.addTextBody("token", tokenApi.getTokenString());
			// entityBuilder.addTextBody("fileId", req.getFileId());
			// entityBuilder.addTextBody("timestamp", timestampStr);
			// entityBuilder.addTextBody("acl", acl);
			// entityBuilder.addTextBody("ext", extJson);
			// method.setEntity(entityBuilder.build());

			String extJson = JSON.toJSONString(req.getExt());

			// URIBuilder builder = new
			// URIBuilder(FileApiInfo.SET_EXT.getApi());
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
			params.add(new BasicNameValuePair("timestamp", timestampStr));
			params.add(new BasicNameValuePair("acl", acl));
			params.add(new BasicNameValuePair("fileId", req.getFileId()));
			params.add(new BasicNameValuePair("ext", extJson));

			List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
			String traceId = getTraceId();
			if (LiteStringUtils.isNotBlank(traceId)) {
				urlParams.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, traceId));
			}

			String api = FileApiInfo.SET_EXT.getApi();
            if(CommonUtils.isWapNetWork()){
                api = FileApiInfo.SET_EXT.getUrlApi();
                connectionManager.setProxy(FileApiInfo.SET_EXT.getIp(),FileApiInfo.SET_EXT.getHost());
            }
		
			method = new HttpPost(HttpClientUtils.urlAppendParams(api, urlParams));
			method.setEntity(new UrlEncodedFormEntity(params));
            method.addHeader("Host", FileApiInfo.SET_EXT.getHost());
			method.addHeader("Cookie", getCookieString());
			// debug 信息
			if (DjangoClient.DEBUG)
			{
				//Logger.D(DjangoClient.LOG_TAG, "cookie:" + getCookieString());
				Logger.D(DjangoClient.LOG_TAG, Arrays.toString(method.getAllHeaders()));
			}
			resp = connectionManager.getConnection().execute(method);

			response = parseDjangoFileInfoResp(SetExtResp.class, resp);
		} catch (Exception e)
		{
			Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
			response = new SetExtResp();
			response.setCode(DjangoConstant.DJANGO_400);
			response.setMsg(e.getMessage());
		} finally
		{
			DjangoUtils.releaseConnection(method, resp);
		}

		return response;
	}

	@Override
	public FileOfflineUploadResp fileOfflineUpload(FileOfflineUploadReq req) {
		FileOfflineUploadResp response = null;
		HttpPost method = null;
		HttpResponse resp = null;

		try
		{
			if (req == null || LiteStringUtils.isBlank(req.downloadUrl))
			{
				throw new DjangoClientException("Invalid args!!! req: " + req);
			}

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
			params.add(new BasicNameValuePair("download_url", req.downloadUrl));
			params.add(new BasicNameValuePair("synchronous", String.valueOf(req.synchoronous)));

			if (req.size > 0) {
				params.add(new BasicNameValuePair("size", String.valueOf(req.size)));
			}
			if (!TextUtils.isEmpty(req.md5)) {
				params.add(new BasicNameValuePair("md5", req.md5));
			}
			if (!TextUtils.isEmpty(req.notifyUrl)) {
				params.add(new BasicNameValuePair("notify_url", req.notifyUrl));
			}
			if (!TextUtils.isEmpty(req.type)) {
				params.add(new BasicNameValuePair("type", req.type));
			}

			List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
			String traceId = getTraceId();
			if (LiteStringUtils.isNotBlank(traceId)) {
				urlParams.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, traceId));
			}

			String api = FileApiInfo.UPLOAD_OFFLINE.getApi();
            if(CommonUtils.isWapNetWork()){
                api = FileApiInfo.UPLOAD_OFFLINE.getUrlApi();
                connectionManager.setProxy(FileApiInfo.UPLOAD_OFFLINE.getIp(),FileApiInfo.UPLOAD_OFFLINE.getHost());
            }

			method = new HttpPost(HttpClientUtils.urlAppendParams(api, urlParams));
			method.setEntity(new UrlEncodedFormEntity(params));

			method.addHeader("Host", FileApiInfo.UPLOAD_OFFLINE.getHost());
			method.addHeader("Cookie", getCookieString());
			// debug 信息
			if (DjangoClient.DEBUG)
			{
				//Logger.D(DjangoClient.LOG_TAG, "cookie:" + getCookieString());
				Logger.D(DjangoClient.LOG_TAG, Arrays.toString(method.getAllHeaders()));
			}
			resp = connectionManager.getConnection().execute(method);
			response = parseDjangoFileInfoResp(FileOfflineUploadResp.class, resp);
		} catch (Exception e) {
			Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
			response = new FileOfflineUploadResp();
			response.setCode(DjangoConstant.DJANGO_400);
			response.setMsg(e.getMessage());
		} finally {
			DjangoUtils.releaseConnection(method, resp);
		}
		return response;
	}

	@Override
	public FileUpResp uploadRapidRange(FileRapidUpReq fileRapidUpReq) {
		FileUpResp response = null;
		HttpGet method = null;
		HttpResponse resp = null;
		String traceId = null;
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", tokenApi.getTokenString()));
			if (LiteStringUtils.isNotBlank(fileRapidUpReq.getMd5())) {
				params.add(new BasicNameValuePair("md5", fileRapidUpReq.getMd5()));
			}
			if (LiteStringUtils.isNotBlank(fileRapidUpReq.getGcid())) {
				params.add(new BasicNameValuePair("gcid", fileRapidUpReq.getGcid()));
			}
			if (LiteStringUtils.isNotBlank(fileRapidUpReq.getExt())) {
				params.add(new BasicNameValuePair("ext", fileRapidUpReq.getExt()));
			}
			traceId = getTraceId();
			if (LiteStringUtils.isNotBlank(traceId)) {
				params.add(new BasicNameValuePair(DjangoConstant.TRACE_ID, traceId));
			}

			FileApiInfo apiInfo = FileApiInfo.UPLOAD_CHECK_RAPID_RANGE;

			String api = apiInfo.getApi();
			if (CommonUtils.isWapNetWork()) {
				api = apiInfo.getUrlApi();
				connectionManager.setProxy(apiInfo.getIp(), apiInfo.getHost());
			}
			method = new HttpGet(HttpClientUtils.urlAppendParams(api, params));
			method.addHeader("Host", apiInfo.getHost());
			// debug 信息
			if (DjangoClient.DEBUG) {
				//Log.d(DjangoClient.LOG_TAG, "cookie:" + getCookieString());
				Logger.D(DjangoClient.LOG_TAG, Arrays.toString(method.getAllHeaders()));
			}
			resp = connectionManager.getConnection().execute(method);

			response = parseRangeResponse(resp);
			if (response.isSuccess()) {
				response.setRapid(true);
			}
		} catch (Exception e) {
			Logger.E(DjangoClient.LOG_TAG, e, e.getMessage());
			response = new FileUpResp();
			response.setCode(DjangoConstant.DJANGO_400);
			response.setMsg(e.getMessage());
		} finally {
			if (response != null && !TextUtils.isEmpty(traceId)) {
				response.setTraceId(traceId);
			}
			DjangoUtils.releaseConnection(method, resp);
		}

		return response;
	}
}

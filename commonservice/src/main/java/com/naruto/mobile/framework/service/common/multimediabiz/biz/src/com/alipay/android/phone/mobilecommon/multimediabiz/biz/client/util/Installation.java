/*
 * Copyright 2014 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.client.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

import android.content.Context;

public class Installation {
	private static String sID = null;
	private static final String INSTALLATION = "INSTALLATION";

	public synchronized static String id(Context context) {
		if (sID == null) {
			File installation = new File(context.getFilesDir(), INSTALLATION);
			try {
				if (!installation.exists())
					writeInstallationFile(installation);
				sID = readInstallationFile(installation);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return sID;
	}

	private static String readInstallationFile(File installation)
			throws IOException {
		RandomAccessFile f = new RandomAccessFile(installation, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes,"UTF-8");
	}

	private static void writeInstallationFile(File installation)
			throws IOException {
		FileOutputStream out = new FileOutputStream(installation);
		String id = UUID.randomUUID().toString();
		out.write(id.getBytes("UTF-8"));
		out.close();
	}
}

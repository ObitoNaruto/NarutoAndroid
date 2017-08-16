/*
 * File Name: 		StringManager.java
 * 
 * Copyright(c)   	Hotcard Technology Pte. Ltd.
 * 					All rights reserved.
 */

package com.naruto.mobile.framework.service.common.falcon.api.src.com.alipay.android.phone.falcon.common;

import java.io.UnsupportedEncodingException;

import android.util.Log;

public class StringManager {

	public StringManager() {

	}

	@Override
	public void finalize() {

	}


	/**
	 * convert GBK into UNICODE
	 * 
	 * @param array
	 *            GBK code in byte array
	 * 
	 * @return UNICODE string, null if error occur
	 */
	public static String convertGbkToUnicode(byte[] array) {
		String str = null;

		try {
			// filter the 0xd
			byte[] text = filterAndCut(array);
			if (text != null)
				str = new String(text, "GBK");
			else
				str = "";
		} catch (UnsupportedEncodingException e) {
			Log.e("convert", e.toString());
		}
		return str.trim();
	}



	/**
	 * in Windows, the string is end with "\r\n" for each row, but "\n" for
	 * Android, the filter parse the Windows' string into Android's. At the same
	 * time, the string array is end of '\0' for non-UNICODE, so ignore the
	 * elements after '\0'
	 * 
	 * @param array
	 *            Windows' string format
	 * 
	 * @return Android's string
	 * 
	 * @remark suit for NON-UNICODE
	 */
	public static byte[] filterAndCut(byte[] array) {
		int len = strlen(array);
		if (len < 1)
			return null;
		byte[] filter = new byte[len];
		for (int i = 0, cnt = 0; i < len; i++) {
			if (array[i] == (byte) 0xd)
				continue;
			filter[cnt++] = array[i];
		}
		return filter;
	}

	/**
	 * count the real length of a string array
	 * 
	 * @param array
	 *            string array.
	 * 
	 * @return real length of array not include the null terminator.
	 */
	public static int strlen(byte[] array) {
		int len = -1;
		if (array != null) {
			if (array.length == 0)
				len = 0;
			else {
				for (int i = 0; i < array.length; i++) {
					if (array[i] == (byte) 0) {
						len = i;
						break;
					}
				}
			}
		}
		return len;
	}

	/**
	 * convert UNICODE into ASCII.
	 * 
	 * @param str
	 *            UNICODE string
	 * 
	 * @return ASCII code in byte array. null if error occur.
	 */
	public static byte[] convertUnicodeToAscii(String str) {
		byte[] result = null;

		try {
			int cnt = str.length();
			byte[] res = str.getBytes("US-ASCII");
			result = new byte[cnt + 1];

			for (int i = 0; i < cnt; i++) {
				result[i] = res[i];
			}
			result[cnt] = 0; // we must add it manually.
		} catch (UnsupportedEncodingException e) {
			Log.e("convert", e.toString());
			result = null;
		}

		return result;
	}

}

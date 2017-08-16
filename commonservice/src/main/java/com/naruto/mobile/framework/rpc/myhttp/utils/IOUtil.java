package com.naruto.mobile.framework.rpc.myhttp.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;


/**
 * IO工具
 * 
 * @author sanping.li@alipay.com
 *
 */
public class IOUtil {
    /**
     * 把流读成字符串
     * 
     * @param is 输入流
     * @return 字符串
     */
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            Log.e("IOUtil", "",e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e("IOUtil", "",e);
            }
        }
        return sb.toString();
    }
    
    /**
     * 关闭流
     * 
     * @param stream 可关闭的流
     */
	public static void closeStream(Closeable stream) {
		try {
			if (stream != null)
				stream.close();
		} catch (IOException e) {
            Log.e("IOUtil", "",e);
		}
	}
	
	
	public static byte[] InputStreamToByte(InputStream is) throws IOException{
		
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();  
		int ch;  
		while ((ch = is.read()) != -1) {  
			bytestream.write(ch);  
		}
		byte byteData[] = bytestream.toByteArray();  
		bytestream.close();  
		return byteData;  
	}	
}

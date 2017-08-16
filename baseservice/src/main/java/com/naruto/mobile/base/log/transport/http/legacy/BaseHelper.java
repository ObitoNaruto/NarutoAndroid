package com.naruto.mobile.base.log.transport.http.legacy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.naruto.mobile.base.log.logging.LogCatLog;
//import com.alipay.mobile.common.logging.LogCatLog;

public class BaseHelper {
    public static String getContentType(String acontentType) {
        String contentType = null;

        try {
            String contentTypeTag = ";";
            int end = acontentType.indexOf(contentTypeTag);
            if (end > 0)
                contentType = acontentType.substring(0, end);
            else
                contentType = acontentType;
        } catch (Exception e) {
            LogCatLog.printStackTraceAndMore(e);
        }

        return contentType;
    }

    public static String getCharset(String contentType) {
        String charset = null;

        try {
            String charsetTag = "charset=";
            int start = contentType.indexOf(charsetTag);
            if (start != -1) {
                start += charsetTag.length();

                int end = contentType.indexOf(";", start);
                if (end != -1)
                    charset = contentType.substring(start, end);
                else
                    charset = contentType.substring(start);
            }
        } catch (Exception e) {
            LogCatLog.printStackTraceAndMore(e);
        }
        return charset;
    }
    
    public static String convertStreamToString(InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
            }
        }
        catch (IOException e)
        {
            LogCatLog.printStackTraceAndMore(e);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                LogCatLog.printStackTraceAndMore(e);
            }
        }
        return sb.toString();
    }
}

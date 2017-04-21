package com.naruto.mobile.base.Router.andRouter.utils;

import android.net.Uri;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

/**
 */
public class UrlUtils {

    private static final String TAG = "UrlUtils";

    /**
     * get the path segments
     * @param url
     * @return
     */
    public static List<String> getPathSegments(String url) {
        Log.d("UrlUtils", "PathSegments:" + Uri.parse(url).getPathSegments());
        return Uri.parse(url).getPathSegments();
    }

    /**
     * get the scheme of the url
     * @param url
     * @return
     */
    public static String getScheme(String url){
        Log.d("UrlUtils", "scheme:" + Uri.parse(url).getScheme());
        return Uri.parse(url).getScheme();
    }


    /**
     * get the protocol of the url
     */
    public static int getPort(String url){
        Log.d("UrlUtils", "port:" + Uri.parse(url).getPort());
        return Uri.parse(url).getPort();
    }

    public static String getHost(String url){
        Log.d("UrlUtils", "host:" + Uri.parse(url).getHost());
        return Uri.parse(url).getHost();
    }

    public static HashMap<String, String> getParameters(String url){
        HashMap<String, String> parameters = new HashMap<>();
        try{
            Uri uri = Uri.parse(url);
            Set<String> keys = uri.getQueryParameterNames();

            for(String key : keys){
                parameters.put(key, uri.getQueryParameter(key));
            }
        } catch (Exception e){
            Timber.e(e, "");
        }
        Log.d("UrlUtils", "parameters:" + parameters);
        return parameters;
    }


    public static String addQueryParameters(String url, String key, String value){
        try{
            Uri uri = Uri.parse(url);
            return uri.buildUpon().appendQueryParameter(key, value).build().toString();
        } catch (Exception e){
            Timber.e(e, "");
        }
        return url;
    }



    public static String addQueryParameters(String url, Map<String, String> parameters){
        try{
            Uri uri = Uri.parse(url);
            Uri.Builder builder = uri.buildUpon();
            for(String key : parameters.keySet()) {
                builder.appendQueryParameter(key, parameters.get(key));
            }
            return builder.toString();
        } catch (Exception e){
            Timber.e(e, "");
        }
        return url;
    }
}

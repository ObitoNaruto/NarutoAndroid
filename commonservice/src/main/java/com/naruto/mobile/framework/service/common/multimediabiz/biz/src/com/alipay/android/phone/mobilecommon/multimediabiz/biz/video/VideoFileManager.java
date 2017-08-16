package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.video;

import java.io.File;
import java.sql.SQLException;
import java.util.List;


import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.AppUtils;


public class VideoFileManager
{
	private static VideoFileManager sInstance;
	private VideoCachePersistence mDb;
	public static String mBaseDir = Environment.getExternalStorageDirectory() + File.separator + "ShortVideo";
	public static String mBaseDir1 = Environment.getExternalStorageDirectory() + File.separator + "ShortVideo";
	public static final int JPG = 1;
	public static final int VIDEO = 0;
	
	public static final int TYPE_RECORD = 0;
	public static final int TYPE_OTHERS = 1;
	private static final String TAG = "VideoFileManager";
    private VideoFileManager() {
    	try {
    		mDb = new VideoCachePersistence(AppUtils.getApplicationContext());
		} catch (SQLException e) {
			// TODO: handle exception
		}
    }

    public synchronized static VideoFileManager getInstance() {
        if (sInstance == null) {
            sInstance = new VideoFileManager();
        }
        return sInstance;
    }
    public void insertRecord(String path, String clouid, String localid, int type)
    {
    	VideoCacheModel model = new VideoCacheModel(path, clouid, localid, type);
    	try {
    		mDb.save(model);
		} catch (SQLException e) {
		}
    }
    public void setCloudId(String cloudid, String localid, int type)
    {
    	List<VideoCacheModel> list = mDb.queryAllVideoCacheModelsByLocalId(localid);
    	String tag = ((type == JPG) ? ".jpg" : ".mp4");
    	int i = 0;
    	if (list != null) {
    		for (i = 0; i < list.size(); i++)
    		{
    			if (list.get(i).path.endsWith(tag))
    			{
    				list.get(i).cloudId = cloudid;
    				break;
    			}
    		}
			try {
				mDb.save(list.get(i));
			} catch (SQLException e) {
			}
		}
    }
    public String getVideoPath1(String id)
    {
    	if (TextUtils.isEmpty(id))
    	{
    		return id;
    	}
    	Log.d(TAG, "getVideoPath1 in:" + id);
    	List<VideoCacheModel> list = null;
    	String result = "";
    	if (id.contains("|"))
    	{
    		list = mDb.queryAllVideoCacheModelsByCloudId(id.substring(0, id.indexOf('|')));
		}
    	else
    	{
    		list = mDb.queryAllVideoCacheModelsByLocalId(id);
		}
    	if (list != null && !list.isEmpty())
    	{
    		for (int i = 0; i < list.size(); i++)
    		{
    			if (list.get(i).path.endsWith("mp4"))
    			{
    				result = list.get(i).path;
    				break;
    			}
    		}
    	}
    	Log.d(TAG, "getVideoPath1 out:" + result);
    	return result;
    }
    
    public String getThumbPath1(String id)
    {	
    	if (TextUtils.isEmpty(id))
    	{
    		return "";
    	}
    	Log.d(TAG, "getThumbPath1 in:" + id);
    	List<VideoCacheModel> list = null;
    	String result = "";
    	if (id.contains("|"))
    	{
    		list = mDb.queryAllVideoCacheModelsByCloudId(id.substring(id.indexOf('|') + 1, id.length()));
		}
    	else
    	{
    		list = mDb.queryAllVideoCacheModelsByLocalId(id);
		}
    	if (list != null)
    	{
    		for (int i = 0; i < list.size(); i++)
    		{
    			if (list.get(i).path.endsWith("jpg"))
    			{
    				result = list.get(i).path;
    				break;
    			}
    		}
    	}
    	Log.d(TAG, "getThumbPath1 out:" + result);
    	return result;
    }
    public String getLocalIdByPath(String path)
    {
    	Log.d(TAG, "getLocalIdByPath in:" + path);
    	String result = "";
    	if (TextUtils.isEmpty(path))
    	{
    		return result;
    	}
    	VideoCacheModel model = mDb.queryVideoCacheByPath(path);
    	if (model != null)
    		result = model.localId;
    	Log.d(TAG, "getLocalIdByPath out:" + result);
    	return result;
    }
    
    public String getVideoPath(String id)
    {	
    	String name = "";
    	if (!id.contains("|"))
    	{
    		id = id + "|" + id;
    	}
    	name = id.substring(0, id.indexOf('|'));
    	if (id.length() > 50)
    		return mBaseDir1 + File.separator + name + ".mp4";
    	else
    		return mBaseDir + File.separator + name + ".mp4";
    }
    
    public String getThumbPath(String id)
    {
    	String name = "";
    	if (!id.contains("|"))
    	{
    		id = id + "|" + id;
    	}
    	name = id.substring(id.indexOf('|') + 1, id.length());
    	if (id.length() > 50)
    		return mBaseDir1 + File.separator + name + ".jpg";
    	else
    		return mBaseDir + File.separator + name + ".jpg";
    }

	public List<VideoCacheModel> queryRecentVideo(long interval) {
		try {
			return mDb.queryVideoCacheModelsByTimeInterval(interval, true);
		} catch (SQLException e) {
		}
		return null;
	}
	public void deleteByLocalId(String localid)
	{
    	List<VideoCacheModel> list = mDb.queryAllVideoCacheModelsByLocalId(localid);
    	if (list != null) {
    		for (int i = 0; i < list.size(); i++)
    		{
    			list.get(i).deleteByUser = 1;
    			try {
    				mDb.save(list.get(i));
    			} catch (SQLException e) {
    			}
    		}
		}
	}
	public void removeRecordById(String id)
	{
		List<VideoCacheModel> list = null;
		if (id.contains("|"))
		{
			list = mDb.queryAllVideoCacheModelsByCloudId(id);
		}
		else
		{
			list = mDb.queryAllVideoCacheModelsByLocalId(id);
		}
    	if (list != null) {
    		for (int i = 0; i < list.size(); i++)
    		{
    			try {
    				mDb.delete(list.get(i));
    			} catch (SQLException e) {
    			}
    		}
		}
	}
}
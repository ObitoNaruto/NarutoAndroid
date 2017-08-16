package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class FFmpegCameraEncoder implements PreviewCallback {
	private static final String TAG = "FFmpegCameraEncoder";
	private byte[] mPreviewBuffer;
	private byte[] mYuvData;
	private byte[] mYuvTmp;
	private int mBufSize;
	private int mFormats = ImageFormat.NV21;
	private volatile boolean mIsRecording;
	private Camera mCamera;
	private int mCameraFacing = CameraInfo.CAMERA_FACING_BACK;
	private FFmpegMuxer mMuxer;
	private FFmpegSessionConfig mSessionConfig;
	private Size mPreviewSize;
	private boolean mFirstFrameRequest = true;
	private boolean mInited = false;
    private int mOrientation;
    private long mFirstTs = 0;
    private long mLastTs = 0;
    private long mRecordStartTimestamp;;
	public FFmpegCameraEncoder(Camera camera, FFmpegSessionConfig sessionConfig) {
		mCamera = camera;
		mPreviewSize = mCamera.getParameters().getPreviewSize();
		mBufSize = mPreviewSize.width * mPreviewSize.height
				* ImageFormat.getBitsPerPixel(mFormats);
		mPreviewBuffer = new byte[mBufSize];
		mYuvData = new byte[mBufSize];
		mYuvTmp = new byte[mBufSize];
		camera.addCallbackBuffer(mPreviewBuffer);
		camera.setPreviewCallbackWithBuffer(this);
		mSessionConfig = sessionConfig;
		mMuxer = mSessionConfig.getMuxer();
		mOrientation = getOrientation(CameraInfo.CAMERA_FACING_BACK);
	}
	private int getOrientation(int facing)
	{
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(facing, info);
        return info.orientation;
	}
	public void switchCamera(Camera camera, int facing)
	{
		mCamera = camera;
		mCameraFacing = facing;
		mPreviewSize = mCamera.getParameters().getPreviewSize();
		mBufSize = mPreviewSize.width * mPreviewSize.height
				* ImageFormat.getBitsPerPixel(mFormats);
		mPreviewBuffer = new byte[mBufSize];
		mYuvData = new byte[mBufSize];
		mYuvTmp = new byte[mBufSize];
		camera.addCallbackBuffer(mPreviewBuffer);
		camera.setPreviewCallbackWithBuffer(this);
		mOrientation = getOrientation(facing);
	}
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// Log.d(TAG, "onPreviewFrame");
		if (mIsRecording) {
			// Log.d(TAG, "handlePreviewFrame");
			Parameters param = camera.getParameters();
			Size s = param.getPreviewSize();
			handlePreviewFrame(data, s.width, s.height, mBufSize);
		}
		camera.addCallbackBuffer(mPreviewBuffer);
	}

	private void handlePreviewFrame(final byte[] data, final int width, final int height, int size) {
		if (mFormats == ImageFormat.NV21) {
//			mYuvTmp = VideoHelper.cropYUV420(mYuvData, height, width,
//					mSessionConfig.vHeight);
			if (mFirstFrameRequest) {
				mFirstFrameRequest = false;
				mRecordStartTimestamp = System.nanoTime() / 1000;
				new Thread(new Runnable() {
					@Override
					public void run() {
						saveFrame(data, width, height);
					}
				}).start();
			}
//			VideoHelper.NV21ToYUV420Planar(data, mYuvTmp,
//					mSessionConfig.vWidth, mSessionConfig.vHeight);
		}
		synchronized (mMuxer) {
			long timestamp = System.nanoTime() / 1000 - mRecordStartTimestamp;;
			mMuxer.putVideo(data, size, timestamp, mOrientation);
		}
	}
	public void start() {
		mSessionConfig.vWidth = mPreviewSize.height;
		mSessionConfig.vHeight = mPreviewSize.width;
		mMuxer.init(mSessionConfig);
		mInited = true;
		mIsRecording = true;
		mFirstFrameRequest = true;
	}
	public boolean isRecording()
	{
		return mIsRecording;
	}
	public void stop() {
		if (mIsRecording) {
			mMuxer.uninit();
			mIsRecording = false;
		}
	}
	private void saveFrame(byte[] data, int width, int height)
	{
		long ts = System.currentTimeMillis();
		if (mOrientation == 90) {
			VideoHelper.rotateYUV420SPAntiClockwiseDegree90(data, mYuvData,
					width, height);
		} else {
			VideoHelper.rotateYUV420SPClockwiseDegree90(data, mYuvData,
					width, height);
		}
		YuvImage image = new YuvImage(mYuvData, ImageFormat.NV21,
				height, mSessionConfig.vHeight, null);
		FileOutputStream fos = null;
		String path = mSessionConfig.vPublishUrl;
		String jpgpath = path.substring(0, path.indexOf('.')) + ".jpg";
		try {
			fos = new FileOutputStream(jpgpath);
			image.compressToJpeg(new Rect(0, 0, height,
					mSessionConfig.vHeight), 100, fos);
		} catch (FileNotFoundException e) {
			// TODO: handle exception
		}
		finally
		{
			if (fos != null)
			{
				try {
					fos.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(jpgpath);
			bitmap = Bitmap.createScaledBitmap(bitmap, 360, 640, false);
			fos = new FileOutputStream(jpgpath);
			bitmap.compress(CompressFormat.JPEG, 70, fos);
		} catch (FileNotFoundException e) {
			// TODO: handle exception
		}
		finally
		{
			if (fos != null)
			{
				try {
					fos.close();
				} catch (Exception e2) {
				}
			}
		}
		Log.d(TAG, "saveFrame took " + (System.currentTimeMillis() - ts) + "ms");
	}
}

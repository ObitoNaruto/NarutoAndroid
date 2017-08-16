package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.os.Build;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @hide
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class AndroidMuxer
{
    private static final String TAG = "AndroidMuxer";
    private static final boolean VERBOSE = true;

    private MediaMuxer mMuxer;
    private boolean mStarted;
    private final int mExpectedNumTracks;

    protected String mOutputPath;
    protected int mNumTracks;
    protected int mNumTracksFinished;
    protected long mFirstPts[] = {0, 0};
    protected long mLastPts[] = {0, 0};

	private AndroidMuxer(String outputFile)
    {
		mOutputPath = outputFile;
    	try
    	{
    		mMuxer = new MediaMuxer(outputFile, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        }
    	catch (IOException e)
        {
        }
        mStarted = false;
        mNumTracks = 0;
        mNumTracksFinished = 0;
        mExpectedNumTracks = 2;
    }

    public static AndroidMuxer create(String outputFile) {
        return new AndroidMuxer(outputFile);
    }

    public int addTrack(MediaFormat trackFormat) {
        if(mStarted)
            throw new RuntimeException("format changed twice");
        int track = mMuxer.addTrack(trackFormat);
        mNumTracks++;
        if(allTracksAdded())
        {
           start();
        }
        return track;
    }

    protected void start() {
        mMuxer.start();
        mStarted = true;
    }
    public void clean()
    {
    	if (!allTracksAdded())
    	{
    		new File(mOutputPath).delete();
    	}
    }
    protected void stop() {
    	Log.d(TAG, "muxer stop");
        mMuxer.stop();
        mStarted = false;
        mMuxer.release();
        Log.d(TAG, "muxer stop end");
//        
//        try {
//        	saveThumbnail();
//		} catch (IOException e)
//		{
//		}
    }
//    private void saveThumbnail() throws IOException
//    {
//    	MediaMetadataRetriever media = new MediaMetadataRetriever();
//        File imageFile = new File(mOutputPath.substring(0, mOutputPath.indexOf('.')) + ".jpg");
//    	media.setDataSource(mOutputPath);
//    	Bitmap bitmap = media.getFrameAtTime();
//    	BufferedOutputStream bos = null;
//        try {
//            bos = new BufferedOutputStream(new FileOutputStream(imageFile));
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos);
//            bitmap.recycle();
//        }
//		finally {
//            if (bos != null) 
//            	bos.close();
//        }
//    }

//    public void release() {
//        mMuxer.release();
//    }

    public boolean isStarted() {
        return mStarted;
    }
    
    protected void signalEndOfTrack(){
    	Log.d(TAG, "signalEndOfTrack");
        mNumTracksFinished++;
    }
    public void writeSampleData(MediaCodec encoder, int trackIndex, int bufferIndex, ByteBuffer encodedData, MediaCodec.BufferInfo bufferInfo) {
        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            signalEndOfTrack();
        }
        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            // MediaMuxer gets the codec config info via the addTrack command
            if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
            encoder.releaseOutputBuffer(bufferIndex, false);
            return;
        }

        if(bufferInfo.size == 0){
            if(VERBOSE) Log.d(TAG, "ignoring zero size buffer");
            encoder.releaseOutputBuffer(bufferIndex, false);
            if(allTracksFinished()){
                stop();
            }
            return;
        }

        if (!mStarted) {
            Log.e(TAG, "writeSampleData called before muxer started. Ignoring packet. Track index: " + trackIndex + "num of tracks added: " + mNumTracks);
            encoder.releaseOutputBuffer(bufferIndex, false);
            return;
        }

        bufferInfo.presentationTimeUs = getNextRelativePts(bufferInfo.presentationTimeUs, trackIndex);

        mMuxer.writeSampleData(trackIndex, encodedData, bufferInfo);
        Log.d("test", "track index: " + trackIndex + ", ts:" + bufferInfo.presentationTimeUs);
        encoder.releaseOutputBuffer(bufferIndex, false);

        if(allTracksFinished()){
            stop();
        }
    }

    public void forceStop() {
        stop();
    }
    protected boolean allTracksFinished(){
        return (mNumTracks == mNumTracksFinished);
    }

    protected boolean allTracksAdded(){
        return (mNumTracks == mExpectedNumTracks);
    }

    /**
     * Return a relative pts given an absolute pts and trackIndex.
     *
     * This method advances the state of the Muxer, and must only
     * be called once per call to {@link #writeSampleData(MediaCodec, int, int, ByteBuffer, MediaCodec.BufferInfo)}.
    */
    protected long getNextRelativePts(long absPts, int trackIndex) {
        if (mFirstPts[trackIndex] == 0) {
            mFirstPts[trackIndex] = absPts;
            return 0;
        }
        return getSafePts(absPts - mFirstPts[trackIndex], trackIndex);
    }
    
    /**
     * Sometimes packets with non-increasing pts are dequeued from the MediaCodec output buffer.
     * This method ensures that a crash won't occur due to non monotonically increasing packet timestamp.
     */
    private long getSafePts(long pts, int trackIndex) {
        if (mLastPts[trackIndex] >= pts) {
            // Enforce a non-zero minimum spacing
            // between pts
            mLastPts[trackIndex] += 9643;
            return mLastPts[trackIndex];
        }
        mLastPts[trackIndex] = pts;
        return pts;
    }
}

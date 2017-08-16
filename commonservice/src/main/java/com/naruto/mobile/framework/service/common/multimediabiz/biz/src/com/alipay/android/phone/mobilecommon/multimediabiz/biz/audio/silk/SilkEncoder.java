package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio.silk;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.naruto.mobile.framework.service.common.falcon.api.src.com.alipay.android.phone.falcon.audio.JniFalconVoice;
//qiang.heq

/**
 * Silk编码器
 * Created by jinmin on 15/5/20.
 */
public class SilkEncoder {
    private AtomicBoolean mRunning = new AtomicBoolean(false);
    private SilkApi mApi;
    private List<DataPacket> mDatas = Collections.synchronizedList(new ArrayList<DataPacket>());
    private Thread mHandleThread;
    private int mCompression;
    private int mSampleRate;
    private int mTargetRate;
    private EncodeOutputHandler mEncodeOutputHandler;
    //qiang.heq
    private boolean falconSwitchStatus;

    public SilkEncoder(int compression, int sampleRate, int targetRate) {
        this.mApi = new SilkApi();
        this.mCompression = compression;
        this.mSampleRate = sampleRate;
        this.mTargetRate = targetRate;
        //qiang.heq
        this.falconSwitchStatus = false;
//        MicroApplicationContext context = AppUtils.getMicroApplicationContext();
//        FalconCfgDataService falconCfgDataService = context.getExtServiceByInterface(FalconCfgDataService.class.getName());
//        if (null != falconCfgDataService) {
//            String mBlurString = falconCfgDataService.getCfgDatafrom("falcon.audio.eliminatenoise.switch");
//            if (null != mBlurString && mBlurString.equals("true")) {
//                this.falconSwitchStatus = true;
//            }
//        }
        //qiang.heq
    }

    public void setEncodeHandler(EncodeOutputHandler handler) {
        this.mEncodeOutputHandler = handler;
    }

    public void setCompression(int compression) {
        this.mCompression = compression;
    }

    public void setSampleRate(int sampleRate) {
        this.mSampleRate = sampleRate;
    }

    public void setTargetRate(int targetRate) {
        this.mTargetRate = targetRate;
    }

    public void add(byte[] data, int size) {
        mDatas.add(new DataPacket(data, size));
    }

    public void add(short[] data, int size) {
        mDatas.add(new DataPacket(data, size));
    }

    public void stop() {
        mRunning.set(false);
    }

    public void reset() {
        mRunning.set(false);
        if (mHandleThread != null) {
            mHandleThread.interrupt();
            mHandleThread = null;
        }
        mDatas.clear();
    }

    //qiang.heq
    static {
    	JniFalconVoice.initJni();
    }
    
    public void start() {
        mRunning.set(true);
        if (mHandleThread == null) {
            mHandleThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    mApi.openEncoder(mCompression, mSampleRate, mTargetRate);
                    byte[] buf = new byte[4096];
                    boolean first = true;
                    if (falconSwitchStatus) {
                        //Logger.I("qiang.heq", "falconSwitchStatus is true");
                        //qiang.heq
                        int packetSize = 882;
                        JniFalconVoice.Init(44100, packetSize, 60);
                        while (mRunning.get()) {
                            if (mDatas.isEmpty() || first) {
                                try {
                                    //20ms 一帧
                                    Thread.sleep(20);
                                    first = false;
                                } catch (InterruptedException e) {
                                }
                                continue;
                            }
                            while (mDatas.size() > 0) {
                                if (mEncodeOutputHandler != null) {
                                    DataPacket packet = mDatas.remove(0);

                                    short[] buf_out = JniFalconVoice.RemoveNoise(packet.getShorts(),packet.getShortSize(),false);
                                    if(buf_out != null)
                                    {
                                        int length = buf_out.length;
                                        int offset = 0;
                                        while(length >= packetSize)
                                        {
                                            int encodeLength = mApi.encode(buf_out,offset, buf, packetSize);
                                            mEncodeOutputHandler.handle(buf, encodeLength);
                                            offset += packetSize;
                                            length -= packetSize;
                                        }
                                    }
                                }
                            }
                        }
                        short[] buf_out = JniFalconVoice.RemoveNoise(null,0,true);
                        if(buf_out != null)
                        {
                            int length = buf_out.length;
                            int offset = 0;
                            while(length >= packetSize)
                            {
                                int encodeLength = mApi.encode(buf_out,offset, buf, packetSize);
                                mEncodeOutputHandler.handle(buf, encodeLength);
                                offset += packetSize;
                                length -= packetSize;
                            }
                        }
                        JniFalconVoice.Release();
                        //qiang.heq
                    } else {
                        //Logger.E("qiang.heq", "falconSwitchStatus is false");
                        while (mRunning.get()) {
                            if (mDatas.isEmpty() || first) {
                                try {
                                    //20ms 一帧
                                    Thread.sleep(20);
                                    first = false;
                                } catch (InterruptedException e) {
                                }
                                continue;
                            }
                            while (mDatas.size() > 0) {
                                if (mEncodeOutputHandler != null) {
                                    DataPacket packet = mDatas.remove(0);
                                    int encodeLength = mApi.encode(packet.getShorts(), 0, buf, packet.getShortSize());
                                    mEncodeOutputHandler.handle(buf, encodeLength);
                                }
                            }
                        }
                    }
                    mApi.closeEncoder();
                    if (mEncodeOutputHandler != null) {
                        mEncodeOutputHandler.handleFinished();
                    }
                }
            });
            mHandleThread.setName("SilkEncoder");
            mHandleThread.start();
        }
    }

    public class DataPacket {
        public int size;
        public byte[] data;
        public short[] shorts;
        public int shortSize;

        public DataPacket(byte[] data, int size) {
            this.data = new byte[size];
            System.arraycopy(data, 0, this.data, 0, size);
            this.size = size;
        }

        public DataPacket(short[] data, int size) {
            this.shorts = new short[size];
            System.arraycopy(data, 0, this.shorts, 0, size);
            this.shortSize = size;
        }

        public short[] getShorts() {
            if (shorts == null && data != null) {
                shorts = SilkUtils.getShortArray(data, size);
                shortSize = shorts.length;
            }
            return shorts;
        }

        public int getShortSize() {
            return shortSize;
        }
    }

    public interface EncodeOutputHandler {
        void handle(byte[] encodeData, int length);
        void handleFinished();
    }

}

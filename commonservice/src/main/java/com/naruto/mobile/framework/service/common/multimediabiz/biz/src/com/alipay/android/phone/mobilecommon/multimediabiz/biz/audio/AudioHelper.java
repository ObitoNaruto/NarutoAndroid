package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.audio;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.concurrent.CopyOnWriteArraySet;

import com.naruto.mobile.framework.service.common.multimediabiz.biz.src.com.alipay.android.phone.mobilecommon.multimediabiz.biz.utils.Logger;

/**
 * 音频管理
 * Created by jinmin on 15/3/30.
 */
class AudioHelper {
    private static final String TAG = AudioHelper.class.getSimpleName();

    private static AudioHelper sInstance = new AudioHelper();

    private Logger logger = Logger.getLogger("AudioHelper");

    private SensorManager mSensorManager;
    private boolean mRegisterSensor = false;

    public interface OnSensorChangeListener {
        void onSensorChanged(boolean closeToFace);
    }

    private CopyOnWriteArraySet<OnSensorChangeListener> sensorChangeListeners = new CopyOnWriteArraySet<OnSensorChangeListener>();

    public void registerSensorChangeListener(OnSensorChangeListener l) {
        sensorChangeListeners.add(l);
    }

    public void unregisterSensorChangeListener(OnSensorChangeListener l) {
        sensorChangeListeners.remove(l);
    }

    /**
     * 距离阈值
     */
    private static final float PROXIMITY_THRESHOLD = 5.0f;

//    private AudioRegulatorManager.AudioRegulator mAudioRegulator;

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float distance = event.values[0];

            boolean isDistanceInRange = (distance >= 0.0 &&
                    distance < PROXIMITY_THRESHOLD &&
                    distance < event.sensor.getMaximumRange());

            logger.d("onSensorChanged distance = " + distance +
                    ", isDistanceInRange = " + isDistanceInRange);
            if (isDistanceInRange) {
                //将语音输出模式调整为接近脸部的模式
//                mAudioRegulator.closeToTheFace();
                for (OnSensorChangeListener l : sensorChangeListeners) {
                    l.onSensorChanged(true);
                }
            } else {
//                mAudioRegulator.stayAwayFromFace(true);
                for (OnSensorChangeListener l : sensorChangeListeners) {
                    l.onSensorChanged(false);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private AudioHelper() {

    }

    public synchronized static AudioHelper getInstance() {
        return sInstance;
    }

    public void registerSensorMonitor(Context context) {
        if (!mRegisterSensor) {
//            mAudioRegulator = getAudioRegulator(context);
            SensorManager sensorManager = getSensorManager(context);
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensorManager.registerListener(mSensorEventListener, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            mRegisterSensor = true;
        }
    }

    public void unregisterSensorMonitor(Context context) {
        if (mRegisterSensor) {
            getSensorManager(context).unregisterListener(mSensorEventListener);
            mRegisterSensor = false;
            sensorChangeListeners.clear();
        }
    }


    private SensorManager getSensorManager(Context context) {
        if (mSensorManager == null) {
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        return mSensorManager;
    }

//    public void initAudioRegulator(Context context) {
//        getAudioRegulator(context);
//    }
//
//    private AudioRegulatorManager.AudioRegulator getAudioRegulator(Context context) {
//        if (mAudioRegulator == null) {
//            mAudioRegulator = AudioRegulatorManager.newAudioRegulator(context);
//        }
//        return mAudioRegulator;
//    }

//    public void turnEarPhone(boolean notify) {
////        mAudioRegulator.turnEarPhone();
//        mAudioRegulator.closeToTheFace(notify);
//    }
//
//    public void turnSpeakerphoneOn(boolean notify) {
////        mAudioRegulator.turnSpeakerphoneOn();
//        mAudioRegulator.stayAwayFromFace(true, notify);
//    }
//
//    public void pauseOtherPlayers() {
//
//    }
//
//    public void resumeOtherPlayers() {
//
//    }
//
//    //====== 来电处理  =========
//    public static interface OnTelephonyListener {
//        public void onCallIn();
//        public void onCallOut();
//    }
//    private Set<OnTelephonyListener> mTelephonyListeners = new HashSet<OnTelephonyListener>();
//    private void registerTelephonyListener(OnTelephonyListener listener) {
//        mTelephonyListeners.add(listener);
//    }
//    public void unregisterTelePhonyListener(OnTelephonyListener listener) {
//        mTelephonyListeners.remove(listener);
//    }
//

}

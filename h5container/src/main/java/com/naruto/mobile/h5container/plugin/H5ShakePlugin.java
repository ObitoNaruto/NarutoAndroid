
package com.naruto.mobile.h5container.plugin;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;

import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.env.H5Environment;
import com.naruto.mobile.h5container.util.H5Log;

public class H5ShakePlugin implements H5Plugin {

    public static final String TAG = "H5ShakePlugin";

    private H5Intent curIntent;

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(VIBRATE);
        filter.addAction(WATCH_SHAKE);
    }

    @Override
    public void onRelease() {
        curIntent = null;
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (VIBRATE.equals(action)) {
            vibrate();
        } else if (WATCH_SHAKE.equals(action)) {
            if (curIntent == null) {
                this.curIntent = intent;
                registerListener();
            }
        }

        return true;
    }

    private void registerListener() {
        Context context = H5Environment.getContext();
        SensorManager sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        int rate = SensorManager.SENSOR_DELAY_NORMAL;
        Sensor sensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(listener, sensor, rate);
    }

    private void unregisterListener() {
        Context context = H5Environment.getContext();
        SensorManager sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(listener);
    }

    private SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent intent) {
            float[] values = intent.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];
            H5Log.d(TAG, "onSensorChanged x " + x + " y " + y + " z " + z);
            int medumValue = 19;
            boolean vibrated = Math.abs(x) > medumValue
                    || Math.abs(y) > medumValue || Math.abs(z) > medumValue;
            if (!vibrated) {
                return;
            }
            unregisterListener();

            if (curIntent != null) {
                curIntent.sendBack(null);
            }
            curIntent = null;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void vibrate() {
        Context context = H5Environment.getContext();
        Vibrator vibrator = (Vibrator) context
                .getSystemService(Activity.VIBRATOR_SERVICE);
        vibrator.vibrate(400);
    }
}

package com.naruto.mobile.base.threadpool;

import android.util.Log;
import android.os.Process;

//import static android.os.Process.PROC_COMBINE;
//import static android.os.Process.PROC_OUT_LONG;
//import static android.os.Process.PROC_SPACE_TERM;


public class ProcessCpuTracker {

    private final static String TAG = ProcessCpuTracker.class.getSimpleName();
    private final static String TOTAL_STAT_FILE = "/proc/stat";

    private final static int[] SYSTEM_CPU_FORMAT = new int[] {
//            PROC_SPACE_TERM | PROC_COMBINE, // 0: N/A
//            PROC_SPACE_TERM | PROC_OUT_LONG, // 1: user time
//            PROC_SPACE_TERM | PROC_OUT_LONG, // 2: nice time
//            PROC_SPACE_TERM | PROC_OUT_LONG, // 3: sys time
//            PROC_SPACE_TERM | PROC_OUT_LONG, // 4: idle time
//            PROC_SPACE_TERM | PROC_OUT_LONG, // 5: iowait time
//            PROC_SPACE_TERM | PROC_OUT_LONG, // 6: irq time
//            PROC_SPACE_TERM | PROC_OUT_LONG // 7: softirq time
    };

    private long[] mTotalCpuData = new long[7];

    private long mBaseUserTime;
    private long mBaseSystemTime;
    // private long mBaseIoWaitTime;
    private long mBaseIrqTime;
    // private long mBaseSoftIrqTime;
    private long mBaseIdleTime;

    private long mRelUserTime;
    private long mRelSystemTime;
    // private long mRelIoWaitTime;
    private long mRelIrqTime;
    // private long mRelSoftIrqTime;
    private long mRelIdleTime;

    public ProcessCpuTracker update() {
        boolean isSuccess = false;

        try {
//            isSuccess = Process.readProcFile(TOTAL_STAT_FILE,
//                    SYSTEM_CPU_FORMAT, null, mTotalCpuData, null);

        } catch (Throwable t) {
            Log.w(TAG, t);
        }

        if (!isSuccess) {
            Log.e(TAG, "fail to compute");
            return this;
        }

        // Total user time is user + nice time.
        final long userTime = mTotalCpuData[0] + mTotalCpuData[1];
        // Total system time is simply system time.
        final long systemTime = mTotalCpuData[2];
        // Total idle time is simply idle time.
        final long idleTime = mTotalCpuData[3];
        // Total irq time is iowait + irq + softirq time.
        // final long ioWaitTime = mTotalCpuData[4];
        final long irqTime = mTotalCpuData[5];
        // final long softIrqTime = mTotalCpuData[6];

        mRelUserTime = userTime - mBaseUserTime;
        mRelSystemTime = systemTime - mBaseSystemTime;
        // mRelIoWaitTime = ioWaitTime - mBaseIoWaitTime;
        mRelIrqTime = irqTime - mBaseIrqTime;
        // mRelSoftIrqTime = softIrqTime - mBaseSoftIrqTime;
        mRelIdleTime = idleTime - mBaseIdleTime;

        mBaseUserTime = userTime;
        mBaseSystemTime = systemTime;
        // mBaseIoWaitTime = ioWaitTime;
        mBaseIrqTime = irqTime;
        // mBaseSoftIrqTime = softIrqTime;
        mBaseIdleTime = idleTime;

        return this;
    }

    public float getCpuIdlePercent() {
        long denom = mRelUserTime + mRelSystemTime + mRelIrqTime + mRelIdleTime;
        return denom > 0 ? mRelIdleTime * 100f / denom : -1;
    }

}


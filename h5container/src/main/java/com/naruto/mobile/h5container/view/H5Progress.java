
package com.naruto.mobile.h5container.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

import com.naruto.mobile.h5container.util.H5Log;

/**
 * SmoothProgress show an elegant progress to user
 */

public class H5Progress extends ProgressBar {
    public static final String TAG = "SmoothProgress";

    public static final int DEFAULT_DURATION = 1200;
    public static final int MIN_DURATION = 300;
    public static final int SET_PROGRESS = 0;

    private ProgressNotifier notifier;

    private long minDuration;
    private long startTime;
    private Timer timer;
    private int targetProgress;
    private int lastProgress;
    private int lastTarget;
    private long originTime;
    private long curDuration;
    private int curPeriod;
    private int nextVisibility;

    public interface ProgressNotifier {
        public void onProgressBegin();

        public void onProgressEnd();
    }

    public H5Progress(Context context) {
        super(context);
        init();
    }

    public H5Progress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public H5Progress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setNotifier(ProgressNotifier notifier) {
        this.notifier = notifier;
    }

    private void init() {
        minDuration = DEFAULT_DURATION;
        this.setMax(100);
        this.nextVisibility = -1;
        reset();
    }

    private void reset() {
        originTime = 0;
        targetProgress = 0;
        lastTarget = 0;
        lastProgress = 0;
    }

    public void setMinDuration(long duration) {
        minDuration = duration;
    }

    public void updateProgress(int progress) {
        long current = System.currentTimeMillis();
        if (originTime == 0) {
            originTime = current;
        }

        int max = getMax();
        progress = (int) (progress * 0.25 + max * 0.75);

        H5Log.d(TAG, "updateProgress " + progress);

        if (progress < lastProgress || progress > max) {
            // reset();
            return;
        } else {
            lastTarget = lastProgress;
        }

        startTime = current;
        targetProgress = progress;
        linearProgress();
    }

    private void linearProgress() {
        if (isIndeterminate()) {
            return;
        }

        curDuration = minDuration;
        if (targetProgress == getMax() && (lastTarget > (getMax() / 2))) {
            curDuration = MIN_DURATION;
        }

        final int deltaProgress = targetProgress - lastTarget;
        if (deltaProgress <= 0 || curDuration <= 0) {
            return;
        }

        curPeriod = (int) (curDuration / deltaProgress);

        if (timer != null) {
            cancelTimer();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                int max = getMax();
                if (max == 0) {
                    cancelTimer();
                    return;
                }
                long currentTime = System.currentTimeMillis();
                long deltaTime = currentTime - startTime;

                long duration = (curDuration * deltaProgress) / max;
                if (duration == 0) {
                    cancelTimer();
                    return;
                }
                int nextProgress = lastTarget
                        + (int) ((deltaTime * deltaProgress) / curDuration);

                // H5Log.d(TAG, "deltaTime " + deltaTime + " deltaProgress "
                // + deltaProgress + " duration " + duration
                // + " nextProgress " + nextProgress);

                post(new ProgressRunner(nextProgress));

                if (nextProgress > targetProgress) {
                    if (nextProgress > getMax()) {
                        reset();
                    }
                    cancelTimer();
                    return;
                }
            }
        }, curPeriod, curPeriod);
    }

    private void cancelTimer() {
        if (timer == null) {
            return;
        }

        timer.cancel();
        timer = null;
    }

    public void setVisibility(int visibility) {
        if (timer == null || visibility == View.VISIBLE) {
            super.setVisibility(visibility);
        } else {
            nextVisibility = visibility;
        }
    }

    class ProgressRunner implements Runnable {
        private int progress;

        public ProgressRunner(int progress) {
            this.progress = progress;
        }

        @Override
        public void run() {
            if (progress > targetProgress) {
                if (progress > getMax() && notifier != null) {
                    notifier.onProgressEnd();
                }

                if (nextVisibility != -1) {
                    H5Progress.super.setVisibility(nextVisibility);
                    nextVisibility = -1;
                }
                return;
            }

            if (lastProgress == 0 && notifier != null) {
                notifier.onProgressBegin();
            }

            // H5Log.d(TAG, "setProgress " + progress);
            setProgress(progress);
            lastProgress = progress;
        }
    }
}

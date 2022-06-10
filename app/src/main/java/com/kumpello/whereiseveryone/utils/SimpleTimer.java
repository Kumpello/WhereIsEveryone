package com.kumpello.whereiseveryone.utils;

import android.os.Handler;

public class SimpleTimer {

    private Runnable runnable = null;
    private final Runnable run = new Runnable() {
        @Override
        public void run() {
            handler.post(runnable);

            handler.postDelayed(run, intervalMS);
        }
    };


    private final Handler handler = new Handler();
    private int intervalMS;

    public void startDelayed(Runnable mRunnable, int delayMS) {
        runnable = mRunnable;
        handler.postAtTime(runnable, System.currentTimeMillis() + delayMS);
    }

    public void start(Runnable mRunnable, final int intervalMS) {
        runnable = mRunnable;
        this.intervalMS = intervalMS;
        handler.postDelayed(run, intervalMS);
    }


    public void changeInterval(final int intervalMS) {
        this.intervalMS = intervalMS;
    }

    public void stop() {
        handler.removeCallbacks(run);
        handler.removeCallbacks(runnable);
    }
}
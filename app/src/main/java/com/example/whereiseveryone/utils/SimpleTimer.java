package com.example.whereiseveryone.utils;

import android.os.Handler;

public class SimpleTimer {

    private Runnable runnable = null;
    private final Handler handler = new Handler();

    public void startDelayed(Runnable mRunnable, int delayMS) {
        runnable = mRunnable;
        handler.postAtTime(runnable, System.currentTimeMillis() + delayMS);
    }

    public void start(Runnable mRunnable, final int intervalMS) {
        runnable = mRunnable;
        handler.postDelayed(runnable, intervalMS);
    }

    public void stop() {
        handler.removeCallbacks(runnable);
    }
}
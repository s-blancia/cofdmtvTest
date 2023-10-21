package com.aicodix.rattlegram;

import android.os.Handler;

import com.aicodix.rattlegram.Output.WaveFileOutputContext;

import com.aicodix.rattlegram.Output.WaveFileOutputContext;

class MainActivityMessenger {
    private final MainActivity mMainActivity;
    private final Handler mHandler;

    MainActivityMessenger(MainActivity activity) {
        mMainActivity = activity;
        mHandler = new Handler();
    }

    <WaveFileOutputContext> void carrySaveAsWaveIsDoneMessage(final WaveFileOutputContext context) {
        mHandler.post(() -> mMainActivity.completeSaving((com.aicodix.rattlegram.Output.WaveFileOutputContext) context));
    }
}
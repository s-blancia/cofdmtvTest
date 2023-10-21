package com.aicodix.rattlegram.Modes;

import android.graphics.Bitmap;

import com.aicodix.rattlegram.ModeInterfaces.ModeSize;
import com.aicodix.rattlegram.Modes.ImageFormats.Yuv;
import com.aicodix.rattlegram.Modes.ImageFormats.YuvFactory;
import com.aicodix.rattlegram.Modes.ImageFormats.YuvImageFormat;
import com.aicodix.rattlegram.Output.IOutput;
import com.aicodix.rattlegram.R;

@ModeSize(width = 320, height = 240)
@ModeDescription(name = R.string.action_robot36)
class Robot36 extends Mode {
    private final Yuv mYuv;
    private final int mLumaScanSamples;
    private final int mChrominanceScanSamples;
    private final int mSyncPulseSamples;
    private final double mSyncPulseFrequency;
    private final int mSyncPorchSamples;
    private final double mSyncPorchFrequency;
    private final int mPorchSamples;
    private final double mPorchFrequency;
    private final int mSeparatorSamples;
    private final double mEvenSeparatorFrequency;
    private final double mOddSeparatorFrequency;

    Robot36(Bitmap bitmap, IOutput output) {
        super(bitmap, output);
        mYuv = YuvFactory.createYuv(mBitmap, YuvImageFormat.NV21);
        mVISCode = 8;
        mLumaScanSamples = convertMsToSamples(88.0);
        mChrominanceScanSamples = convertMsToSamples(44.0);
        mSyncPulseSamples = convertMsToSamples(9.0);
        mSyncPulseFrequency = 1200.0;
        mSyncPorchSamples = convertMsToSamples(3.0);
        mSyncPorchFrequency = 1500.0;
        mPorchSamples = convertMsToSamples(1.5);
        mPorchFrequency = 1900.0;
        mSeparatorSamples = convertMsToSamples(4.5);
        mEvenSeparatorFrequency = 1500.0;
        mOddSeparatorFrequency = 2300.0;
    }

    protected int getTransmissionSamples() {
        int lineSamples = mSyncPulseSamples + mSyncPorchSamples
                + mLumaScanSamples + mSeparatorSamples
                + mPorchSamples + mChrominanceScanSamples;
        return mBitmap.getHeight() * lineSamples;
    }

    protected void writeEncodedLine() {
        addSyncPulse();
        addSyncPorch();
        addYScan(mLine);
        if (mLine % 2 == 0) {
            addSeparator(mEvenSeparatorFrequency);
            addPorch();
            addVScan(mLine);
        } else {
            addSeparator(mOddSeparatorFrequency);
            addPorch();
            addUScan(mLine);
        }
    }

    private void addSyncPulse() {
        for (int i = 0; i < mSyncPulseSamples; ++i)
            setTone(mSyncPulseFrequency);
    }

    private void addSyncPorch() {
        for (int i = 0; i < mSyncPorchSamples; ++i)
            setTone(mSyncPorchFrequency);
    }

    private void addSeparator(double separatorFrequency) {
        for (int i = 0; i < mSeparatorSamples; ++i)
            setTone(separatorFrequency);
    }

    private void addPorch() {
        for (int i = 0; i < mPorchSamples; ++i)
            setTone(mPorchFrequency);
    }

    private void addYScan(int y) {
        for (int i = 0; i < mLumaScanSamples; ++i)
            setColorTone(mYuv.getY((i * mYuv.getWidth()) / mLumaScanSamples, y));
    }

    private void addUScan(int y) {
        for (int i = 0; i < mChrominanceScanSamples; ++i)
            setColorTone(mYuv.getU((i * mYuv.getWidth()) / mChrominanceScanSamples, y));
    }

    private void addVScan(int y) {
        for (int i = 0; i < mChrominanceScanSamples; ++i)
            setColorTone(mYuv.getV((i * mYuv.getWidth()) / mChrominanceScanSamples, y));
    }
}
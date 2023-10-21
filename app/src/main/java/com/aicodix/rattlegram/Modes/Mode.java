package com.aicodix.rattlegram.Modes;

import android.graphics.Bitmap;

import com.aicodix.rattlegram.ModeInterfaces.IMode;
import com.aicodix.rattlegram.Output.IOutput;

abstract class Mode implements IMode {
    protected Bitmap mBitmap;
    protected int mVISCode;
    protected int mLine;
    private final IOutput mOutput;
    private final double mSampleRate;
    private double mRunningIntegral;

    protected Mode(Bitmap bitmap, IOutput output) {
        mOutput = output;
        mSampleRate = mOutput.getSampleRate();
        mBitmap = bitmap;
    }

    @Override
    public void init() {
        mRunningIntegral = 0.0;
        mLine = 0;
        mOutput.init(getTotalSamples());
        writeCalibrationHeader();
    }

    @Override
    public boolean process() {
        if (mLine >= mBitmap.getHeight())
            return false;
        writeEncodedLine();
        ++mLine;
        return true;
    }

    @Override
    public void finish(boolean cancel) {
        mOutput.finish(cancel);
        destroyBitmap();
    }

    private int getTotalSamples() {
        return getHeaderSamples() + getTransmissionSamples();
    }

    private int getHeaderSamples() {
        return 2 * convertMsToSamples(300.0)
                + convertMsToSamples(10.0)
                + 10 * convertMsToSamples(30.0);
    }

    protected abstract int getTransmissionSamples();

    private void writeCalibrationHeader() {
        int leaderToneSamples = convertMsToSamples(300.0);
        double leaderToneFrequency = 1900.0;
        int breakSamples = convertMsToSamples(10.0);
        double breakFrequency = 1200.0;
        int visBitSamples = convertMsToSamples(30.0);
        double visBitSSFrequency = 1200.0;
        double[] visBitFrequency = new double[]{1300.0, 1100.0};
        for (int i = 0; i < leaderToneSamples; ++i)
            setTone(leaderToneFrequency);
        for (int i = 0; i < breakSamples; ++i)
            setTone(breakFrequency);
        for (int i = 0; i < leaderToneSamples; ++i)
            setTone(leaderToneFrequency);
        for (int i = 0; i < visBitSamples; ++i)
            setTone(visBitSSFrequency);
        int parity = 0;
        for (int pos = 0; pos < 7; ++pos) {
            int bit = (mVISCode >> pos) & 1;
            parity ^= bit;
            for (int i = 0; i < visBitSamples; ++i)
                setTone(visBitFrequency[bit]);
        }
        for (int i = 0; i < visBitSamples; ++i)
            setTone(visBitFrequency[parity]);
        for (int i = 0; i < visBitSamples; ++i)
            setTone(visBitSSFrequency);
    }

    protected abstract void writeEncodedLine();

    protected int convertMsToSamples(double durationMs) {
        return (int) Math.round(durationMs * mSampleRate / 1000.0);
    }

    protected void setTone(double frequency) {
        mRunningIntegral += 2.0 * frequency * Math.PI / mSampleRate;
        mRunningIntegral %= 2.0 * Math.PI;
        mOutput.write(Math.sin(mRunningIntegral));
    }

    protected void setColorTone(int color) {
        double blackFrequency = 1500.0;
        double whiteFrequency = 2300.0;
        setTone(color * (whiteFrequency - blackFrequency) / 255.0 + blackFrequency);
    }

    private void destroyBitmap() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
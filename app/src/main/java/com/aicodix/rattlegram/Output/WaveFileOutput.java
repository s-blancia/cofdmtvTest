package com.aicodix.rattlegram.Output;

import java.io.BufferedOutputStream;

class WaveFileOutput implements IOutput {
    private final double mSampleRate;
    private final WaveFileOutputContext mContext;
    private BufferedOutputStream mOutputStream;
    private int mSamples, mWrittenSamples;

    WaveFileOutput(WaveFileOutputContext context, double sampleRate) {
        mContext = context;
        mSampleRate = sampleRate;
    }

    public void init(int samples) {
        int offset = (int) ((0.01 * mSampleRate) / 2.0);
        mSamples = samples + 2 * offset;
        mWrittenSamples = 0;
        InitOutputStream();
        writeHeader();
        padWithZeros(offset);
    }

    private void writeHeader() {
        try {
            int numChannels = 1;
            int bitsPerSample = Short.SIZE;
            int blockAlign = numChannels * bitsPerSample / Byte.SIZE;
            int subchunk2Size = mSamples * blockAlign;
            mOutputStream.write("RIFF".getBytes());
            mOutputStream.write(toLittleEndian(36 + subchunk2Size));
            mOutputStream.write("WAVE".getBytes());
            mOutputStream.write("fmt ".getBytes());
            mOutputStream.write(toLittleEndian(16));
            mOutputStream.write(toLittleEndian((short) 1));
            mOutputStream.write(toLittleEndian((short) numChannels));
            mOutputStream.write(toLittleEndian((int) mSampleRate));
            mOutputStream.write(toLittleEndian((int) mSampleRate * blockAlign));
            mOutputStream.write(toLittleEndian((short) blockAlign));
            mOutputStream.write(toLittleEndian((short) bitsPerSample));
            mOutputStream.write("data".getBytes());
            mOutputStream.write(toLittleEndian(subchunk2Size));
        } catch (Exception ignore) {
        }
    }

    private void InitOutputStream() {
        try {
            mOutputStream = new BufferedOutputStream(mContext.getOutputStream());
        } catch (Exception ignore) {
        }
    }

    @Override
    public double getSampleRate() {
        return mSampleRate;
    }

    @Override
    public void write(double value) {
        short tmp = (short) (value * Short.MAX_VALUE);
        ++mWrittenSamples;
        try {
            mOutputStream.write(toLittleEndian(tmp));
        } catch (Exception ignore) {
        }
    }

    @Override
    public void finish(boolean cancel) {
        if (!cancel)
            padWithZeros(mSamples);
        try {
            mOutputStream.close();
            mOutputStream = null;
        } catch (Exception ignore) {
        }
        if (cancel)
            mContext.deleteFile();
    }

    private void padWithZeros(int count) {
        try {
            while (mWrittenSamples++ < count)
                mOutputStream.write(toLittleEndian((short) 0));
        } catch (Exception ignore) {
        }
    }

    private byte[] toLittleEndian(int value) {
        byte[] buffer = new byte[4];
        buffer[0] = (byte) (value & 255);
        buffer[1] = (byte) ((value >> 8) & 255);
        buffer[2] = (byte) ((value >> 16) & 255);
        buffer[3] = (byte) ((value >> 24) & 255);
        return buffer;
    }

    private byte[] toLittleEndian(short value) {
        byte[] buffer = new byte[2];
        buffer[0] = (byte) (value & 255);
        buffer[1] = (byte) ((value >> 8) & 255);
        return buffer;
    }
}
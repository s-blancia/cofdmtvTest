package com.aicodix.rattlegram;

import android.graphics.Bitmap;

import com.aicodix.rattlegram.ModeInterfaces.IModeInfo;

import java.util.LinkedList;
import java.util.List;

import com.aicodix.rattlegram.ModeInterfaces.IMode;
import com.aicodix.rattlegram.ModeInterfaces.IModeInfo;
import com.aicodix.rattlegram.Modes.ModeFactory;
import com.aicodix.rattlegram.Output.IOutput;
import com.aicodix.rattlegram.Output.OutputFactory;
import com.aicodix.rattlegram.Output.WaveFileOutputContext;

class Encoder {
    private final MainActivityMessenger mMessenger;
    private final Thread mThread;
    private final List<IMode> mQueue;
    private Thread mSaveWaveThread;
    private boolean mQuit, mStop;
    private Class<?> mModeClass;
    private OnEncodeCompleteListener encodeCompleteListener;

    Encoder(MainActivityMessenger messenger) {
        mMessenger = messenger;
        mQueue = new LinkedList<>();
        mQuit = false;
        mStop = false;
        mModeClass = ModeFactory.getDefaultMode();
        mThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    IMode mode;
                    synchronized (this) {
                        while (mQueue.isEmpty() && !mQuit) {
                            try {
                                wait();
                            } catch (Exception ignore) {
                            }
                        }
                        if (mQuit)
                            return;
                        mStop = false;
                        mode = mQueue.remove(0);
                    }
                    mode.init();
                    while (mode.process()) {
                        synchronized (this) {
                            if (mQuit || mStop)
                                break;
                        }
                    }
                    mode.finish(mStop);
                }
            }
        };
        mThread.start();
    }

    boolean setMode(String className) {
        try {
            mModeClass = Class.forName(className);
        } catch (Exception ignore) {
            return false;
        }
        return true;
    }

    IModeInfo getModeInfo() {
        return ModeFactory.getModeInfo(mModeClass);
    }

    public void setOnEncodeCompleteListener(OnEncodeCompleteListener listener) {
        this.encodeCompleteListener = listener;
    }

    void play(Bitmap bitmap) {
        IOutput output = OutputFactory.createOutputForSending();
        IMode mode = ModeFactory.CreateMode(mModeClass, bitmap, output);
        if (encodeCompleteListener != null) {
            encodeCompleteListener.onEncodeComplete();
        }
        if (mode != null)
            enqueue(mode);
    }

    void save(Bitmap bitmap, WaveFileOutputContext context) {
        if (mSaveWaveThread != null && mSaveWaveThread.isAlive())
            return;
        IOutput output = OutputFactory.createOutputForSavingAsWave(context);
        IMode mode = ModeFactory.CreateMode(mModeClass, bitmap, output);
        if (mode != null)
            save(mode, context);
    }

    private void save(final IMode mode, final WaveFileOutputContext context) {
        mSaveWaveThread = new Thread() {
            @Override
            public void run() {
                mode.init();
                while (mode.process()) {
                    synchronized (this) {
                        if (mQuit)
                            break;
                    }
                }
                mode.finish(mQuit);
                if (!mQuit)
                    mMessenger.carrySaveAsWaveIsDoneMessage(context);
            }
        };
        mSaveWaveThread.start();
    }

    void stop() {
        synchronized (mThread) {
            mStop = true;
            int size = mQueue.size();
            for (int i = 0; i < size; ++i)
                mQueue.remove(0).finish(true);
        }
    }

    private void enqueue(IMode mode) {
        synchronized (mThread) {
            mQueue.add(mode);
            mThread.notify();
        }
    }

    void destroy() {
        synchronized (mThread) {
            mQuit = true;
            mThread.notify();
        }
    }

    public interface OnEncodeCompleteListener {
        void onEncodeComplete();
    }
}
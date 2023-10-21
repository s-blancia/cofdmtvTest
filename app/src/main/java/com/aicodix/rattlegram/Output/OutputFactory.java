package com.aicodix.rattlegram.Output;

public final class OutputFactory {
    public static IOutput createOutputForSending() {
        double sampleRate = 44100.0;
        return new AudioOutput(sampleRate);
    }

    public static IOutput createOutputForSavingAsWave(WaveFileOutputContext context) {
        double sampleRate = 44100.0;
        return new WaveFileOutput(context, sampleRate);
    }
}
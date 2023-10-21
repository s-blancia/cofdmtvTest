package com.aicodix.rattlegram.Output;

public interface IOutput {
    double getSampleRate();

    void init(int samples);

    void write(double value);

    void finish(boolean cancel);
}
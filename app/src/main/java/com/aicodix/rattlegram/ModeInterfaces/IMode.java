package com.aicodix.rattlegram.ModeInterfaces;

public interface IMode {
    void init();

    boolean process();

    void finish(boolean cancel);
}
package com.aicodix.rattlegram.Modes;

import com.aicodix.rattlegram.ModeInterfaces.IModeInfo;
import com.aicodix.rattlegram.ModeInterfaces.ModeSize;

class ModeInfo implements IModeInfo {
    private final Class<?> mModeClass;

    ModeInfo(Class<?> modeClass) {
        mModeClass = modeClass;
    }

    public String getModeClassName() {
        return mModeClass.getName();
    }

    public ModeSize getModeSize() {
        return mModeClass.getAnnotation(ModeSize.class);
    }
}
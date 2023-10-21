package com.aicodix.rattlegram.Modes;

import android.graphics.Bitmap;

import java.lang.reflect.Constructor;

import  com.aicodix.rattlegram.ModeInterfaces.IMode;
import  com.aicodix.rattlegram.ModeInterfaces.IModeInfo;
import  com.aicodix.rattlegram.ModeInterfaces.ModeSize;
import  com.aicodix.rattlegram.Output.IOutput;

public final class ModeFactory {
    public static Class<?> getDefaultMode() {
        return Robot36.class;
    }

    public static String getDefaultModeClassName() {
        return (new ModeInfo(getDefaultMode())).getModeClassName();
    }

    public static IModeInfo getModeInfo(Class<?> modeClass) {
        if (!isModeClassValid(modeClass))
            return null;
        return (IModeInfo) new ModeInfo(modeClass);
    }

    public static IMode CreateMode(Class<?> modeClass, Bitmap bitmap, IOutput output) {
        Mode mode = null;
        if (bitmap != null && output != null && isModeClassValid(modeClass)) {
            ModeSize size = modeClass.getAnnotation(ModeSize.class);
            assert size != null;
            if (bitmap.getWidth() == size.width() && bitmap.getHeight() == size.height()) {
                try {
                    Constructor<?> constructor = modeClass.getDeclaredConstructor(Bitmap.class, IOutput.class);
                    mode = (Mode) constructor.newInstance(bitmap, output);
                } catch (Exception ignore) {
                }
            }
        }
        return mode;
    }

    private static boolean isModeClassValid(Class<?> modeClass) {
        return Mode.class.isAssignableFrom(modeClass) &&
                modeClass.isAnnotationPresent(ModeSize.class) &&
                modeClass.isAnnotationPresent(ModeDescription.class);
    }
}
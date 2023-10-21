package com.aicodix.rattlegram.Modes.ImageFormats;

import android.graphics.Bitmap;

public final class YuvFactory {
    public static Yuv createYuv(Bitmap bitmap, int format) {
        switch (format) {
            case YuvImageFormat.YV12:
                return new YV12(bitmap);
            case YuvImageFormat.NV21:
                return new NV21(bitmap);
            case YuvImageFormat.YUY2:
                return new YUY2(bitmap);
            case YuvImageFormat.YUV440P:
                return new YUV440P(bitmap);
            default:
                throw new IllegalArgumentException("Only support YV12, NV21, YUY2 and YUV440P");
        }
    }
}
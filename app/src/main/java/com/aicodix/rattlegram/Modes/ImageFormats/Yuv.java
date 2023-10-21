package com.aicodix.rattlegram.Modes.ImageFormats;

import android.graphics.Bitmap;

public abstract class Yuv {
    protected byte[] mYuv;
    final int mWidth;
    final int mHeight;

    Yuv(Bitmap bitmap) {
        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();
        convertBitmapToYuv(bitmap);
    }

    protected abstract void convertBitmapToYuv(Bitmap bitmap);

    public int getWidth() {
        return mWidth;
    }

    public abstract int getY(int x, int y);

    public abstract int getU(int x, int y);

    public abstract int getV(int x, int y);
}
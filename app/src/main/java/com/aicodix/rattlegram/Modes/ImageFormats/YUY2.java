package com.aicodix.rattlegram.Modes.ImageFormats;

import android.graphics.Bitmap;

class YUY2 extends Yuv {
    YUY2(Bitmap bitmap) {
        super(bitmap);
    }

    protected void convertBitmapToYuv(Bitmap bitmap) {
        mYuv = new byte[2 * mWidth * mHeight];
        for (int pos = 0, h = 0; h < mHeight; ++h) {
            for (int w = 0; w < mWidth; w += 2) {
                mYuv[pos++] = (byte) YuvConverter.convertToY(bitmap.getPixel(w, h));
                int u0 = YuvConverter.convertToU(bitmap.getPixel(w, h));
                int u1 = YuvConverter.convertToU(bitmap.getPixel(w + 1, h));
                mYuv[pos++] = (byte) ((u0 + u1) / 2);
                mYuv[pos++] = (byte) YuvConverter.convertToY(bitmap.getPixel(w + 1, h));
                int v0 = YuvConverter.convertToV(bitmap.getPixel(w, h));
                int v1 = YuvConverter.convertToV(bitmap.getPixel(w + 1, h));
                mYuv[pos++] = (byte) ((v0 + v1) / 2);
            }
        }
    }

    public int getY(int x, int y) {
        return 255 & mYuv[2 * mWidth * y + 2 * x];
    }

    public int getU(int x, int y) {
        return 255 & mYuv[2 * mWidth * y + (((x & ~1) << 1) | 1)];
    }

    public int getV(int x, int y) {
        return 255 & mYuv[2 * mWidth * y + ((x << 1) | 3)];
    }
}
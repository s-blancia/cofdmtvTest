package com.aicodix.rattlegram.Modes.ImageFormats;

import android.graphics.Bitmap;

class YUV440P extends Yuv {
    YUV440P(Bitmap bitmap) {
        super(bitmap);
    }

    protected void convertBitmapToYuv(Bitmap bitmap) {
        mYuv = new byte[2 * mWidth * mHeight];
        int pos = 0;
        for (int h = 0; h < mHeight; ++h)
            for (int w = 0; w < mWidth; ++w)
                mYuv[pos++] = (byte) YuvConverter.convertToY(bitmap.getPixel(w, h));
        for (int h = 0; h < mHeight; h += 2) {
            for (int w = 0; w < mWidth; ++w) {
                int u0 = YuvConverter.convertToU(bitmap.getPixel(w, h));
                int u1 = YuvConverter.convertToU(bitmap.getPixel(w, h + 1));
                mYuv[pos++] = (byte) ((u0 + u1) / 2);
            }
        }
        for (int h = 0; h < mHeight; h += 2) {
            for (int w = 0; w < mWidth; ++w) {
                int v0 = YuvConverter.convertToV(bitmap.getPixel(w, h));
                int v1 = YuvConverter.convertToV(bitmap.getPixel(w, h + 1));
                mYuv[pos++] = (byte) ((v0 + v1) / 2);
            }
        }
    }

    public int getY(int x, int y) {
        return 255 & mYuv[mWidth * y + x];
    }

    public int getU(int x, int y) {
        return 255 & mYuv[mWidth * mHeight + mWidth * (y >> 1) + x];
    }

    public int getV(int x, int y) {
        return 255 & mYuv[((3 * mWidth * mHeight) >> 1) + mWidth * (y >> 1) + x];
    }
}
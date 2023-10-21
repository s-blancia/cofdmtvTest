package com.aicodix.rattlegram.Modes.ImageFormats;

import android.graphics.Bitmap;

class YV12 extends Yuv {
    YV12(Bitmap bitmap) {
        super(bitmap);
    }

    protected void convertBitmapToYuv(Bitmap bitmap) {
        mYuv = new byte[(3 * mWidth * mHeight) / 2];
        int pos = 0;
        for (int h = 0; h < mHeight; ++h)
            for (int w = 0; w < mWidth; ++w)
                mYuv[pos++] = (byte) YuvConverter.convertToY(bitmap.getPixel(w, h));
        for (int h = 0; h < mHeight; h += 2) {
            for (int w = 0; w < mWidth; w += 2) {
                int u0 = YuvConverter.convertToU(bitmap.getPixel(w, h));
                int u1 = YuvConverter.convertToU(bitmap.getPixel(w + 1, h));
                int u2 = YuvConverter.convertToU(bitmap.getPixel(w, h + 1));
                int u3 = YuvConverter.convertToU(bitmap.getPixel(w + 1, h + 1));
                mYuv[pos++] = (byte) ((u0 + u1 + u2 + u3) / 4);
            }
        }
        for (int h = 0; h < mHeight; h += 2) {
            for (int w = 0; w < mWidth; w += 2) {
                int v0 = YuvConverter.convertToV(bitmap.getPixel(w, h));
                int v1 = YuvConverter.convertToV(bitmap.getPixel(w + 1, h));
                int v2 = YuvConverter.convertToV(bitmap.getPixel(w, h + 1));
                int v3 = YuvConverter.convertToV(bitmap.getPixel(w + 1, h + 1));
                mYuv[pos++] = (byte) ((v0 + v1 + v2 + v3) / 4);
            }
        }
    }

    public int getY(int x, int y) {
        return 255 & mYuv[mWidth * y + x];
    }

    public int getU(int x, int y) {
        return 255 & mYuv[mWidth * mHeight + (mWidth >> 1) * (y >> 1) + (x >> 1)];
    }

    public int getV(int x, int y) {
        return 255 & mYuv[((5 * mWidth * mHeight) >> 2) + (mWidth >> 1) * (y >> 1) + (x >> 1)];
    }
}
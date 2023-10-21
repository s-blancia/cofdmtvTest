package com.aicodix.rattlegram;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class Utility {
    @NonNull
    static Rect getEmbeddedRect(int w, int h, int iw, int ih) {
        Rect rect;
        int ow = (9 * w) / 10;
        int oh = (9 * h) / 10;
        if (iw * oh < ow * ih) {
            int right = (iw * oh) / ih;
            rect = new Rect(0, 0, right, oh);
            rect.offset((w - right) / 2, (h - oh) / 2);
        } else {
            int bottom = (ih * ow) / iw;
            rect = new Rect(0, 0, ow, bottom);
            rect.offset((w - ow) / 2, (h - bottom) / 2);
        }
        return rect;
    }

    static float getTextSizeFactor(int w, int h) {
        return 0.1f * (Utility.getEmbeddedRect(w, h, 320, 240).height());
    }

    static int convertToDegrees(int exifOrientation) {
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
        }
        return 0;
    }

    static Uri createImageUri(Context context) {
        if (!isExternalStorageWritable())
            return null;
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(dir, createFileName() + ".jpg");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            return Uri.fromFile(file);
        return FileProvider.getUriForFile(context, "om.sstvencoder", file);
    }

    static String createWaveFileName() {
        return createFileName() + ".wav";
    }

    private static String createFileName() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
    }

    static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
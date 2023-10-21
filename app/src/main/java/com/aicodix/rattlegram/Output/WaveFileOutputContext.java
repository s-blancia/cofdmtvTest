package com.aicodix.rattlegram.Output;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.OutputStream;

public class WaveFileOutputContext {
    private final ContentResolver mContentResolver;
    private File mFile;
    private Uri mUri;
    private final ContentValues mValues;

    public WaveFileOutputContext(ContentResolver contentResolver, String fileName) {
        mContentResolver = contentResolver;
        mValues = getContentValues(fileName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            mUri = mContentResolver.insert(MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), mValues);
        else
            mFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), fileName);
    }

    private ContentValues getContentValues(String fileName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/wav");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Audio.Media.RELATIVE_PATH, (new File(Environment.DIRECTORY_MUSIC, "SSTV Encoder")).getPath());
            values.put(MediaStore.Audio.Media.IS_PENDING, 1);
        } else {
            values.put(MediaStore.Audio.Media.ALBUM, "SSTV Encoder");
            values.put(MediaStore.Audio.Media.TITLE, fileName);
            values.put(MediaStore.Audio.Media.IS_MUSIC, true);
        }
        return values;
    }

    public OutputStream getOutputStream() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return mContentResolver.openOutputStream(mUri);
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    public void update() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (mUri != null && mValues != null) {
                mValues.clear();
                mValues.put(MediaStore.Audio.Media.IS_PENDING, 0);
                mContentResolver.update(mUri, mValues, null, null);
            }
        } else {
            if (mFile != null && mValues != null) {
                mValues.put(MediaStore.Audio.Media.DATA, mFile.toString());
                mUri = mContentResolver.insert(MediaStore.Audio.Media.getContentUriForPath(mFile.getAbsolutePath()), mValues);
            }
        }
    }

    public void deleteFile() {
        try {
            if (mFile == null)
                mFile = new File(mUri.getPath());
        } catch (Exception ignore) {
        }
    }
}
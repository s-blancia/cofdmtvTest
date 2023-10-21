package com.aicodix.rattlegram;

import android.content.Context;
import android.net.Uri;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.aicodix.rattlegram.Modes.ModeFactory;

class Settings {
    private final int mVersion;
    private final String mFileName;
    private Context mContext;
    private String mModeClassName;
    private String mImageUri;

    private Settings() {
        mVersion = 1;
        mFileName = "settings.json";
        mModeClassName = ModeFactory.getDefaultModeClassName();
    }

    Settings(Context context) {
        this();
        mContext = context;
    }

    void load() {
        JsonReader reader = null;
        try {
            InputStream in = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                in = Files.newInputStream(getFile().toPath());
            }
            reader = new JsonReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            read(reader);
        } catch (Exception ignore) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    void setModeClassName(String modeClassName) {
        mModeClassName = modeClassName;
    }

    String getModeClassName() {
        return mModeClassName;
    }

    void setImageUri(Uri uri) {
        mImageUri = uri == null ? null : uri.toString();
    }

    Uri getImageUri() {
        if (mImageUri == null)
            return null;
        return Uri.parse(mImageUri);
    }

    private File getFile() {
        return new File(mContext.getFilesDir(), mFileName);
    }

    private void read(JsonReader reader) throws IOException {
        reader.beginObject();
        {
            if (readVersion(reader) == mVersion) {
                readModeClassName(reader);
                readImageUri(reader);
                readTextOverlayPath(reader);
            }
        }
        reader.endObject();
    }

    private int readVersion(JsonReader reader) throws IOException {
        reader.nextName();
        return reader.nextInt();
    }

    private void readModeClassName(JsonReader reader) throws IOException {
        reader.nextName();
        mModeClassName = reader.nextString();
    }

    private void readImageUri(JsonReader reader) throws IOException {
        reader.nextName();
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            mImageUri = null;
        } else
            mImageUri = reader.nextString();
    }

    private void readTextOverlayPath(JsonReader reader) throws IOException {
        reader.nextName();
    }
}
package com.aicodix.rattlegram;

import android.graphics.Bitmap;

import java.util.Map;

public class Entry implements Map.Entry<Long, Bitmap> {
    private final Long id;
    private Bitmap imageBitmap;
    private final String text;
    private final String timestamp;

    public Entry(Long id, Bitmap imageBitmap, String text, String timestamp) {
        this.id = id;
        this.imageBitmap = imageBitmap;
        this.text = text;
        this.timestamp = timestamp;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    @Override
    public Long getKey() {
        return id;
    }

    @Override
    public Bitmap getValue() {
        return imageBitmap;
    }

    @Override
    public Bitmap setValue(Bitmap value) {
        Bitmap oldValue = imageBitmap;
        imageBitmap = value;
        return oldValue;
    }

    public String getText() {
        return text;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
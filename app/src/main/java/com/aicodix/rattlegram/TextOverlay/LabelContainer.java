package com.aicodix.rattlegram.TextOverlay;

import android.graphics.Canvas;
import android.graphics.Rect;

import androidx.annotation.NonNull;

class LabelContainer {
    private final LabelPainter mPainter;
    private final Position mPosition;

    LabelContainer(@NonNull Label label) {
        mPainter = new LabelPainter(label);
        mPosition = new Position();
    }

    boolean contains(float x, float y) {
        return mPainter.getBounds().contains(x, y);
    }

    void draw(Canvas canvas) {
        mPainter.draw(canvas);
    }

    void drawActive(Canvas canvas) {
        mPainter.drawActive(canvas);
    }

    void draw(Canvas canvas, Rect src, Rect dst) {
        mPainter.draw(canvas, src, dst);
    }

    void jumpInside(float textSizeFactor, float screenW, float screenH) {
        mPainter.moveLabelInside(textSizeFactor, screenW, screenH, mPosition);
    }

    void offset(float x, float y) {
        mPosition.offset(x, y);
    }

    void update(float textSizeFactor, float screenW, float screenH) {
        mPainter.update(textSizeFactor, screenW, screenH, mPosition);
    }
}
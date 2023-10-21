package com.aicodix.rattlegram.TextOverlay;

import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.LinkedList;
import java.util.List;

public class LabelCollection {
    private static class Size {
        private final float mW;
        private final float mH;

        Size(float w, float h) {
            mW = w;
            mH = h;
        }

        float width() {
            return mW;
        }

        float height() {
            return mH;
        }
    }

    private final List<LabelContainer> mLabels;
    private Size mScreenSize;
    private float mTextSizeFactor;
    private LabelContainer mActiveLabel;
    private float mPreviousX, mPreviousY;

    public LabelCollection() {
        mLabels = new LinkedList<>();
        mPreviousX = 0f;
        mPreviousY = 0f;
    }

    public void update(float w, float h, float textSizeFactor) {
        if (mScreenSize != null) {
            float x = (w - mScreenSize.width()) / 2f;
            float y = (h - mScreenSize.height()) / 2f;
            for (LabelContainer label : mLabels)
                label.offset(x, y);
        }
        mScreenSize = new Size(w, h);
        mTextSizeFactor = textSizeFactor;
        for (LabelContainer label : mLabels)
            label.update(mTextSizeFactor, w, h);
    }

    public void draw(Canvas canvas) {
        for (LabelContainer label : mLabels)
            label.draw(canvas);
        if (mActiveLabel != null)
            mActiveLabel.drawActive(canvas);
    }

    public void draw(Canvas canvas, Rect src, Rect dst) {
        for (LabelContainer label : mLabels)
            label.draw(canvas, src, dst);
    }

    public boolean moveLabelBegin(float x, float y) {
        mActiveLabel = find(x, y);
        if (mActiveLabel == null)
            return false;
        mLabels.remove(mActiveLabel);
        mPreviousX = x;
        mPreviousY = y;
        mActiveLabel.jumpInside(mTextSizeFactor, mScreenSize.width(), mScreenSize.height());
        return true;
    }

    public void moveLabel(float x, float y) {
        mActiveLabel.offset(x - mPreviousX, y - mPreviousY);
        mActiveLabel.update(mTextSizeFactor, mScreenSize.width(), mScreenSize.height());
        mPreviousX = x;
        mPreviousY = y;
    }

    public void moveLabelEnd() {
        mLabels.add(mActiveLabel);
        mActiveLabel = null;
        mPreviousX = 0f;
        mPreviousY = 0f;
    }

    private LabelContainer find(float x, float y) {
        for (LabelContainer label : mLabels) {
            if (label.contains(x, y))
                return label;
        }
        return null;
    }
}
package com.aicodix.rattlegram.TextOverlay;

import android.graphics.Color;

import java.io.Serializable;

public class Label implements Serializable {
    public static final float TEXT_SIZE_NORMAL = 2f;
    public static final float OUTLINE_SIZE_NORMAL = 0.05f;
    private final String mText;
    private final float mTextSize;
    private final float mOutlineSize;
    private final String mFamilyName;
    private final boolean mBold;
    private final boolean mItalic;
    private final boolean mOutline;
    private final int mForeColor;
    private final int mOutlineColor;

    public Label() {
        mText = "";
        mTextSize = TEXT_SIZE_NORMAL;
        mFamilyName = null;
        mBold = true;
        mItalic = false;
        mForeColor = Color.BLACK;
        mOutline = true;
        mOutlineSize = OUTLINE_SIZE_NORMAL;
        mOutlineColor = Color.WHITE;
    }

    public String getText() {
        return mText;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public String getFamilyName() {
        return mFamilyName;
    }

    public boolean getBold() {
        return mBold;
    }

    public boolean getItalic() {
        return mItalic;
    }

    public int getForeColor() {
        return mForeColor;
    }

    public boolean getOutline() {
        return mOutline;
    }

    public float getOutlineSize() {
        return mOutlineSize;
    }

    public int getOutlineColor() {
        return mOutlineColor;
    }
}
package com.aicodix.rattlegram.TextOverlay;

class Position {
    private float mX;
    private float mY;

    Position() {
        mX = 0f;
        mY = 0f;
    }

    void set(float x, float y) {
        mX = x;
        mY = y;
    }

    void offset(float x, float y) {
        mX += x;
        mY += y;
    }

    float getX() {
        return mX;
    }

    float getY() {
        return mY;
    }
}
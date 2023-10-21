package com.aicodix.rattlegram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.GestureDetectorCompat;

import java.io.InputStream;

import com.aicodix.rattlegram.ModeInterfaces.ModeSize;
import com.aicodix.rattlegram.TextOverlay.LabelCollection;

public class CropView extends AppCompatImageView {
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            mLongPress = false;
            if (!mInScale && mLabelCollection.moveLabelBegin(e.getX(), e.getY())) {
                invalidate();
                mLongPress = true;
            }
        }
    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if (!mLongPress) {
                mInScale = true;
                return true;
            }
            return false;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleImage(detector.getScaleFactor());
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mInScale = false;
        }
    }

    private final GestureDetectorCompat mDetectorCompat;
    private final ScaleGestureDetector mScaleDetector;
    private boolean mLongPress, mInScale;
    private ModeSize mModeSize;
    private final Paint mPaint, mRectPaint, mBorderPaint;
    private RectF mInputRect;
    private Rect mOutputRect;
    private BitmapRegionDecoder mRegionDecoder;
    private int mImageWidth, mImageHeight;
    private Bitmap mCacheBitmap;
    private boolean mSmallImage;
    private boolean mImageOK;
    private final Rect mCanvasDrawRect, mImageDrawRect;
    private int mOrientation;
    private final Rect mCacheRect;
    private int mCacheSampleSize;
    private final BitmapFactory.Options mBitmapOptions;
    private final LabelCollection mLabelCollection;

    public CropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDetectorCompat = new GestureDetectorCompat(getContext(), new GestureListener());
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureListener());
        mBitmapOptions = new BitmapFactory.Options();
        mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        mRectPaint = new Paint();
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(1f);
        mBorderPaint = new Paint();
        mBorderPaint.setColor(Color.BLACK);
        mCanvasDrawRect = new Rect();
        mImageDrawRect = new Rect();
        mCacheRect = new Rect();
        mOutputRect = new Rect();
        mSmallImage = false;
        mImageOK = false;
        mLabelCollection = new LabelCollection();
    }

    public void setModeSize(ModeSize size) {
        mModeSize = size;
        mOutputRect = Utility.getEmbeddedRect(getWidth(), getHeight(), mModeSize.width(), mModeSize.height());
        if (mImageOK)
            resetInputRect();
        invalidate();
    }

    private void resetInputRect() {
        float iw = mModeSize.width();
        float ih = mModeSize.height();
        float ow = mImageWidth;
        float oh = mImageHeight;
        if (iw * oh > ow * ih) {
            float right = (iw * oh) / ih;
            mInputRect = new RectF(0f, 0f, right, oh);
            mInputRect.offset((ow - right) / 2f, 0f);
        } else {
            float bottom = (ih * ow) / iw;
            mInputRect = new RectF(0f, 0f, ow, bottom);
            mInputRect.offset(0f, (oh - bottom) / 2f);
        }
    }

    public void rotateImage(int orientation) {
        if (!mImageOK)
            return;
        mOrientation += orientation;
        mOrientation %= 360;
        if (orientation == 90 || orientation == 270) {
            int tmp = mImageWidth;
            mImageWidth = mImageHeight;
            mImageHeight = tmp;
        }
        resetInputRect();
        invalidate();
    }

    public void setNoBitmap() {
        mImageOK = false;
        mOrientation = 0;
        recycle();
        invalidate();
    }

    public void setBitmap(@NonNull InputStream stream) throws Exception {
        mImageOK = false;
        mOrientation = 0;
        recycle();
        loadImage(stream);
        invalidate();
    }

    private void loadImage(InputStream stream) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        byte[] streamBytes = null;
        String errorMessage = null;
        try {
            int length = stream.available();
            if (length > 0) {
                streamBytes = new byte[length];
                if (length == stream.read(streamBytes, 0, streamBytes.length)) {
                    BitmapFactory.decodeByteArray(streamBytes, 0, streamBytes.length, options);
                } else
                    streamBytes = null;
            }
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
            streamBytes = null;
        }
        mImageWidth = options.outWidth;
        mImageHeight = options.outHeight;
        if (streamBytes != null && mImageWidth > 0 && mImageHeight > 0) {
            mSmallImage = mImageWidth * mImageHeight < 1024 * 1024;
            if (mSmallImage) {
                mCacheBitmap = BitmapFactory.decodeByteArray(streamBytes, 0, streamBytes.length, null);
            } else {
                mRegionDecoder = BitmapRegionDecoder.newInstance(streamBytes, 0, streamBytes.length, true);
                mCacheRect.setEmpty();
            }
        }
        if (mCacheBitmap == null && mRegionDecoder == null) {
            String message = errorMessage;
            if (message == null) {
                message = "Stream could not be decoded.";
                if (mImageWidth > 0 && mImageHeight > 0) {
                    message += " Image size: " + mImageWidth + "x" + mImageHeight;
                }
            }
            throw new Exception(message);
        }
        mImageOK = true;
        resetInputRect();
    }

    private void recycle() {
        if (mRegionDecoder != null) {
            mRegionDecoder.recycle();
            mRegionDecoder = null;
        }
        if (mCacheBitmap != null) {
            mCacheBitmap.recycle();
            mCacheBitmap = null;
        }
    }

    public void scaleImage(float scaleFactor) {
        if (!mImageOK)
            return;
        float newW = mInputRect.width() / scaleFactor;
        float newH = mInputRect.height() / scaleFactor;
        float dx = 0.5f * (mInputRect.width() - newW);
        float dy = 0.5f * (mInputRect.height() - newH);
        float max = 2f * Math.max(mImageWidth, mImageHeight);
        if (Math.min(newW, newH) >= 4f && Math.max(newW, newH) <= max) {
            mInputRect.inset(dx, dy);
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent e) {
        boolean consumed = false;
        if (mLongPress) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    mLabelCollection.moveLabel(e.getX(), e.getY());
                    invalidate();
                    consumed = true;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mLabelCollection.moveLabelEnd();
                    invalidate();
                    mLongPress = false;
                    consumed = true;
                    break;
            }
        }
        consumed = mScaleDetector.onTouchEvent(e) || consumed;
        return mDetectorCompat.onTouchEvent(e) || consumed || super.onTouchEvent(e);
    }

    @Override
    protected void onSizeChanged(int w, int h, int old_w, int old_h) {
        super.onSizeChanged(w, h, old_w, old_h);
        if (mModeSize != null)
            mOutputRect = Utility.getEmbeddedRect(w, h, mModeSize.width(), mModeSize.height());
        mLabelCollection.update(w, h, Utility.getTextSizeFactor(w, h));
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (mImageOK) {
            maximizeImageToCanvasRect();
            adjustCanvasAndImageRect(getWidth(), getHeight());
            canvas.drawRect(mOutputRect, mBorderPaint);
            drawBitmap(canvas);
        }
        mLabelCollection.draw(canvas);
        drawModeRect(canvas);
    }

    private void maximizeImageToCanvasRect() {
        if (mInputRect == null) {
            // Handle the case when mInputRect is null (initialize it or handle accordingly)
            return;
        }

        float l = mOutputRect.left * mInputRect.width() / mOutputRect.width();
        float t = mOutputRect.top * mInputRect.height() / mOutputRect.height();
        float r = (mOutputRect.right - getWidth()) * mInputRect.width() / mOutputRect.width();
        float b = (mOutputRect.bottom - getHeight()) * mInputRect.height() / mOutputRect.height();
        mImageDrawRect.left = Math.round(mInputRect.left - l);
        mImageDrawRect.top = Math.round(mInputRect.top - t);
        mImageDrawRect.right = Math.round(mInputRect.right - r);
        mImageDrawRect.bottom = Math.round(mInputRect.bottom - b);
    }

    private void adjustCanvasAndImageRect(int width, int height) {
        mCanvasDrawRect.set(0, 0, width, height);
        if (mImageDrawRect.left < 0) {
            mCanvasDrawRect.left -= (mImageDrawRect.left * mCanvasDrawRect.width()) / mImageDrawRect.width();
            mImageDrawRect.left = 0;
        }
        if (mImageDrawRect.top < 0) {
            mCanvasDrawRect.top -= (mImageDrawRect.top * mCanvasDrawRect.height()) / mImageDrawRect.height();
            mImageDrawRect.top = 0;
        }
        if (mImageDrawRect.right > mImageWidth) {
            mCanvasDrawRect.right -= ((mImageDrawRect.right - mImageWidth) * mCanvasDrawRect.width()) / mImageDrawRect.width();
            mImageDrawRect.right = mImageWidth;
        }
        if (mImageDrawRect.bottom > mImageHeight) {
            mCanvasDrawRect.bottom -= ((mImageDrawRect.bottom - mImageHeight) * mCanvasDrawRect.height()) / mImageDrawRect.height();
            mImageDrawRect.bottom = mImageHeight;
        }
    }

    private void drawModeRect(Canvas canvas) {
        mRectPaint.setColor(Color.BLUE);
        canvas.drawRect(mOutputRect, mRectPaint);
        mRectPaint.setColor(Color.GREEN);
        drawRectInset(canvas, mOutputRect, -1);
        mRectPaint.setColor(Color.RED);
        drawRectInset(canvas, mOutputRect, -2);
    }

    private void drawRectInset(Canvas canvas, Rect rect, int inset) {
        canvas.drawRect(
                rect.left + inset,
                rect.top + inset,
                rect.right - inset,
                rect.bottom - inset, mRectPaint);
    }

    private Rect getIntRect(RectF rect) {
        return new Rect(
                Math.round(rect.left),
                Math.round(rect.top),
                Math.round(rect.right),
                Math.round(rect.bottom));
    }

    private int getSampleSize() {
        int sx = Math.round(mInputRect.width() / mModeSize.width());
        int sy = Math.round(mInputRect.height() / mModeSize.height());
        int scale = Math.max(1, Math.max(sx, sy));
        return Integer.highestOneBit(scale);
    }

    public Bitmap getBitmap() {
        Bitmap result = Bitmap.createBitmap(mModeSize.width(), mModeSize.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawColor(Color.BLACK);
        if (mImageOK) {
            mImageDrawRect.set(getIntRect(mInputRect));
            adjustCanvasAndImageRect(mModeSize.width(), mModeSize.height());
            drawBitmap(canvas);
        }
        mLabelCollection.draw(canvas, mOutputRect, new Rect(0, 0, mModeSize.width(), mModeSize.height()));
        return result;
    }

    private void drawBitmap(Canvas canvas) {
        canvas.save();
        canvas.rotate(mOrientation);
        rotateDrawRectangles();
        if (!mSmallImage) {
            updateCache();
            mImageDrawRect.offset(-mCacheRect.left, -mCacheRect.top);
            mImageDrawRect.left /= mCacheSampleSize;
            mImageDrawRect.top /= mCacheSampleSize;
            mImageDrawRect.right /= mCacheSampleSize;
            mImageDrawRect.bottom /= mCacheSampleSize;
        }
        canvas.drawBitmap(mCacheBitmap, mImageDrawRect, mCanvasDrawRect, mPaint);
        canvas.restore();
    }

    private void rotateDrawRectangles() {
        int w = mImageWidth;
        int h = mImageHeight;
        for (int i = 0; i < mOrientation / 90; ++i) {
            int tmp = w;
            w = h;
            h = tmp;
            mImageDrawRect.set(
                    mImageDrawRect.top,
                    h - mImageDrawRect.left,
                    mImageDrawRect.bottom,
                    h - mImageDrawRect.right);
            mCanvasDrawRect.set(
                    mCanvasDrawRect.top,
                    -mCanvasDrawRect.right,
                    mCanvasDrawRect.bottom,
                    -mCanvasDrawRect.left);
        }
        mImageDrawRect.sort();
    }

    private void updateCache() {
        int sampleSize = getSampleSize();
        if (sampleSize >= mCacheSampleSize && mCacheRect.contains(mImageDrawRect))
            return;
        if (mCacheBitmap != null)
            mCacheBitmap.recycle();
        int cacheWidth = mImageDrawRect.width();
        int cacheHeight = mImageDrawRect.height();
        while (cacheWidth * cacheHeight < (sampleSize * 1024 * sampleSize * 1024)) {
            cacheWidth += mImageDrawRect.width();
            cacheHeight += mImageDrawRect.height();
        }
        int left = -sampleSize & (mImageDrawRect.centerX() - cacheWidth / 2);
        int top = -sampleSize & (mImageDrawRect.centerY() - cacheHeight / 2);
        int right = -sampleSize & (mImageDrawRect.centerX() + cacheWidth / 2 + sampleSize - 1);
        int bottom = -sampleSize & (mImageDrawRect.centerY() + cacheHeight / 2 + sampleSize - 1);
        mCacheRect.set(
                Math.max(0, left),
                Math.max(0, top),
                Math.min(mRegionDecoder.getWidth(), right),
                Math.min(mRegionDecoder.getHeight(), bottom));
        mBitmapOptions.inSampleSize = mCacheSampleSize = sampleSize;
        mCacheBitmap = mRegionDecoder.decodeRegion(mCacheRect, mBitmapOptions);
    }
}
package com.example.eduhub.settings.widgets;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.appcompat.widget.AppCompatImageView;

public class ZoomImageView extends AppCompatImageView {

    private Matrix baseMatrix = new Matrix();
    private Matrix transformMatrix = new Matrix();
    private float[] matrixValues = new float[9];

    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;

    private float minScale = 1f;
    private float maxScale = 5f;

    private PointF lastTouch = new PointF();
    private boolean isDragging = false;

    public ZoomImageView(Context context) {
        super(context);
        init();
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setScaleType(ScaleType.MATRIX);

        scaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        gestureDetector = new GestureDetector(getContext(), new GestureListener());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) resetZoom();
    }

    public void resetZoom() {
        if (getDrawable() == null) return;

        int dw = getDrawable().getIntrinsicWidth();
        int dh = getDrawable().getIntrinsicHeight();
        int vw = getWidth();
        int vh = getHeight();

        if (dw <= 0 || dh <= 0 || vw <= 0 || vh <= 0) return;

        float scale = Math.min((float) vw / dw, (float) vh / dh);
        float dx = (vw - dw * scale) / 2f;
        float dy = (vh - dh * scale) / 2f;

        baseMatrix.reset();
        baseMatrix.postScale(scale, scale);
        baseMatrix.postTranslate(dx, dy);
        minScale = scale;

        transformMatrix.reset();
        setImageMatrix(baseMatrix);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                lastTouch.set(event.getX(), event.getY());
                isDragging = true;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!scaleDetector.isInProgress() && isDragging) {
                    float dx = event.getX() - lastTouch.x;
                    float dy = event.getY() - lastTouch.y;
                    lastTouch.set(event.getX(), event.getY());
                    panBy(dx, dy);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                isDragging = false;
                break;
            }
        }
        return true;
    }

    private void panBy(float dx, float dy) {
        transformMatrix.postTranslate(dx, dy);
        applyTransform();
    }

    private void scaleBy(float focusX, float focusY, float factor) {
        transformMatrix.postScale(factor, factor, focusX, focusY);

        getImageMatrix().getValues(matrixValues);
        float currentScale = matrixValues[Matrix.MSCALE_X];
        float overallScale = currentScale * getBaseScale();

        if (overallScale < minScale) {
            float correction = minScale / overallScale;
            transformMatrix.postScale(correction, correction, focusX, focusY);
        } else if (overallScale > maxScale) {
            float correction = maxScale / overallScale;
            transformMatrix.postScale(correction, correction, focusX, focusY);
        }

        applyTransform();
    }

    private float getBaseScale() {
        baseMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    private void applyTransform() {
        Matrix m = new Matrix(baseMatrix);
        m.postConcat(transformMatrix);
        setImageMatrix(m);
    }

    
    public float[] getVisibleCropRect(int cropDiameterDp) {
        float density = getResources().getDisplayMetrics().density;
        float cropDiameterPx = cropDiameterDp * density;

        Matrix full = new Matrix(baseMatrix);
        full.postConcat(transformMatrix);

        Matrix inverse = new Matrix();
        full.invert(inverse);

        int vw = getWidth();
        int vh = getHeight();

        float cx = vw / 2f;
        float cy = vh / 2f;
        float half = cropDiameterPx / 2f;

        float[] src = new float[]{
                cx - half, cy - half,
                cx + half, cy + half
        };
        float[] dst = new float[4];
        inverse.mapPoints(dst, src);

        float ix = dst[0];
        float iy = dst[1];
        float ix2 = dst[2];
        float iy2 = dst[3];

        float size = Math.max(ix2 - ix, iy2 - iy);
        return new float[]{ix, iy, size};
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float factor = detector.getScaleFactor();
            scaleBy(detector.getFocusX(), detector.getFocusY(), factor);
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            getImageMatrix().getValues(matrixValues);
            float currentScale = matrixValues[Matrix.MSCALE_X] * getBaseScale();

            if (currentScale > minScale * 1.5f) {
                transformMatrix.reset();
                applyTransform();
            } else {
                scaleBy(e.getX(), e.getY(), 2f);
            }
            return true;
        }
    }
}

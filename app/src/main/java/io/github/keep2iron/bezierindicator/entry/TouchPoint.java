package io.github.keep2iron.bezierindicator.entry;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/07/31 13:59
 */
public class TouchPoint {
    public int centerX;
    public int centerY;
    public float R;

    public boolean isShow;
    public Paint mPaint;

    public static float STROKE_WIDTH;

    public TouchPoint() {
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#ffffff"));
    }

    public void setStrokeWidth(float strokeWidth) {
        mPaint.setStrokeWidth(strokeWidth);
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public boolean isShow() {
        return isShow;
    }
}
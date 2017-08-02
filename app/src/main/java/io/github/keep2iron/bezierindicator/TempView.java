package io.github.keep2iron.bezierindicator;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/08/02 16:16
 */
public class TempView extends View {
    BezierCircle mBezierCircle;
    private int M;                          //起始点和控制点的距离
    private int R;                          //圆的半径

    private float C = 0.551915024494f;      //如果要画圆，控制点的距离与半径的比值是这个值
    private Paint mPaint;

    public TempView(Context context) {
        this(context, null);
    }

    public TempView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TempView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        R = 60;
        M = (int) (R * C);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);              //防锯齿
        mPaint.setDither(true);                 //防抖动
        mPaint.setColor(Color.parseColor("#ff7e7b"));           //设置颜色
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);           //设置画笔的Style

        mBezierCircle = new BezierCircle(R, M);
    }

    public void start(final float start,final float end) {
        mBezierCircle.drawByPositionOffset(0, 1, start);
        invalidate();

        postDelayed(new Runnable() {
            @Override
            public void run() {
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(start, end)
                        .setDuration(5000);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        Log.e("tag", "" + value);
                        mBezierCircle.drawByPositionOffset(0, 1, value);
                        invalidate();
                    }
                });
                valueAnimator.start();
            }
        }, 3000);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawMoveCircle(canvas);
    }

    /**
     * 画出移动的小球
     *
     * @param canvas 画布
     */
    private void drawMoveCircle(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);

        Path path = mBezierCircle.buildPath();
        canvas.drawPath(path, mPaint);
        canvas.restore();
    }
}

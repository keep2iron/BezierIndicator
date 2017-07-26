package io.github.keep2iron.bezierindicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/07/19 15:28
 */
public class BezierIndicator extends View implements ViewPager.OnPageChangeListener {
    private Paint mPaint;                           //画笔
    private Point mStartPoint;

    private int M;                                  //起始点和控制点的距离
    private int R = 50;                             //圆的半径

    private float C = 0.551915024494f;              //如果要画圆，控制点的距离与半径的比值是这个值

    BezierCircle mBezierCircle;

    private boolean isInitlize;                     //是否要进行初始化,因为进行坐标计算

    private float mCurrentPercent;                  //当前滑动的百分比

    int span;                                       //元素之间的间隔距离

    int currentPosition;
    boolean isTurnLeft;
    int[] roundColors = new int[4];

    int color;

    public BezierIndicator(Context context) {
        this(context, null);
    }

    public BezierIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BezierIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                heightSize = 2 * R + 30;        //默认30的内边距
                break;
        }

        setMeasuredDimension(widthSize,heightSize);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);              //防锯齿
        mPaint.setDither(true);                 //防抖动
        mPaint.setColor(Color.parseColor("#ff7e7b"));           //设置颜色
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);           //设置画笔的Style
        mPaint.setStrokeWidth(3);               //设置画笔的宽度

        M = (int) (R * C);

        roundColors[0] = Color.parseColor("#B04285F4");
        roundColors[1] = Color.parseColor("#B0EA4335");
        roundColors[2] = Color.parseColor("#B0FBBC05");
        roundColors[3] = Color.parseColor("#B034A853");

        color = roundColors[0];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int count = 3;

        span = (getWidth() - (count * R * 2)) / (count + 1);
        mStartPoint = new Point(span + R, h / 2);
        mBezierCircle = new BezierCircle(R, M);

        isInitlize = true;
    }

    float lastOffset;
    int lasPositionOffsetPixels;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (!isInitlize) return;

        mBezierCircle.drawByPositionOffset(positionOffset);
        mCurrentPercent = positionOffset;
        currentPosition = position;

        lastOffset = positionOffset;

        isTurnLeft = (positionOffsetPixels - lasPositionOffsetPixels) < 0;
        lasPositionOffsetPixels = positionOffsetPixels;

        int startPosition = position;
        int toPosition = isTurnLeft ? position - 1 : position + 1;
        toPosition = toPosition < 0 ? 0 : toPosition;
        toPosition = toPosition > 2 ? 2 : toPosition;
        color = mBezierCircle.getCurrentColor(positionOffset,roundColors[startPosition],roundColors[toPosition]);
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(state == ViewPager.SCROLL_STATE_IDLE && lastOffset > 0 && lastOffset < 1.0f){
            float toOffset = lastOffset <= 0.5f ? 0 : 1.0f;
            mBezierCircle.drawByPositionOffset(toOffset);
        }

        if (state == ViewPager.SCROLL_STATE_SETTLING) {
            Log.e("tag","is " + isTurnLeft);
            mBezierCircle.wave(isTurnLeft,this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mStartPoint.x
                + (span + 2 * R) * mCurrentPercent
                + (span + 2 * R)* (currentPosition), mStartPoint.y);

        Path path = mBezierCircle.buildPath();
        mPaint.setColor(color);
        canvas.drawPath(path, mPaint);

        canvas.restore();
    }
}

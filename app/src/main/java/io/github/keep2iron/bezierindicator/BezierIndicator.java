package io.github.keep2iron.bezierindicator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import io.github.keep2iron.bezierindicator.entry.TouchPoint;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/07/19 15:28
 */
public class BezierIndicator extends View implements ViewPager.OnPageChangeListener {
    private Paint mPaint;                           //画笔
    private Point mStartPoint;

    private int M;                                  //起始点和控制点的距离
    private int R;                             //圆的半径

    private float C = 0.551915024494f;              //如果要画圆，控制点的距离与半径的比值是这个值

    BezierCircle mBezierCircle;

    private boolean isInitlize;                     //是否要进行初始化,因为进行坐标计算

    private float mCurrentPercent;                  //当前滑动的百分比

    int span;                                       //元素之间的间隔距离

    int mCurrentPosition;                            //当前page的Position，跟随ViewPager的滑动的变化而变化

    int[] roundColors = new int[3];

    Bitmap[] mBitmaps = new Bitmap[3];
    RectF[] mBitmapRects = new RectF[3];

    int color;
    private PaintFlagsDrawFilter canvasFilter;
    private Paint mCircleBgPaint;
    private Paint mBitmapPaint;

    int startColor;
    int endColor;

    int translateMoveSize;              //平移移动距离

    TouchPoint mTouchPoint = new TouchPoint();
    private ViewPager mViewPager;

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

        int minR = 50;

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                heightSize = 2 * minR + 60;        //默认30的内边距
                break;
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);              //防锯齿
        mPaint.setDither(true);                 //防抖动
        mPaint.setColor(Color.parseColor("#ff7e7b"));           //设置颜色
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);           //设置画笔的Style

        mCircleBgPaint = new Paint();
        mCircleBgPaint.setAntiAlias(true);              //防锯齿
        mCircleBgPaint.setDither(true);                 //防抖动

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);              //防锯齿
        mBitmapPaint.setDither(true);                 //防抖动

        roundColors[0] = getResources().getColor(io.github.keep2iron.bezierindicator.R.color.color1);
        roundColors[1] = getResources().getColor(io.github.keep2iron.bezierindicator.R.color.color2);
        roundColors[2] = getResources().getColor(io.github.keep2iron.bezierindicator.R.color.color3);

        color = roundColors[0];
        startColor = roundColors[0];
        endColor = roundColors[1];

        canvasFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        mBitmaps[0] = BitmapFactory.decodeResource(getResources(), io.github.keep2iron.bezierindicator.R.drawable.camera);
        mBitmaps[1] = BitmapFactory.decodeResource(getResources(), io.github.keep2iron.bezierindicator.R.drawable.msg);
        mBitmaps[2] = BitmapFactory.decodeResource(getResources(), io.github.keep2iron.bezierindicator.R.drawable.notice);
    }

    public void setUpWithViewPager(ViewPager viewPager){
        this.mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        R = 60;

        int count = 3;
        M = (int) (R * C);

        span = (getWidth() - (count * R * 2)) / (count + 1);
        mStartPoint = new Point(span + R, h / 2);
        mBezierCircle = new BezierCircle(R, M);

        isInitlize = true;

        float size = 0.5f;
        for (int i = 0; i < mBitmapRects.length; i++) {
            mBitmapRects[i] = new RectF(
                    mStartPoint.x + (span + 2 * R) * i - R * size,
                    mStartPoint.y - R * size,
                    mStartPoint.x + (span + 2 * R) * i + R * size,
                    mStartPoint.y + R * size);
        }

        mTouchPoint.STROKE_WIDTH = 1.f / 3 * R;

        translateMoveSize = span + 2 * R;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (!isInitlize) return;
        if(!isMoveByTouch) moveBezierCirclePercent(position, positionOffset);
    }

    /**
     * 移动Bezier圆
     *
     * @param position
     * @param positionOffset
     */
    private void moveBezierCirclePercent(int position, float positionOffset) {
        mBezierCircle.drawByPositionOffset(positionOffset); //通过当前的偏移计算小圆的形状

        boolean right = position + positionOffset - mCurrentPosition > 0;
        if (positionOffset != 0.0f) {
            //位移未完成
            startColor = roundColors[mCurrentPosition];
            endColor = roundColors[(mCurrentPosition + (right ? 1 : - 1)) % roundColors.length];
            color = mBezierCircle.getCurrentColor(right ? positionOffset : 1 - positionOffset, startColor, endColor);
        }

        mCurrentPosition = position;     //用于计算当前偏移的position
        mCurrentPercent = positionOffset;

        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.setDrawFilter(canvasFilter);     //开启画布抗锯齿

        drawTouchWave(canvas);
        drawCircleBg(canvas);
        drawMoveCircle(canvas);
        drawBitmap(canvas);
    }

    private void drawTouchWave(Canvas canvas) {
        if(mTouchPoint.isShow()) {
            canvas.drawCircle(mTouchPoint.getCenterX(),mTouchPoint.getCenterY(),mTouchPoint.R,mTouchPoint.mPaint);
        }
    }

    private void drawCircleBg(Canvas canvas) {
        canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);

        for (int i = 0; i < mBitmaps.length; i++) {
            mCircleBgPaint.setStyle(Paint.Style.FILL);
            mCircleBgPaint.setColor(Color.parseColor("#30ffffff"));
            canvas.drawCircle(mBitmapRects[i].centerX(), mBitmapRects[i].centerY(), R, mCircleBgPaint);
        }
    }

    private void drawBitmap(Canvas canvas) {
        mBitmapPaint.setColor(Color.WHITE);
        canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);

        for (int i = 0; i < mBitmaps.length; i++) {
            canvas.drawRect(mBitmapRects[i], mBitmapPaint);
            mBitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawBitmap(mBitmaps[i], null, mBitmapRects[i], mBitmapPaint);
            mBitmapPaint.setXfermode(null);
        }
    }

    /**
     * 画出移动的小球
     *
     * @param canvas 画布
     */
    private void drawMoveCircle(Canvas canvas) {
        canvas.save();

        canvas.translate(mStartPoint.x
                + (translateMoveSize) * mCurrentPercent
                + (span + 2 * R) * mCurrentPosition, mStartPoint.y);

        Log.e("tag","mCurrentPercent : " + mCurrentPercent + " translateMoveSize : " + translateMoveSize);

        Path path = mBezierCircle.buildPath();
        mPaint.setColor(color);
        canvas.drawPath(path, mPaint);
        canvas.restore();
    }

    private int getDistance(float x1,float y1,float x2,float y2){
        return (int) Math.sqrt(Math.pow(x1 - x2,2) + Math.pow(y1 - y2,2));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < mBitmapRects.length; i++) {
                    int distance = getDistance(mBitmapRects[i].centerX(), mBitmapRects[i].centerY(), event.getX(), event.getY());
                    if(distance <= R){
                        mTouchPoint.setCenterX((int) mBitmapRects[i].centerX());
                        mTouchPoint.setCenterY((int) mBitmapRects[i].centerY());
                        mTouchPoint.setShow(true);
                        startWave();
                        startMoveBezierCircleByTouch(mCurrentPosition,i);
                        break;
                    }
                }
                break;
        }

        return true;
    }

    /**
     * 开启点击的水波纹
     */
    private void startWave(){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(R, 4.0f / 3 * R);
        valueAnimator.setDuration(400);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                float percent = (value - R) / (1.f / 3 * R);
                mTouchPoint.setStrokeWidth(TouchPoint.STROKE_WIDTH * (1 - percent));
                mTouchPoint.R = value;
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mTouchPoint.setShow(false);
                invalidate();
            }
        });
        valueAnimator.start();
    }

    boolean isMoveByTouch;
    private void startMoveBezierCircleByTouch(final int formPos, final int toPos){
        if(formPos == toPos) return;
        final boolean isTurnRight = toPos - formPos > 0;
        translateMoveSize =  Math.abs(toPos - formPos) *(span + 2 * R);

        mViewPager.setCurrentItem(toPos);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, Math.abs(translateMoveSize));
        valueAnimator.setDuration(600);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                isMoveByTouch = true;
                int value = (int) animation.getAnimatedValue();

                float percent = Math.abs(value * 1.0f) / Math.abs((toPos - formPos) *(span + 2 * R));
                if(percent >= 1.0f) {
                    isMoveByTouch = false;
                    translateMoveSize = span + 2 * R;
                    mCurrentPosition = toPos;
                    moveBezierCirclePercent(toPos, 0.0f);
                }else {
                    Log.e("tag","value : " + value);
                    moveBezierCirclePercent(isTurnRight ? formPos : toPos, isTurnRight ? percent : 1.00003f - percent);
                }
            }
        });
        valueAnimator.start();
    }
}
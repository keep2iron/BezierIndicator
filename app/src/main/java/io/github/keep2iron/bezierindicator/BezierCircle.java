package io.github.keep2iron.bezierindicator;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Path;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import io.github.keep2iron.bezierindicator.entry.HorizontalPoint;
import io.github.keep2iron.bezierindicator.entry.VerticalPoint;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/07/24 13:25
 * <p>
 * bezier控制点到p点的距离为C
 * -p3-
 * |                    |
 * p4                   p2
 * |                    |
 * -p1-
 * 具有水平方向上的控制点p1 和 p3
 */
public class BezierCircle {
    private HorizontalPoint p1, p3;
    private VerticalPoint p2, p4;

    private Path mPath;

    private int R;
    private float M;

    public BezierCircle(int R, int M) {
        this.mPath = new Path();
        this.R = R;
        this.M = M;

        p1 = new HorizontalPoint(0, R, M);
        p2 = new VerticalPoint(R, 0, M);
        p3 = new HorizontalPoint(0, -R, M);
        p4 = new VerticalPoint(-R, 0, M);

        mPath = new Path();
    }

    public Path buildPath() {
        mPath.reset();

        mPath.reset();
        mPath.moveTo(p1.x, p1.y);
        mPath.cubicTo(p1.rightX, p1.rightY, p2.bottomX, p2.bottomY, p2.x, p2.y);
        mPath.cubicTo(p2.topX, p2.topY, p3.rightX, p3.rightY, p3.x, p3.y);
        mPath.cubicTo(p3.leftX, p3.leftY, p4.topX, p4.topY, p4.x, p4.y);
        mPath.cubicTo(p4.bottomX, p4.bottomY, p1.leftX, p1.leftY, p1.x, p1.y);

        return mPath;
    }

    /**
     * 通过ViewPager的百分比进行控制显示的状态
     *
     * @param fromPos
     * @param toPos
     * @param positionOffset Value from [0, 1) indicating the offset from the page at position.
     * @see android.support.v4.view.ViewPager.OnPageChangeListener
     */
    public void drawByPositionOffset(int fromPos, int toPos, float positionOffset) {
        boolean isTurnRight = toPos - fromPos > 0;

        if (positionOffset >= 0 && positionOffset <= 0.2f) {
            buildCircle1(positionOffset, isTurnRight);
        } else if (positionOffset <= 0.5f) {
            buildCircle2(positionOffset, isTurnRight);
        } else if (positionOffset <= 0.8f) {
            buildCircle3(positionOffset, isTurnRight);
        } else if (positionOffset <= 0.9f) {
            buildCircle4(positionOffset, isTurnRight);
        } else if (positionOffset <= 1.0f) {
            buildCircle5(positionOffset, isTurnRight);
        }
    }

    private void buildCircle5(float positionOffset, boolean isTurnRight) {
        double value = Math.sin(Math.toRadians((positionOffset - 0.9f) / 0.1f * 180));
        Log.e("tag","value : " + value);
        if(isTurnRight){
            p4.setX((float) (-R +  R / 2.0f * value));
        } else {
            p2.setX((float) (R -  R / 2.0f * value));
        }
    }

    private void buildCircle4(float positionOffset, boolean isTurnRight) {       //0.8 - 09
        if(isTurnRight) {
            float p4X = -9 / 5.f * R + 4 / 5.f * R * (positionOffset - 0.8f) / 0.1f;
            p4.setX(p4X);

            p1.setX(0);
            p3.setX(0);
            p2.setX(R);
        }else{
            float p2X = 9 / 5.f * R - 4 / 5.f * R * (positionOffset - 0.8f) / 0.1f;
            p2.setX(p2X);

            p1.setX(0);
            p3.setX(0);
            p4.setX(-R);
        }
    }

    /**
     * 在这个过程中需要将circle从一个椭圆变成左边较尖锐的椭圆
     *
     * @param positionOffset
     * @param isTurnRight
     */
    private void buildCircle3(float positionOffset, boolean isTurnRight) {       //0.5 - 0.8f
        if (isTurnRight) {
            float x = ((R + R + 4 / 5.f * R) / 2.f - R) - ((R + R + 4 / 5.f * R) / 2.f - R) * (positionOffset - 0.5f) / 0.3f;
            p1.setX(x);
            p3.setX(x);

            float m = 5.f * M / 3 - 2.f * M / 3 * (positionOffset - 0.5f) / 0.3f;
            p2.setM(m);
            p4.setM(m);

            float p4X = -R - 4 / 5.f * R * (positionOffset - 0.5f) / 0.3f;
            p4.setX(p4X);

            float p2X = 9 / 5.f * R - 4 / 5.f * R * (positionOffset - 0.5f) / 0.3f;
            p2.setX(p2X);
        } else {
            float x = ((R + R + 4 / 5.f * R) / 2.f - R) - ((R + R + 4 / 5.f * R) / 2.f - R) * (positionOffset - 0.5f) / 0.3f;
            p1.setX(-x);
            p3.setX(-x);

            float m = 5.f * M / 3 - 2.f * M / 3 * (positionOffset - 0.5f) / 0.3f;
            p2.setM(m);
            p4.setM(m);

            float p2X = R + 4 / 5.f * R * (positionOffset - 0.5f) / 0.3f;
            p2.setX(p2X);

            float p4X = -9 / 5.f * R + 4 / 5.f * R * (positionOffset - 0.5f) / 0.3f;
            p4.setX(p4X);
        }
    }

    /**
     * 在这个过程中需要将Circle变成一个椭圆
     * 这个里p2点的x是R + 4 / 5R,因此椭圆的长轴的距离为 R+ R + 4 / 5R = 14 / 5R
     * <p>
     * 然后 14 / 5R /2 为长轴一半 - R即为椭圆中心点的坐标
     *
     * @param positionOffset
     * @param isTurnRight
     */
    private void buildCircle2(float positionOffset, boolean isTurnRight) {   //0.2 < pos <= 0.5f
        if (isTurnRight) {
            p2.setX(R + 4 / 5.f * R);

            float x = ((R + R + 4 / 5.f * R) / 2.f - R) * (positionOffset - 0.2f) / 0.3f;
            p1.setX(x);
            p3.setX(x);

            float m = M + M * 2 / 3.f * (positionOffset - 0.2f) / 0.3f;
            p4.setM(m);
            p2.setM(m);
        } else {
            p4.setX(-R - 4 / 5.f * R);

            float x = ((R + R + 4 / 5.f * R) / 2.f - R) * (positionOffset - 0.2f) / 0.3f;
            p1.setX(-x);
            p3.setX(-x);

            float m = M + M * 2 / 3.f * (positionOffset - 0.2f) / 0.3f;
            p4.setM(m);
            p2.setM(m);
        }
    }

    /**
     * 因为变化率是0 - 0.2f区间进行变化, 而p2点的x坐标则是从R - 4/5R之间进行变化,
     * 因此x = R + (4/5R * (变化率的百分比))
     * 变化率的百分比为 pos / 0.2f   0.2为区间值,pos在0 - 0.2f之间变化
     *
     * @param positionOffset
     * @param isTurnRight
     */
    private void buildCircle1(float positionOffset, boolean isTurnRight) {   //0 < pos <=0.2f
        if (isTurnRight) {
            p1.setX(0);
            p3.setX(0);
            p4.setX(-R);

            p2.setX(R + 4 / 5.0f * R * positionOffset / 0.2f);
        } else {
            p1.setX(0);
            p3.setX(0);
            p2.setX(R);

            p4.setX(-R - 4 / 5.0f * R * positionOffset / 0.2f);
        }
    }


    public void wave(final boolean isTurnRight) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, (float) Math.PI);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(400);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (isTurnRight) {
                    p4.setX((float) (-R + R / 3.0f * Math.sin(value)));
                    p2.setX(R);
                } else {
                    p2.setX((float) (R - R / 3.0f * Math.sin(value)));
                    p4.setX(-R);
                }
            }
        });
        valueAnimator.start();
    }

    private int[][] f = new int[2][3];
    private int[] result = new int[3];

    public int getCurrentColor(float percent, int startColor, int endColor) {
        f[0][0] = (startColor & 0xff0000) >> 16;
        f[0][1] = (startColor & 0x00ff00) >> 8;
        f[0][2] = (startColor & 0x0000ff);

        f[1][0] = (endColor & 0xff0000) >> 16;
        f[1][1] = (endColor & 0x00ff00) >> 8;
        f[1][2] = (endColor & 0x0000ff);

        for (int i = 0; i < 3; i++) {
            result[i] = (int) (f[0][i] + (f[1][i] - f[0][i]) * percent);
        }

        return Color.rgb(result[0], result[1], result[2]);
    }
}
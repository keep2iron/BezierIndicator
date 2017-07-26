package io.github.keep2iron.bezierindicator.entry;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/07/24 16:34
 *
 *          -p3-
 * |                    |
 * p4                   p2
 * |                    |
 *          -p1-
 *
 * 具有垂直方向的控制点分别是 p2 p4
 */
public class VerticalPoint {
    public float x,y;
    public float m;

    public float topY;
    public float bottomY;

    public float bottomX;
    public float topX;

    public VerticalPoint(float x, float y, float m) {
        this.x = x;
        this.y = y;
        this.m = m;

        topY = y - m;
        bottomY = y + m;

        topX = x;
        bottomX = x;
    }

    public void setX(float x){
        this.x = x;
        this.topX = x;
        this.bottomX = x;
    }

    public void setM(float m) {
        this.m = m;
        this.topY = y - m;
        this.bottomY = y + m;
    }
}
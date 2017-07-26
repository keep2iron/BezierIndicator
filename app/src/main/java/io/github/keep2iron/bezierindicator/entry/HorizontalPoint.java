package io.github.keep2iron.bezierindicator.entry;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/07/24 16:36
 *
 * bezier控制点到p点的距离为M
 *          -p3-
 * |                    |
 * p4                   p2
 * |                    |
 *          -p1-
 * 具有水平方向上的控制点p1 和 p3
 */
public class HorizontalPoint {
    public float x,y;              //基本点的坐标
    public float m;                //控制点到基本点的距离

    public float leftX;            //左边的控制点
    public float rightX;           //右边的控制点

    public float rightY;
    public float leftY;

    public HorizontalPoint(float x, float y, float m) {
        this.x = x;
        this.y = y;
        this.m = m;

        leftX = x - m;
        rightX = x + m;

        leftY = y;
        rightY = y;
    }

    public void setX(float x){
        this.x = x;

        leftX = x - m;
        rightX = x + m;
    }
}

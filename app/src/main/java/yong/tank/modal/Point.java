package yong.tank.modal;

/**
 * Created by hasee on 2016/11/7.
 */

public class Point {

    public Point() {
        //默认point为null
        this.flag=true;
    }

    public Point(float x, float y, int degree, boolean flag) {
        this.x = x;
        this.y = y;
        this.degree = degree;
        this.flag = flag;
    }

    private float x;
    private float y;
    //点此时的斜率 有的可以没有
    private int degree;
    private boolean flag;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isPointNull(){
        return flag;
    }
    public void setPointNull(){
        this.flag=true;
    }
    public void setPointNotNull(){
        this.flag=false;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }
}

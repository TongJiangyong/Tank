package yong.tank.modal;

/**
 * Created by hasee on 2016/11/7.
 */

public class Point {

    public Point() {
        //默认point为null
        this.flag=true;
    }

    private int x;
    private int y;
    private boolean flag;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
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
}

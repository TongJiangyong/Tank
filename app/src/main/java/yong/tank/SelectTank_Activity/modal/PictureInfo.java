package yong.tank.SelectTank_Activity.modal;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by hasee on 2016/10/28.
 */

public  abstract class PictureInfo{
    //记录图片
    private Bitmap Picture;
    //记录图片的位置
    private Rect rect;

    public Bitmap getPicture() {
        return Picture;
    }

    public void setPicture(Bitmap picture) {
        Picture = picture;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    //判断picture是否包括点......
    public  boolean isContainPoint(int x,int y){
        if(this.getRect()==null){
            return false;
        }
        return this.getRect().contains(x,y);
    }
    //获取图片的中心店
    public Point getCentralPoint(){
        if(this.getRect()==null){
            return new Point(this.getRect().centerX(),this.getRect().centerY());
        }
        return null;
    }

    abstract public void test();
}

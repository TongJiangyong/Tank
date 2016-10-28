package yong.tank.SelectRoom.modal;

import android.graphics.Bitmap;
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

//判断是否点击到图片
    abstract public void test();
}

package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.io.Serializable;

import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/1.
 */

public class Explode implements Serializable {
    private transient Bitmap explodeFrame[];
    private transient int currentFrame= 0;
    private int explodeType= 0;  //0为ground ，1为tank
    private float explode_x;
    private float explode_y;
    private transient static String TAG = "Explode";
    //TODO 以后加上角度
    public Explode(Bitmap[] explodeFrame, float explode_x, float explode_y,int explodeType) {
        this.explodeFrame = explodeFrame;
        this.explode_x = explode_x;
        this.explode_y = explode_y;
        this.explodeType=explodeType;
    }

    public void drawSelf(Canvas canvas) {
        //计算血条的比例
        canvas.drawBitmap(this.explodeFrame[currentFrame], this.getDrawCenter_x(),this.getDrawCenter_y(), null);//绘制子弹
        currentFrame++;
        if(StaticVariable.DEBUG) {
            Log.w(TAG, "explode draw");
        }
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public int getExplodeType() {
        return explodeType;
    }

    public void setExplodeType(int explodeType) {
        this.explodeType = explodeType;
    }

    public float getDrawCenter_x() {
        return explode_x-this.explodeFrame[currentFrame].getWidth()/2;
    }

    public float getDrawCenter_y() {
        return explode_y-this.explodeFrame[currentFrame].getHeight()/2;
    }
}

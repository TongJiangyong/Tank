package yong.tank.modal;

import android.graphics.Canvas;
import android.graphics.Paint;

import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/1.
 */

public class PlayerPain {
    private Paint paint = new Paint();
    private int outsideCircle_x = StaticVariable.LOCAL_SCREEN_WIDTH *4/5;
    private int outsideCircle_y = StaticVariable.LOCAL_SCREEN_HEIGHT *3/4;
    private int outsideCircle_r = StaticVariable.LOCAL_SCREEN_WIDTH *1/8;
    private float insideCircle_x = StaticVariable.LOCAL_SCREEN_WIDTH *4/5;
    private float insideCircle_y = StaticVariable.LOCAL_SCREEN_HEIGHT *3/4;
    private float insideCircle_r = StaticVariable.LOCAL_SCREEN_WIDTH *1/20;

    public PlayerPain() {
    }

    public void drawSelf(Canvas canvas){

        //设置透明度
        paint.setColor(0x70000000);
        //绘制摇杆背景
        canvas.drawCircle(outsideCircle_x, outsideCircle_y, outsideCircle_r, paint);
        paint.setColor(0x70ff0000);
        //绘制摇杆
        canvas.drawCircle(insideCircle_x, insideCircle_y,
                insideCircle_r, paint);
    }
    public boolean isInCircle(int x,int y){
        return ((outsideCircle_x-x)*(outsideCircle_x-x)+(outsideCircle_y-y)*(outsideCircle_y-y))<outsideCircle_r*outsideCircle_r;
    }

    public float getInsideCircle_x() {
        return insideCircle_x;
    }

    public void setInsideCircle_x(float insideCircle_x) {
        this.insideCircle_x = insideCircle_x;
    }

    public float getInsideCircle_y() {
        return insideCircle_y;
    }

    public void setInsideCircle_y(float insideCircle_y) {
        this.insideCircle_y = insideCircle_y;
    }

    public int setTankDirectiron(float insideCircle_x) {
        if(insideCircle_x>this.outsideCircle_x){
            return StaticVariable.MYTANKEFORWARD;
        }else if(insideCircle_x==this.outsideCircle_x){
            return StaticVariable.TANKESTOP;
        }else{
            return StaticVariable.MYTANKEBACK;
        }

    }
}

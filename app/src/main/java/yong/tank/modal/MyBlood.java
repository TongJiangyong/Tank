package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import yong.tank.modal.abstractGoup.Blood;
import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

/**
 * Created by hasee on 2016/11/1.
 */

public class MyBlood extends Blood{
    //blood相关的硬编码 硬编码已经调整，暂时不用管
    public  transient int bloodBlock_x= Tool.dip2px(StaticVariable.LOCAL_DENSITY,5);
    public transient int bloodBlock_y=Tool.dip2px(StaticVariable.LOCAL_DENSITY,5);
    public MyBlood(Bitmap blood, Bitmap power, Bitmap bloodBlock, float bloodNum, float powerNum) {
        super(blood, power, bloodBlock, bloodNum, powerNum);
    }


    public void positionUpdate(){
        //this.tankPosition_x=tankPrevPosition_x+((Tool.dip2px(StaticVariable.LOCAL_DENSITY, tankBascInfo.getSpeed())/15)*tankDirectrion);
        //this.tankPosition_x=tankPrevPosition_x+tankBascInfo.getIntervalSpeed()*tankDirectrion;
        //int intervalSpeed = (int)((float)StaticVariable.LOCAL_SCREEN_WIDTH /(float)(2*StaticVariable.LOGICAL_FRAME*tankBascInfo.getSpeed()/10));
        //Log.i(TAG,"intervalSpeed:"+tankBascInfo.getIntervalSpeed());
        //TODO 这里设置装填的时间需要2s
        if(powerNum<1){
            powerNum=powerNum+1/(float)(StaticVariable.LOGICAL_FRAME*StaticVariable.TANK_LOADING_TIME);
        }else{
            //设置运行发射
            this.allowFire = true;
        }
    }

    //这里配置一个其他的drawEnermy方法进行配置，可能会更好，或者将静态量提取出drawSelf出来
    public void drawSelf(Canvas canvas) {
        //计算血条的比例
        Rect src_blood = new Rect(0, 0, (int)(blood.getWidth()*bloodNum), blood.getHeight());
        Rect des_blood = new Rect(bloodBlock_x+Tool.dip2px(StaticVariable.LOCAL_DENSITY,56),
                bloodBlock_y+Tool.dip2px(StaticVariable.LOCAL_DENSITY,29),
                bloodBlock_x+Tool.dip2px(StaticVariable.LOCAL_DENSITY,56)+(int)(blood.getWidth()*bloodNum),
                bloodBlock_y+Tool.dip2px(StaticVariable.LOCAL_DENSITY,29)+blood.getHeight());
        //计算power的比例
        Rect src_power = new Rect(0, 0, (int)(power.getWidth()*powerNum), power.getHeight());
        Rect des_power = new Rect(bloodBlock_x+Tool.dip2px(StaticVariable.LOCAL_DENSITY,58),
                bloodBlock_y+Tool.dip2px(StaticVariable.LOCAL_DENSITY,41),
                bloodBlock_x+Tool.dip2px(StaticVariable.LOCAL_DENSITY,58)+(int)(power.getWidth()*powerNum),
                bloodBlock_y+Tool.dip2px(StaticVariable.LOCAL_DENSITY,41)+power.getHeight());
        //绘制装填进度条
        canvas.drawBitmap(power,src_power,des_power, null);
        //绘制框
        canvas.drawBitmap(bloodBlock,bloodBlock_x,bloodBlock_y, null);
        //绘制血条
        canvas.drawBitmap(blood, src_blood, des_blood, null);

    }

}

package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

/**
 * Created by hasee on 2016/11/1.
 */

public class Blood {
    private Bitmap blood;
    private Bitmap power;
    private Bitmap bloodBlock;
    //blood相关的硬编码 硬编码已经调整，暂时不用管
    private int bloodBlock_x= Tool.dip2px(StaticVariable.LOCAL_DENSITY,5);
    private int bloodBlock_y=Tool.dip2px(StaticVariable.LOCAL_DENSITY,5);
    private double bloodNum;
    private double powerNum;
    private Boolean allowFire = true;       //子弹装填时间使能 （时间开关）

    public Blood(Bitmap blood, Bitmap power, Bitmap bloodBlock, float bloodNum,float powerNum) {
        this.powerNum = powerNum;
        this.blood = blood;
        this.power = power;
        this.bloodBlock = bloodBlock;
        this.bloodNum = bloodNum;
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
        Rect des_power = new Rect(Tool.dip2px(StaticVariable.LOCAL_DENSITY,59),
                bloodBlock_y+Tool.dip2px(StaticVariable.LOCAL_DENSITY,41),
                bloodBlock_x+Tool.dip2px(StaticVariable.LOCAL_DENSITY,59)+(int)(power.getWidth()*powerNum),
                bloodBlock_y+Tool.dip2px(StaticVariable.LOCAL_DENSITY,41)+power.getHeight());
        //绘制装填进度条
        canvas.drawBitmap(power,src_power,des_power, null);
        //绘制框
        canvas.drawBitmap(bloodBlock,bloodBlock_x,bloodBlock_y, null);
        //绘制血条
        canvas.drawBitmap(blood, src_blood, des_blood, null);
        //TODO 这里设置装填的时间需要1s 硬编码
        if(powerNum<1){
            powerNum=powerNum+0.04;
        }else{
            //设置运行发射
            this.allowFire = true;
        }
    }

    public double getBloodNum() {
        return bloodNum;
    }

    public void setBloodNum(double bloodNum) {
        this.bloodNum = bloodNum;
    }

    public double getPowerNum() {
        return powerNum;
    }

    public void setPowerNum(double powerNum) {
        this.powerNum = powerNum;
    }

    public Boolean getAllowFire() {
        return allowFire;
    }

    public void setAllowFire(Boolean allowFire) {
        this.allowFire = allowFire;
    }

    public void subtractionBlood(double subtraction){
        bloodNum=bloodNum-subtraction;
    }

}

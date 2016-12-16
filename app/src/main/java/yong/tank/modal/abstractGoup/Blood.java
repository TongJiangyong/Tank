package yong.tank.modal.abstractGoup;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

/**
 * Created by hasee on 2016/11/1.
 */

public abstract  class Blood {
    public Bitmap blood;
    public Bitmap power;
    public Bitmap bloodBlock;
    //blood相关的硬编码 硬编码已经调整，暂时不用管
    public int bloodBlock_x= Tool.dip2px(StaticVariable.LOCAL_DENSITY,5);
    public int bloodBlock_y=Tool.dip2px(StaticVariable.LOCAL_DENSITY,5);
    public double bloodNum;
    public double powerNum;
    public Boolean allowFire = true;       //子弹装填时间使能 （时间开关）

    public Blood(Bitmap blood, Bitmap power, Bitmap bloodBlock, float bloodNum, float powerNum) {
        this.powerNum = powerNum;
        this.blood = blood;
        this.power = power;
        this.bloodBlock = bloodBlock;
        this.bloodNum = bloodNum;
    }

    //这里配置一个其他的drawEnermy方法进行配置，可能会更好，或者将静态量提取出drawSelf出来
    public abstract void  drawSelf(Canvas canvas);

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

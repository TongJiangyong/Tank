package yong.tank.modal.abstractGoup;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.io.Serializable;

/**
 * Created by hasee on 2016/11/1.
 */

public abstract  class Blood implements Serializable {
    public transient Bitmap blood;
    public transient Bitmap power;
    public transient Bitmap bloodBlock;
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

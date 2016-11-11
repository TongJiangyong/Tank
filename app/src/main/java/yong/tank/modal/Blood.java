package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by hasee on 2016/11/1.
 */

public class Blood {
    private Bitmap blood;
    private Bitmap power;
    private Bitmap bloodBlock;
    private int bloodNum;
    private int powerNum;
    private Boolean allowFire = true;       //子弹装填时间使能 （时间开关）

    public Blood(Bitmap blood, Bitmap power, Bitmap bloodBlock, int bloodNum,int powerNum) {
        this.powerNum = powerNum;
        this.blood = blood;
        this.power = power;
        this.bloodBlock = bloodBlock;
        this.bloodNum = bloodNum;
    }

    public void drawSelf(Canvas canvas) {
        //绘制框

        //绘制血条

        //绘制子弹条
    }

    public int getBloodNum() {
        return bloodNum;
    }

    public void setBloodNum(int bloodNum) {
        this.bloodNum = bloodNum;
    }

    public int getPowerNum() {
        return powerNum;
    }

    public void setPowerNum(int powerNum) {
        this.powerNum = powerNum;
    }


    public Boolean getAllowFire() {
        return allowFire;
    }

    public void setAllowFire(Boolean allowFire) {
        this.allowFire = allowFire;
    }


}

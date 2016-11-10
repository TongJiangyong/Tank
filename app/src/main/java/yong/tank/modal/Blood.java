package yong.tank.modal;

import android.graphics.Bitmap;

/**
 * Created by hasee on 2016/11/1.
 */

public class Blood {
    private Bitmap blood;
    private Bitmap power;
    private Bitmap bloodBlock;
    private int bloodNum;
    private int powerNum;
    private Boolean enableFire = false;     //允许发射使能（总开关）
    private Boolean allowFire = false;       //子弹装填时间使能 （时间开关）

    public Blood(Bitmap blood, Bitmap power, Bitmap bloodBlock, int bloodNum,int powerNum) {
        this.powerNum = powerNum;
        this.blood = blood;
        this.power = power;
        this.bloodBlock = bloodBlock;
        this.bloodNum = bloodNum;
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

    public Boolean getEnableFire() {
        return enableFire;
    }

    public void setEnableFire(Boolean enableFire) {
        this.enableFire = enableFire;
    }

    public Boolean getAllowFire() {
        return allowFire;
    }

    public void setAllowFire(Boolean allowFire) {
        this.allowFire = allowFire;
    }
}

package yong.tank.modal;

import android.graphics.Bitmap;

/**
 * Created by hasee on 2016/11/1.
 */

public class Blood {
    private Bitmap blood;
    private Bitmap power;
    private Bitmap bloodBlock;
    private int blood_num;
    private int power_num;

    public Blood(Bitmap blood, Bitmap power, Bitmap bloodBlock, int blood_num, int power_num) {
        this.blood = blood;
        this.power = power;
        this.bloodBlock = bloodBlock;
        this.blood_num = blood_num;
        this.power_num = power_num;
    }


    public int getBlood_num() {
        return blood_num;
    }

    public void setBlood_num(int blood_num) {
        this.blood_num = blood_num;
    }

    public int getPower_num() {
        return power_num;
    }

    public void setPower_num(int power_num) {
        this.power_num = power_num;
    }
}

package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by hasee on 2016/11/1.
 */

public class Blood {
    private Bitmap blood;
    private Bitmap power;
    private Bitmap bloodBlock;
    private int bloodBlock_x=20;
    private int bloodBlock_y=20;
    private float bloodNum;
    private float powerNum;
    private Boolean allowFire = true;       //子弹装填时间使能 （时间开关）

    public Blood(Bitmap blood, Bitmap power, Bitmap bloodBlock, float bloodNum,float powerNum) {
        this.powerNum = powerNum;
        this.blood = blood;
        this.power = power;
        this.bloodBlock = bloodBlock;
        this.bloodNum = bloodNum;
    }

    public void drawSelf(Canvas canvas) {
        //计算血条的比例
        Rect src_blood = new Rect(0, 0, (int)(blood.getWidth()*bloodNum), blood.getHeight());
        Rect des_blood = new Rect(bloodBlock_x+170,
                bloodBlock_y+87,
                bloodBlock_x+170+(int)(blood.getWidth()*bloodNum),
                bloodBlock_y+87+blood.getHeight());
        //计算power的比例
        Rect src_power = new Rect(0, 0, (int)(power.getWidth()*powerNum), power.getHeight());
        Rect des_power = new Rect(bloodBlock_x+173,
                bloodBlock_y+125,
                bloodBlock_x+173+(int)(power.getWidth()*powerNum),
                bloodBlock_y+125+power.getHeight());

        //绘制框
        canvas.drawBitmap(bloodBlock,bloodBlock_x,bloodBlock_y, null);
        //绘制血条
        canvas.drawBitmap(blood, src_blood, des_blood, null);
        //绘制子弹进度条
        canvas.drawBitmap(power,src_power,des_power, null);
    }

    public float getBloodNum() {
        return bloodNum;
    }

    public void setBloodNum(float bloodNum) {
        this.bloodNum = bloodNum;
    }

    public float getPowerNum() {
        return powerNum;
    }

    public void setPowerNum(float powerNum) {
        this.powerNum = powerNum;
    }

    public Boolean getAllowFire() {
        return allowFire;
    }

    public void setAllowFire(Boolean allowFire) {
        this.allowFire = allowFire;
    }


}

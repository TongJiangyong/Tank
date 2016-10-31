package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by hasee on 2016/10/27.
 */

public class MyTank implements Tank{
    private Bitmap tankPicture;
    private int tankType;
    private TankBascInfo tankBascInfo;


    public MyTank(Bitmap tankPicture, int tankType, TankBascInfo tankBascInfo) {
        this.tankPicture = tankPicture;
        this.tankType = tankType;
        this.tankBascInfo = tankBascInfo;
    }

    public void drawSelf(Canvas canvas){

        canvas.drawBitmap(this.getTankPicture(),200,200,null);
    }


    public Bitmap getTankPicture() {
        return tankPicture;
    }

    public void setTankPicture(Bitmap tankPicture) {
        this.tankPicture = tankPicture;
    }

    public int getTankType() {
        return tankType;
    }

    public void setTankType(int tankType) {
        this.tankType = tankType;
    }

    public TankBascInfo getTankBascInfo() {
        return tankBascInfo;
    }

    public void setTankBascInfo(TankBascInfo tankBascInfo) {
        this.tankBascInfo = tankBascInfo;
    }
}

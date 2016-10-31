package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

/**
 * Created by hasee on 2016/10/27.
 */

public class MyTank implements Tank{
    private Bitmap tankPicture;
    private Bitmap armPicture;
    private int tankType;
    private TankBascInfo tankBascInfo;
    private int tankPosition_x = 0;
    private int tankPosition_y =0;
    private int armDegree=0; //armpicture为内置的.....
    public MyTank(Bitmap tankPicture,Bitmap armPicture, int tankType, TankBascInfo tankBascInfo) {
        this.tankPicture = tankPicture;
        this.tankType = tankType;
        this.tankBascInfo = tankBascInfo;
        this.armPicture=armPicture;
        this.tankPosition_x=StaticVariable.SCREEN_WIDTH/4-this.tankPicture.getWidth()/2;
        this.tankPosition_y=StaticVariable.SCREEN_HEIGHT*3/4;
    }

    public void drawSelf(Canvas canvas){

        canvas.drawBitmap(this.tankPicture,this.tankPosition_x,this.tankPosition_y,null);
        Bitmap armPicture_tmp = Tool.reBuildImg(this.getArmPicture(),this.armDegree,(float)1.5,(float)1.5,false,false);

        canvas.drawBitmap(armPicture_tmp,
                this.tankPosition_x+110,
                this.tankPosition_y+27,
                null);
    }


    public Bitmap getTankPicture() {
        return tankPicture;
    }

    public Bitmap getArmPicture() {
        return armPicture;
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

    public int getTankPosition_x() {
        return tankPosition_x;
    }

    public void setTankPosition_x(int tankPosition_x) {
        this.tankPosition_x = tankPosition_x;
    }

    public int getTankPosition_y() {
        return tankPosition_y;
    }

    public void setTankPosition_y(int tankPosition_y) {
        this.tankPosition_y = tankPosition_y;
    }

    public int getArmDegree() {
        return armDegree;
    }

    public void setArmDegree(int armDegree) {
        this.armDegree = armDegree;
    }
}

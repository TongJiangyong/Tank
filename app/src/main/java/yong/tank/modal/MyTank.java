package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

import static android.R.transition.move;
import static yong.tank.R.mipmap.speed;

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
    private int tankSpeed=5;
    private int tankDirectrion=0;
    private static String TAG = "MyTank";
    private int armDegree=-10; //armpicture为内置的.....
    public MyTank(Bitmap tankPicture,Bitmap armPicture, int tankType, TankBascInfo tankBascInfo) {
        this.tankPicture = tankPicture;
        this.tankType = tankType;
        this.tankBascInfo = tankBascInfo;
        this.armPicture=armPicture;
        this.tankPosition_x=StaticVariable.SCREEN_WIDTH/4-this.tankPicture.getWidth()/2;
        this.tankPosition_y=StaticVariable.SCREEN_HEIGHT*3/4;
    }

    public void drawSelf(Canvas canvas){

        if((tankPosition_x<0)||(tankPosition_x+this.tankPicture.getWidth()>StaticVariable.SCREEN_WIDTH*3/7)){
            //TODO 处理移动....
        }else{
            this.tankPosition_x+=(tankSpeed*tankDirectrion);
        }
        Log.w(TAG,"tank position:"+this.tankPosition_x);
        canvas.drawBitmap(this.tankPicture,this.tankPosition_x,this.tankPosition_y,null);
        Bitmap armPicture_tmp = Tool.reBuildImg(this.getArmPicture(),this.armDegree,1,1,false,false);
        //Bitmap armPicture_tmp_2 = Tool.reBuildImg(armPicture_tmp,0,1,1,true,false);

        canvas.drawBitmap(armPicture_tmp,
                this.tankPosition_x+110,
                //0,0,
                //注意这种角度的变化方法.....一定要加上图片本身的宽度....
                this.tankPosition_y-armPicture_tmp.getHeight()+65,
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

    public boolean isInCircle(int x,int y){
        if(this.tankPosition_x<x&&
                x<(this.tankPosition_x+this.tankPicture.getWidth())&&
                this.tankPosition_y<y&&
                y<(this.tankPosition_y+this.tankPicture.getHeight())){
            return true;
        }else{
            return false;
        }
    }

    public void move(int tankDirection) {
        this.tankDirectrion=tankDirection;
    }
}

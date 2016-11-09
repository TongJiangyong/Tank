package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
    private int tankSpeed=5;
    private int tankDirectrion=0;
    private Point tankCenter = new Point();
    private static String TAG = "MyTank";
    private int armDegree=-10; //armpicture为内置的.....
    private List<Bullet> bullets;
    public MyTank(Bitmap tankPicture,Bitmap armPicture, int tankType, TankBascInfo tankBascInfo) {
        this.tankPicture = tankPicture;
        this.tankType = tankType;
        this.tankBascInfo = tankBascInfo;
        this.armPicture=armPicture;
        this.tankPosition_x=StaticVariable.SCREEN_WIDTH/4-this.tankPicture.getWidth()/2;
        //this.tankPosition_y=StaticVariable.SCREEN_HEIGHT*3/4;
        //这是测试用的tank位置
        this.tankPosition_y=StaticVariable.SCREEN_HEIGHT*3/5;
        bullets = new ArrayList<>(5);//暂时定子弹数为5？
    }

    public void drawSelf(Canvas canvas){

        //TODO 考虑策略模式优化
        if(tankPosition_x<0){
            this.tankPosition_x=0;
        }else if((tankPosition_x+this.tankPicture.getWidth())>StaticVariable.SCREEN_WIDTH*3/7){
            this.tankPosition_x=(StaticVariable.SCREEN_WIDTH*3/7-this.tankPicture.getWidth());
        }else{
            this.tankPosition_x+=(tankSpeed*tankDirectrion);
        }
        //Log.w(TAG,"tank position:"+this.tankPosition_x);
        canvas.drawBitmap(this.tankPicture,this.tankPosition_x,this.tankPosition_y,null);
        Bitmap armPicture_tmp = Tool.reBuildImg(this.getArmPicture(),this.armDegree,1,1,false,false);
        //Bitmap armPicture_tmp_2 = Tool.reBuildImg(armPicture_tmp,0,1,1,true,false);

        canvas.drawBitmap(armPicture_tmp,
                this.tankPosition_x+110,
                //0,0,
                //注意这种角度的变化方法.....一定要加上图片本身的宽度....
                this.tankPosition_y-armPicture_tmp.getHeight()+65,
                null);


        //测试绘制一个圆环：
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(0x80000000);
        canvas.drawCircle(this.tankCenter.getX(),this.tankCenter.getY(), (int)(this.getTankPicture().getWidth()*1.4),paint);
        paint.setColor(0x60000000);
        int test=(int)(this.getTankPicture().getWidth()*1.5);
        canvas.drawCircle(this.tankCenter.getX(),this.tankCenter.getY(), test, paint);
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

    public boolean isInFireCircle(int x, int y) {
        int distance = (x-this.getTankCenter().getX())*(x-this.getTankCenter().getX())+(y-this.getTankCenter().getY())*(y-this.getTankCenter().getY());
        int distance_scope=this.getTankPicture().getWidth()*this.getTankPicture().getWidth();
        if(distance>0&&distance<distance_scope*2){
            return true;
        }else{
            return false;
        }
    }
    //这是一个释放的范围...
    public boolean isOutFireCircle(int x, int y) {
        int distance = (x-this.getTankCenter().getX())*(x-this.getTankCenter().getX())+(y-this.getTankCenter().getY())*(y-this.getTankCenter().getY());
        int distance_scope=this.getTankPicture().getWidth()*this.getTankPicture().getWidth();
        if(distance>distance_scope*2&&distance<distance_scope*2.5){
            return true;
        }else{
            return false;
        }
    }

    public Point getTankCenter() {
        this.tankCenter.setX(this.getTankPosition_x()+this.tankPicture.getWidth()/2);
        this.tankCenter.setY(this.getTankPosition_y()+this.tankPicture.getHeight()/2);
        return tankCenter;
    }

    public void weaponMove(int tankDegree) {
        this.armDegree=tankDegree;
    }

    public void bulletFire(int tankDegree, int distance) {
        //TODO 计算子弹的发射路劲......
        //具体子弹的绘制在子弹中，但是在tank的绘制中，对其进行调用即可.....
        Log.w(TAG,"FIRE IN TANK");
    }

}

package yong.tank.modal.abstractGoup;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.Serializable;
import java.util.List;

import yong.tank.modal.Point;
import yong.tank.modal.TankBascInfo;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/10/27.
 */

public abstract class Tank implements Serializable{
    public transient Bitmap tankPicture;
    private transient Bitmap armPicture;
    private int tankType;
    public transient TankBascInfo tankBascInfo;
    public int tankPosition_x = 0;
    public int tankPosition_y =0;
    public int tankDirectrion=0;
    private double firePower = 0; //发射的距离角度
    private Point tankCenter = new Point();
    private transient static String TAG = "Tank";
    public int weaponDegree =-10; //armpicture为内置的.....
    public int weaponPoxition_x =0;
    public int weaponPoxition_y =0;
    private transient Boolean enableFire = false;     //允许发射使能（总开关）
    private transient Boolean fireAction = false;      //tank发射动作使能 （动作开关）
    //预发射路径点
    public transient List<Point> preFirePath;
    //已经发射的子弹，严格控制子弹加入

    //tank所拥有的子弹种类
    /**  类型   数量
     *  0       100000
     *  1        10
     *  2        10
     *  3        10
     *  4        10
     *  暂时只允许坦克只拥有两种子弹
     * **/
    //坦克的当前发射子弹类型
    private transient int selectedBullets= StaticVariable.ORIGIN;
    //坦克的当前发射子弹数量
    private transient int selectedBulletsNum = StaticVariable.TANK_BULLET_YPTE[0][1];

    //坦克的当前所拥有的发射类型和该类型的子弹数量
    public Tank(Bitmap tankPicture,Bitmap armPicture, int tankType, TankBascInfo tankBascInfo) {
            this.tankPicture = tankPicture;
            this.tankType = tankType;
            this.tankBascInfo = tankBascInfo;
            this.armPicture=armPicture;
            //Log.w(TAG,"current bullet num:"+selectedBulletsNum);
            }

    //TODO  modify this method
    public abstract void drawSelf(Canvas canvas);
    //绘制prefire的圆环：
    public void drawPreFireCircle(Canvas canvas) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            paint.setColor(0x80000000);
            canvas.drawCircle(this.getTankCenter().getX(),this.getTankCenter().getY(), (int)(this.getTankPicture().getWidth()*1.4),paint);
            //Log.w(TAG,"distance :"+this.getTankPicture().getWidth()*1.4);
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

    public int getWeaponDegree() {
            return weaponDegree;
            }

    public void setWeaponDegree(int weaponDegree) {
            this.weaponDegree = weaponDegree;
            }



    public int getSelectedBullets() {
            return selectedBullets;
            }

    public void setSelectedBullets(int selectedBullets) {
            this.selectedBullets = selectedBullets;
            }

    public Boolean getEnableFire() {
            return enableFire;
            }

    public void setEnableFire(Boolean enableFire) {
            this.enableFire = enableFire;
            }

    public Boolean getFireAction() {
            return fireAction;
            }

    public void setFireAction(Boolean fireAction) {
            this.fireAction = fireAction;
            }

    public int getWeaponPoxition_x() {
            return weaponPoxition_x;
            }


    public int getWeaponPoxition_y() {
            return weaponPoxition_y;
            }


    public boolean isInCircle(int x, int y){
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
            //TODO 这里需要将tank的distance设为一个定值
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
            this.weaponDegree = -tankDegree;
            }


    public List<Point> getPreFirePath() {
            return preFirePath;
            }

    public void setPreFirePath(List<Point> preFirePath) {
            this.preFirePath = preFirePath;
            }


    public int getSelectedBulletsNum() {
            return selectedBulletsNum;
            }

    public void setSelectedBulletsNum(int selectedBulletsNum) {
            this.selectedBulletsNum = selectedBulletsNum;
            }

    public double getFirePower() {
        return firePower;
    }

    public void setFirePower(double firePower) {
        this.firePower = firePower;
    }

    //x，y的坐标在第3象限外
    public boolean isOutDirection(int dx, int dy) {
            if(dx<=this.getTankCenter().getX()&&dy>=this.getTankCenter().getY()){
            return true;
            }else{
            return false;
            }
            }
    public  abstract void addBuleetFire(Bullet bullet);
}

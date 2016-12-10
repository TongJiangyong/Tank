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
    private int tankDirectrion=0;
    private Point tankCenter = new Point();
    private static String TAG = "MyTank";
    private int weaponDegree =-10; //armpicture为内置的.....
    private int weaponPoxition_x =0;
    private int weaponPoxition_y =0;
    private Boolean enableFire = false;     //允许发射使能（总开关）
    private Boolean fireAction = false;      //tank发射动作使能 （动作开关）
    //预发射路径点
    private List<Point> preFirePath;
    //已经发射的子弹，严格控制子弹加入
    private List<Bullet> bulletsFire = new ArrayList<>(3);//暂时发送子弹数为3？但是这样做好像没用.....
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
    private int selectedBullets=StaticVariable.ORIGIN;
    //坦克的当前发射子弹数量
    private int selectedBulletsNum = StaticVariable.TANK_BULLET_YPTE[0][1];

    //坦克的当前所拥有的发射类型和该类型的子弹数量
    public MyTank(Bitmap tankPicture,Bitmap armPicture, int tankType, TankBascInfo tankBascInfo) {
        this.tankPicture = tankPicture;
        this.tankType = tankType;
        this.tankBascInfo = tankBascInfo;
        this.armPicture=armPicture;
        this.tankPosition_x=StaticVariable.LOCAL_SCREEN_WIDTH /4-this.tankPicture.getWidth()/2;
        //this.tankPosition_y=StaticVariable.LOCAL_SCREEN_HEIGHT*3/4;
        //这是测试用的tank位置
        this.tankPosition_y=StaticVariable.LOCAL_SCREEN_HEIGHT *2/3;
        Log.w(TAG,"current bullet num:"+selectedBulletsNum);
    }

    public void drawSelf(Canvas canvas){

        //TODO 考虑策略模式优化
        if(tankPosition_x<0){
            this.tankPosition_x=0;
        }else if((tankPosition_x+this.tankPicture.getWidth())>StaticVariable.LOCAL_SCREEN_WIDTH *3/7){
            this.tankPosition_x=(StaticVariable.LOCAL_SCREEN_WIDTH *3/7-this.tankPicture.getWidth());
        }else{
            //由于这里有余数，所以会让分辨率产生损失.....感觉这样没办法处理.....
            //Log.i(TAG,"move:"+(Tool.dip2px(StaticVariable.LOCAL_DENSITY, tankBascInfo.getSpeed())));
            this.tankPosition_x+=((Tool.dip2px(StaticVariable.LOCAL_DENSITY, tankBascInfo.getSpeed())/15)*tankDirectrion);
        }
        canvas.drawBitmap(this.tankPicture,this.tankPosition_x,this.tankPosition_y,null);
        Bitmap armPicture_tmp = Tool.reBuildImg(this.getArmPicture(),this.weaponDegree,1,1,false,false);
        //Bitmap armPicture_tmp_2 = Tool.reBuildImg(armPicture_tmp,0,1,1,true,false);
        //TODO 注意这两种方法，硬编码可以让不同分辨率的屏幕适应......
//        Log.i(TAG,"width:"+Tool.dip2px(StaticVariable.LOCAL_DENSITY, 22));
        int weaponPoxitionTemp_x = this.tankPosition_x+tankPicture.getWidth()*2/5;
        int weaponPoxitionTemp_y = this.tankPosition_y-armPicture_tmp.getHeight()+Tool.dip2px(StaticVariable.LOCAL_DENSITY, 22);
        canvas.drawBitmap(armPicture_tmp,
                weaponPoxitionTemp_x,
                //0,0,
                //注意这种角度的变化方法.....一定要加上图片本身的宽度....
                weaponPoxitionTemp_y,
                null);
        //坦克画在arm的后面

        // TODO 这里weapon的点要更精细一点
        this.weaponPoxition_x = weaponPoxitionTemp_x+armPicture_tmp.getWidth();
        this.weaponPoxition_y = weaponPoxitionTemp_y;

        //TODO 绘制预发射的子弹路径 绘制点的方法即可
        if(preFirePath!=null){
            Paint preFirePathPaint = new Paint();
            preFirePathPaint.setStrokeWidth(5);
            for(int i=0;i<preFirePath.size();i++)
            {
                canvas.drawPoint(preFirePath.get(i).getX(), preFirePath.get(i).getY(), preFirePathPaint);
            }
            this.drawPreFireCircle(canvas);
        }

        //绘制所有的子弹
        if(bulletsFire.size()==0){
        }else{
            //注意多线程的处理
            for (int i = bulletsFire.size() -1; i >= 0; i--)
            {
                //绘制子弹
                bulletsFire.get(i).drawSelf(canvas);
            }
        }
    }
    //绘制prefire的圆环：
    private void drawPreFireCircle(Canvas canvas) {
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

    public List<Bullet> getBulletsFire() {
        return bulletsFire;
    }

    public void setBulletsFire(List<Bullet> bulletsFire) {
        this.bulletsFire = bulletsFire;
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

    public void addBuleetFire(Bullet bullet) {
        this.getBulletsFire().add(bullet);
    }

    public int getSelectedBulletsNum() {
        return selectedBulletsNum;
    }

    public void setSelectedBulletsNum(int selectedBulletsNum) {
        this.selectedBulletsNum = selectedBulletsNum;
    }

    //x，y的坐标在第3象限外
    public boolean isOutDirection(int dx, int dy) {
        if(dx<=this.getTankCenter().getX()&&dy>=this.getTankCenter().getY()){
            return true;
        }else{
            return false;
        }
    }
}

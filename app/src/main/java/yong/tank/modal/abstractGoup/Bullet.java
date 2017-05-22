package yong.tank.modal.abstractGoup;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.util.Log;

import java.io.Serializable;
import java.util.List;

import yong.tank.modal.BulletBascInfo;
import yong.tank.modal.Point;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/8.
 */

public abstract class Bullet implements Serializable{
    private  transient BulletBascInfo bulletBascInfo;
    public transient  Bitmap bulletPicture;
    private int bulletType;
    //发射路径点
    public transient List<Point> firePath;
    public float bulletPosition_x;
    public float bulletPosition_y;
    //用来决定距离的....
    public double bulletDistance;
    public int bulletDegree; //armpicture为内置的.....
    private transient static String TAG = "Bullet";
    public transient Matrix matrix = new Matrix(); // 预备用作旋转的类
    public boolean drawFlag=false;
    public int pathPosition = 0;  //当前子弹位于的position 绘制敌方子弹主要的变量
    //测试绘制路径
    private transient Path path = new Path();
    public double bulletV_x=0;
    public double bulletV_y=0;
    public Bullet(Bitmap bulletPicture, int bulletType) {
        this.bulletPicture = bulletPicture;
        this.bulletType = bulletType;
        this.bulletBascInfo = StaticVariable.BUTTLE_BASCINFOS[bulletType];
    }

    public void  positionUpdate() {
        //这里关联speed和distance，暂时不处理
        //this.gameDto.getMyTank().getSelectedBullets()
        //这里计算时，采用向下为正，向右为正的方法
        //这里指示的是每一帧的内容

        bulletV_y = bulletV_y + StaticVariable.GRAVITY/StaticVariable.LOGICAL_FRAME;
        float newPosition_x = (bulletPosition_x + (float)bulletV_x/StaticVariable.LOGICAL_FRAME );
        //bulletPosition_x+=v_x*t;
        float newPosition_y = (bulletPosition_y + (float)(bulletV_y/StaticVariable.LOGICAL_FRAME )+ (float)(StaticVariable.GRAVITY /(2*StaticVariable.LOGICAL_FRAME*StaticVariable.LOGICAL_FRAME)));
        //bulletPosition_y+=v_y*t-g*t*t/2;
        bulletDegree = (int) Math.toDegrees(Math.atan(bulletV_y / bulletV_x));
        //System.out.println( "bulletV_x:" + init_x + " bulletV_y:" + init_y);
        bulletPosition_x=newPosition_x;
        bulletPosition_y=newPosition_y;
        Log.w(TAG, "isInCircle**************bulletDegree:" + bulletDegree + " bulletV_y:" + bulletV_y + " bulletV_x:" + bulletV_x );
        Log.w(TAG, "isInCircle bulletDegree:" + bulletDegree + "bulletDistance:" + bulletDistance + " bulletPosition_x:" + bulletPosition_x + " bulletPosition_y:" + bulletPosition_y);
        //time = time + StaticVariable.INTERVAL;
    }


    public Bitmap getBulletPicture() {
        return bulletPicture;
    }

    public void setBulletPicture(Bitmap bulletPicture) {
        this.bulletPicture = bulletPicture;
    }


    public float getBulletPosition_x() {
        return bulletPosition_x;
    }

    public void setBulletPosition_x(int bulletPosition_x) {
        this.bulletPosition_x = bulletPosition_x;
    }

    public List<Point> getFirePath() {
        return firePath;
    }

    public void setFirePath(List<Point> firePath) {
        this.firePath = firePath;
    }

    public boolean isDrawFlag() {
        return drawFlag;
    }

    public void setDrawFlag(boolean drawFlag) {
        this.drawFlag = drawFlag;
    }

    public float getBulletPosition_y() {
        return bulletPosition_y;
    }

    public void setBulletPosition_y(int bulletPosition_y) {
        this.bulletPosition_y = bulletPosition_y;
        //设置轨迹起点
        path.moveTo(this.bulletPosition_x, this.bulletPosition_y);
        bulletV_x=bulletBascInfo.getSpeed()*bulletDistance*Math.cos(Math.toRadians(bulletDegree));
        bulletV_y=-(bulletBascInfo.getSpeed()*bulletDistance*Math.sin(Math.toRadians(bulletDegree)));
    }

    public void drawSelf(Canvas canvas){
        //如果是本地模式，则，按照路径数据进行绘制
        //Log.w(TAG, "drawSelf*******************");
        //if(StaticVariable.CHOSED_MODE== StaticVariable.GAME_MODE.LOCAL){
        //这样的处理是为了让物理上的视觉效果更好
        if(drawFlag&&bulletPosition_y<StaticVariable.GAMME_GROUND_POSITION){
            matrix.setTranslate(bulletPosition_x, bulletPosition_y);//子弹坐标
            //注意这里，给的角度为负数
            matrix.postRotate(bulletDegree,bulletPosition_x,bulletPosition_y);//子弹的旋转
            canvas.drawBitmap(this.bulletPicture, matrix, null);//绘制子弹
            //Log.i(TAG, "X:" + bulletPosition_x+" Y:" + bulletPosition_y  + " Degree:" + bulletDegree);
        }
        //如果不是本地模式，则按照位置进行绘制
/*        }else{
            //如果不是本地模式，则，按坐标
            if(drawFlag) {
                matrix.setTranslate(bulletPosition_x, bulletPosition_y);//子弹坐标
                matrix.postRotate(-bulletDegree, bulletPosition_x, bulletPosition_y);
                canvas.drawBitmap(this.bulletPicture, matrix, null);//绘制子弹
            }
        }*/
        if(bulletPosition_x> StaticVariable.LOCAL_SCREEN_WIDTH ||
                bulletPosition_y>StaticVariable.LOCAL_SCREEN_HEIGHT ||
                bulletPosition_x<0){
            this.setDrawFlag(false);
        }

    }

    public int getBulletDegree() {
        return bulletDegree;
    }

    public void setBulletDegree(int bulletDegree) {
        this.bulletDegree = bulletDegree;
    }

    public double getBulletDistance() {
        return bulletDistance;
    }

    public void setBulletDistance(double bulletDistance) {
        this.bulletDistance = bulletDistance;
    }

    public int getPathPosition() {
        return pathPosition;
    }

    public void setPathPosition(int pathPosition) {
        this.pathPosition = pathPosition;
    }

    public BulletBascInfo getBulletBascInfo() {
        return bulletBascInfo;
    }

    public int getBulletType() {
        return bulletType;
    }

    public void setBulletType(int bulletType) {
        this.bulletType = bulletType;
    }
}

package yong.tank.modal.abstractGoup;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;

import java.io.Serializable;
import java.util.List;

import yong.tank.modal.BulletBascInfo;
import yong.tank.modal.Point;

/**
 * Created by hasee on 2016/11/8.
 */

public abstract class Bullet implements Serializable{
    private  transient BulletBascInfo bulletBascInfo;
    public transient  Bitmap bulletPicture;
    //发射路径点
    public transient List<Point> firePath;
    public int bulletPosition_x;
    public int bulletPosition_y;
    //用来决定距离的....
    public double bulletDistance;
    public int bulletDegree; //armpicture为内置的.....
    private transient static String TAG = "Bullet";
    public transient Matrix matrix = new Matrix(); // 预备用作旋转的类
    public boolean drawFlag=false;
    public int pathPosition = 0;  //当前子弹位于的position 绘制敌方子弹主要的变量
    //测试绘制路径
    private transient Path path = new Path();
    double bulletV_x=0;
    double bulletV_y=0;
    public Bullet(Bitmap bulletPicture, BulletBascInfo bulletBascInfo) {
        this.bulletPicture = bulletPicture;
        this.bulletBascInfo = bulletBascInfo;
    }

    public abstract  void drawSelf(Canvas canvas);

    public Bitmap getBulletPicture() {
        return bulletPicture;
    }

    public void setBulletPicture(Bitmap bulletPicture) {
        this.bulletPicture = bulletPicture;
    }


    public int getBulletPosition_x() {
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

    public int getBulletPosition_y() {
        return bulletPosition_y;
    }

    public void setBulletPosition_y(int bulletPosition_y) {
        this.bulletPosition_y = bulletPosition_y;
        //设置轨迹起点
        path.moveTo(this.bulletPosition_x, this.bulletPosition_y);
        bulletV_x=bulletBascInfo.getSpeed()*bulletDistance*Math.cos(Math.toRadians(bulletDegree));
        bulletV_y=-(bulletBascInfo.getSpeed()*bulletDistance*Math.sin(Math.toRadians(bulletDegree)));
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
}

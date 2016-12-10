package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;

import java.util.List;

import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/8.
 */

public class Bullet {
    private  BulletBascInfo bulletBascInfo;
    private int bulletPosition_x;
    private int bulletPosition_y;
    //用来决定距离的....
    private double bulletDistance;
    private int bulletDegree; //armpicture为内置的.....
    private Bitmap bulletPicture;
    private static String TAG = "Bullet";
    private Matrix matrix = new Matrix(); // 预备用作旋转的类
    private boolean drawFlag=false;
    private int pathPosition = 0;  //当前子弹位于的position 绘制敌方子弹主要的变量
    //测试绘制路径
    private Path path = new Path();
    //发射路径点
    private List<Point> firePath;
    double bulletV_x=0;
    double bulletV_y=0;
    public Bullet(Bitmap bulletPicture,BulletBascInfo bulletBascInfo) {
        this.bulletPicture = bulletPicture;
        this.bulletBascInfo = bulletBascInfo;
    }

    public void drawSelf(Canvas canvas){
        //TODO 绘制子弹 如果路径存在
        if(firePath!=null&&drawFlag&&pathPosition<firePath.size()){
            //for(int i =0;i<firePath.size();i++){
            matrix.setTranslate(firePath.get(pathPosition).getX(), firePath.get(pathPosition).getY());//子弹坐标
            matrix.postRotate(firePath.get(pathPosition).getDegree(),firePath.get(pathPosition).getX(),firePath.get(pathPosition).getY());//子弹的旋转
            bulletPosition_x=firePath.get(pathPosition).getX();
            bulletPosition_y=firePath.get(pathPosition).getY();
            canvas.drawBitmap(this.bulletPicture, matrix, null);//绘制子弹
            //Log.w(TAG, "X:" + firePath.get(pathPosition).getX() +" Y:" + firePath.get(pathPosition).getY() + " Degree:" + firePath.get(pathPosition).getDegree());
            pathPosition++;
            //}
        }
        //TODO 计算位置
/*        bulletPosition();
        Paint paint = new Paint();
        paint.setColor(Color.RED);// 设置红色
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawPath(path, paint);*/
        if(bulletPosition_x> StaticVariable.LOCAL_SCREEN_WIDTH ||
                bulletPosition_y>StaticVariable.LOCAL_SCREEN_HEIGHT ||
                bulletPosition_x<0){
            this.setDrawFlag(false);
        }
    }

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

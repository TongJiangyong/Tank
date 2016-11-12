package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.util.Log;

import java.util.List;

import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/8.
 */

public class Bullet {
    //public BulletBascInfo(int type, int speed, int power, int picture, String bulletName)
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
        //Log.w(TAG,"draw Bullet");
        //matrix.setTranslate(bulletPosition_x, bulletPosition_x);//坐标
        //matrix.postRotate(angle, x, y+6);//设置旋转角度 以及旋转中
        //canvas.drawBitmap(this.bulletPicture, matrix, null);//炮筒
        //TODO 绘制子弹 如果路径存在
        if(firePath!=null&&drawFlag){
            for(int i =0;i<firePath.size();i++){
                matrix.setTranslate(firePath.get(i).getX(), firePath.get(i).getY());//子弹坐标
                matrix.postRotate(firePath.get(i).getDegree());//子弹的旋转
                bulletPosition_x=firePath.get(i).getX();
                bulletPosition_y=firePath.get(i).getY();
                canvas.drawBitmap(this.bulletPicture, matrix, null);//绘制子弹
            }

        }
        //TODO 计算位置
/*        bulletPosition();
        Paint paint = new Paint();
        paint.setColor(Color.RED);// 设置红色
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawPath(path, paint);*/
        if(bulletPosition_x> StaticVariable.SCREEN_WIDTH||
                bulletPosition_y>StaticVariable.SCREEN_HEIGHT||
                bulletPosition_y<0||
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

    //将这个抽象为函数，然后调用....
    double t=1;
    //路径计算好以后，怎么给子弹
    //写成一个函数，然后计算返回一系列的点和角度即可.....List<Map<Point,int>>
    private void bulletPosition(){
        //这里关联speed和distance，暂时不处理
        if(drawFlag){
            bulletV_x=bulletV_x;
            bulletV_y=bulletV_y+StaticVariable.GRAVITY*t;
            int  newPosition_x = (int)(bulletPosition_x+bulletV_x*t);
            //bulletPosition_x+=v_x*t;
            int newPosition_y=(int)(bulletPosition_y+(bulletV_y*t+StaticVariable.GRAVITY*t*t/2));
            //bulletPosition_y+=v_y*t-g*t*t/2;
            double test = Math.abs(bulletV_y)/Math.abs(bulletV_x);
            bulletDegree=(int)Math.toDegrees(Math.atan (test));
            path.quadTo(bulletPosition_x, bulletPosition_y, newPosition_x, newPosition_y);
            bulletPosition_x=newPosition_x;
            bulletPosition_y=newPosition_y;
            Log.w(TAG,"bulletV_x:"+bulletV_x+" bulletV_y:"+bulletV_y);
            Log.w(TAG,"bulletDegree:"+bulletDegree+"bulletDistance:"+bulletDistance+" bulletPosition_x:"+bulletPosition_x+" bulletPosition_y:"+bulletPosition_y);
            t=t+StaticVariable.INTERVAL;
            }
        }




}

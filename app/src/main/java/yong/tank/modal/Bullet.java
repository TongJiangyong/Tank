package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by hasee on 2016/11/8.
 */

public class Bullet {
    //public BulletBascInfo(int type, int speed, int power, int picture, String bulletName)
    private  BulletBascInfo bulletBascInfo;
    private int bulletPosition_x = 0;
    private int bulletPosition_y =0;
    private int bulletDegree=0; //armpicture为内置的.....
    private Bitmap bulletPicture;
    private static String TAG = "Bullet";
    public Bullet(Bitmap bulletPicture,BulletBascInfo bulletBascInfo) {
        this.bulletPicture = bulletPicture;
        this.bulletBascInfo = bulletBascInfo;
    }

    public void drawSelf(Canvas canvas){
        Log.w(TAG,"draw Bullet");
        canvas.drawBitmap(this.bulletPicture,this.bulletPosition_x,this.bulletPosition_y,null);
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

    public int getBulletPosition_y() {
        return bulletPosition_y;
    }

    public void setBulletPosition_y(int bulletPosition_y) {
        this.bulletPosition_y = bulletPosition_y;
    }



    public int getBulletDegree() {
        return bulletDegree;
    }

    public void setBulletDegree(int bulletDegree) {
        this.bulletDegree = bulletDegree;
    }
}

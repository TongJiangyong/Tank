package yong.tank.modal;

import android.graphics.Bitmap;

import yong.tank.modal.abstractGoup.Bullet;

/**
 * Created by hasee on 2016/11/8.
 */

public class MyBullet extends Bullet{
    private transient static String TAG = "MyBullet";
    public MyBullet(Bitmap bulletPicture, int bulletType,double initVx,double initVy,float initPx,float initPy) {
        super(bulletPicture, bulletType);
        this.bulletV_x = initVx;
        this.bulletV_y = initVy;
        this.bulletPosition_x = initPx;
        this.bulletPosition_y = initPy;
    }
}

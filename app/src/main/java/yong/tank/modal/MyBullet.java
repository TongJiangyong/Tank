package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import yong.tank.modal.abstractGoup.Bullet;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/8.
 */

public class MyBullet extends Bullet{
    private transient static String TAG = "MyBullet";
    public MyBullet(Bitmap bulletPicture, int bulletType,double initVx,double initVy,int initPx,int initPy) {
        super(bulletPicture, bulletType);
        this.bulletV_x = initVx;
        this.bulletV_y = initVy;
        this.bulletPosition_x = initPx;
        this.bulletPosition_y = initPy;
    }

    public void  positionUpdate() {
        //这里关联speed和distance，暂时不处理
        //this.gameDto.getMyTank().getSelectedBullets()
        //这里计算时，采用向下为正，向右为正的方法
        //这里指示的是每一帧的内容
        Log.w(TAG, "**************bulletDegree:" + bulletDegree + " bulletV_y:" + bulletV_y + " bulletV_x:" + bulletV_x );
        bulletV_y = bulletV_y + StaticVariable.GRAVITY/StaticVariable.LOGICAL_FRAME;
        int newPosition_x = (int)(bulletPosition_x + bulletV_x/StaticVariable.LOGICAL_FRAME );
        //bulletPosition_x+=v_x*t;
        int newPosition_y = (int)(bulletPosition_y + bulletV_y/StaticVariable.LOGICAL_FRAME + StaticVariable.GRAVITY /(double)(2*StaticVariable.LOGICAL_FRAME*StaticVariable.LOGICAL_FRAME));
        //bulletPosition_y+=v_y*t-g*t*t/2;
        bulletDegree = (int) Math.toDegrees(Math.atan(bulletV_y / bulletV_x));
        //System.out.println( "bulletV_x:" + init_x + " bulletV_y:" + init_y);
        bulletPosition_x=newPosition_x;
        bulletPosition_y=newPosition_y;
        //Log.w(TAG, "bulletDegree:" + bulletDegree + "bulletDistance:" + bulletDistance + " bulletPosition_x:" + init_x + " bulletPosition_y:" + init_y);
        //time = time + StaticVariable.INTERVAL;
    }


    public void drawSelf(Canvas canvas){
        //TODO 绘制子弹 如果路径存在
        if(drawFlag){
            //for(int i =0;i<firePath.size();i++){
            matrix.setTranslate(bulletPosition_x, bulletPosition_y);//子弹坐标
            matrix.postRotate(bulletDegree,bulletPosition_x,bulletPosition_y);//子弹的旋转
            canvas.drawBitmap(this.bulletPicture, matrix, null);//绘制子弹
            Log.i(TAG, "X:" + bulletPosition_x +" Y:" + bulletPosition_y + " Degree:" + bulletDegree);
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


}

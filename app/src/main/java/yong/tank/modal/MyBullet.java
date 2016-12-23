package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import yong.tank.modal.abstractGoup.Bullet;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/8.
 */

public class MyBullet extends Bullet{

    public MyBullet(Bitmap bulletPicture, int bulletType) {
        super(bulletPicture, bulletType);
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


}

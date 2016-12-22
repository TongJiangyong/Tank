package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import yong.tank.modal.abstractGoup.Bullet;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/8.
 */

public class EnemyBullet extends Bullet{
    private transient static String TAG = "EnemyBullet";
    public EnemyBullet(Bitmap bulletPicture, BulletBascInfo bulletBascInfo) {
        super(bulletPicture, bulletBascInfo);
    }

    public void drawSelf(Canvas canvas){
        //如果是本地模式，则，按照路径数据进行绘制
        if(StaticVariable.CHOSED_MODE== StaticVariable.GAME_MODE.LOCAL){
            if(firePath!=null&&drawFlag&&pathPosition<firePath.size()){
                //for(int i =0;i<firePath.size();i++){
                matrix.setTranslate(firePath.get(pathPosition).getX(), firePath.get(pathPosition).getY());//子弹坐标
                //注意这里，给的角度为负数
                matrix.postRotate(-firePath.get(pathPosition).getDegree(),firePath.get(pathPosition).getX(),firePath.get(pathPosition).getY());//子弹的旋转
                bulletPosition_x=firePath.get(pathPosition).getX();
                bulletPosition_y=firePath.get(pathPosition).getY();
                canvas.drawBitmap(this.bulletPicture, matrix, null);//绘制子弹
                //Log.w(TAG, "X:" + firePath.get(pathPosition).getX() +" Y:" + firePath.get(pathPosition).getY() + " Degree:" + firePath.get(pathPosition).getDegree()+" position:"+pathPosition);
                pathPosition++;
                //}
            }
            //如果不是本地模式，则按照位置进行绘制
        }else{
            //如果不是本地模式，则，按坐标
            if(drawFlag) {
                matrix.setTranslate(bulletPosition_x, bulletPosition_y);//子弹坐标
                matrix.postRotate(bulletDegree, bulletPosition_x, bulletPosition_y);
                canvas.drawBitmap(this.bulletPicture, matrix, null);//绘制子弹
            }
        }
        if(bulletPosition_x> StaticVariable.LOCAL_SCREEN_WIDTH ||
                bulletPosition_y>StaticVariable.LOCAL_SCREEN_HEIGHT ||
                bulletPosition_x<0){
            this.setDrawFlag(false);
        }

    }


}

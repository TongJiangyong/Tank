package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import yong.tank.modal.abstractGoup.Bullet;
import yong.tank.modal.abstractGoup.Tank;
import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

/**
 * Created by hasee on 2016/10/27.
 */

public class EnemyTank extends Tank implements Serializable{
    private transient static String TAG = "EnemyTank";
    //这里好好学习一下
    public List<EnemyBullet> bulletsFire;
    public EnemyTank(Bitmap tankPicture, Bitmap armPicture, int tankType, TankBascInfo tankBascInfo) {
        super(tankPicture, armPicture, tankType, tankBascInfo);
        bulletsFire = new ArrayList<EnemyBullet>(3);//暂时发送子弹数为3？但是这样做好像没用.....
        //重置坦克的位置......
        this.tankPosition_x=StaticVariable.LOCAL_SCREEN_WIDTH *3/4-this.tankPicture.getWidth()/2;
        //this.tankPosition_y=StaticVariable.LOCAL_SCREEN_HEIGHT*3/4;
        //这是测试用的tank位置
        this.tankPosition_y=StaticVariable.LOCAL_SCREEN_HEIGHT *2/3;
    }


    public void drawSelf(Canvas canvas){
        //TODO 考虑策略模式优化
        if((tankPosition_x+this.tankPicture.getWidth())>StaticVariable.LOCAL_SCREEN_WIDTH){
            this.tankPosition_x=StaticVariable.LOCAL_SCREEN_WIDTH-this.tankPicture.getWidth();
        }else if((tankPosition_x+this.tankPicture.getWidth())<StaticVariable.LOCAL_SCREEN_WIDTH *4/7){
            this.tankPosition_x=StaticVariable.LOCAL_SCREEN_WIDTH *4/7;
        }
        canvas.drawBitmap(this.tankPicture,this.tankPosition_x,this.tankPosition_y,null);
        Bitmap armPicture_tmp = Tool.reBuildImg(this.getArmPicture(),this.weaponDegree,1,1,false,false);
        // 不知道为什么，这里和mytank不一样
        int weaponPoxitionTemp_x = this.tankPosition_x+tankPicture.getWidth()*3/5 -armPicture_tmp.getWidth();
        int weaponPoxitionTemp_y = this.tankPosition_y-armPicture_tmp.getHeight()+Tool.dip2px(StaticVariable.LOCAL_DENSITY, 22);
        canvas.drawBitmap(armPicture_tmp,
                weaponPoxitionTemp_x,
                //0,0,
                //注意这种角度的变化方法.....一定要加上图片本身的宽度....
                weaponPoxitionTemp_y,
                null);
        //坦克画在arm的后面

        // TODO 这里weapon的点要更精细一点
        this.weaponPoxition_x = weaponPoxitionTemp_x;
        this.weaponPoxition_y = weaponPoxitionTemp_y;

        /**绘制所有的子弹**/
        if(bulletsFire.size()==0){
        }else{
            //这里值得注意的是，模式不同，绘制子弹的方法也不同
            for (int i = bulletsFire.size() -1; i >= 0; i--) {
                //绘制子弹
                //Log.i(TAG,"enermy draw Bullet*****************");
                bulletsFire.get(i).drawSelf(canvas);
            }
        }
    }

    @Override
    public void addBuleetFire(Bullet bullet) {
        this.getBulletsFire().add((EnemyBullet) bullet);
    }


    public List<EnemyBullet> getBulletsFire() {
        return bulletsFire;
    }

    public void setBulletsFire(List<EnemyBullet> bulletsFire) {
        this.bulletsFire = bulletsFire;
    }
}

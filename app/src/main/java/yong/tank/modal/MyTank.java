package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

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
//TODO 写成抽象方法，然后重写drawSelf方法？类似处理包括 bullet mytank blood.....考虑一下这种做法....可信性比较好.....
public class MyTank extends Tank implements Serializable{
    private transient static String TAG = "MyTank";
    //这里好好学习一下
    public List<MyBullet> bulletsFire= new ArrayList<MyBullet>(3);
    public MyTank(Bitmap tankPicture, Bitmap armPicture, int tankType, TankBascInfo tankBascInfo) {
        super(tankPicture, armPicture, tankType, tankBascInfo);
        bulletsFire = new ArrayList<MyBullet>(3);//暂时发送子弹数为3？但是这样做好像没用.....
        this.tankPosition_x=StaticVariable.LOCAL_SCREEN_WIDTH /4-this.tankPicture.getWidth()/2;
        this.tankPrevPosition_x = this.tankPosition_x;
        //this.tankPosition_y=StaticVariable.LOCAL_SCREEN_HEIGHT*3/4;
        //这是测试用的tank位置
        this.tankPosition_y=StaticVariable.LOCAL_SCREEN_HEIGHT *2/3;
    }

    public void positionUpdate(){
        //TODO 考虑策略模式优化
        //this.tankPosition_x=tankPrevPosition_x+((Tool.dip2px(StaticVariable.LOCAL_DENSITY, tankBascInfo.getSpeed())/15)*tankDirectrion);
        //this.tankPosition_x=tankPrevPosition_x+tankBascInfo.getIntervalSpeed()*tankDirectrion;
        //int intervalSpeed = (int)((float)StaticVariable.LOCAL_SCREEN_WIDTH /(float)(2*StaticVariable.LOGICAL_FRAME*tankBascInfo.getSpeed()/10));
        //Log.i(TAG,"intervalSpeed:"+tankBascInfo.getIntervalSpeed());
        this.tankPosition_x=tankPrevPosition_x+tankBascInfo.getIntervalSpeed()*tankDirectrion;

        if(tankPosition_x<0){
            this.tankPosition_x=this.tankPrevPosition_x;
        }else if((tankPosition_x+this.tankPicture.getWidth())>(StaticVariable.LOCAL_SCREEN_WIDTH /2)){
            //TODO 这里先暂时设置为一半
            this.tankPosition_x=this.tankPrevPosition_x;
        }
    }
    public void drawSelf(Canvas canvas){
        canvas.drawBitmap(this.tankPicture,this.tankPosition_x,this.tankPosition_y,null);
        Bitmap armPicture_tmp = Tool.reBuildImg(this.getArmPicture(),this.weaponDegree,1,1,false,false);
        int weaponPoxitionTemp_x = this.tankPosition_x+tankPicture.getWidth()*2/5;
        int weaponPoxitionTemp_y = this.tankPosition_y-armPicture_tmp.getHeight()+Tool.dip2px(StaticVariable.LOCAL_DENSITY, 22);
        canvas.drawBitmap(armPicture_tmp,
                weaponPoxitionTemp_x,
                //0,0,
                //注意这种角度的变化方法.....一定要加上图片本身的宽度....
                weaponPoxitionTemp_y,
                null);
        //坦克画在arm的后面
        //Log.i(TAG,"tankPosition_x is:"+tankPosition_x);
        tankPrevPosition_x = tankPosition_x;
        // TODO 这里weapon的点要更精细一点
        this.weaponPoxition_x = weaponPoxitionTemp_x+armPicture_tmp.getWidth();
        this.weaponPoxition_y = weaponPoxitionTemp_y;
        //Log.i(TAG,"current position:"+weaponPoxition_x);
        //TODO 绘制预发射的子弹路径 绘制点的方法即可
        if(preFirePath!=null){
            Paint preFirePathPaint = new Paint();
            preFirePathPaint.setStrokeWidth(3);
            for(int i=0;i<preFirePath.size();i++)
            {
                canvas.drawPoint(preFirePath.get(i).getX(), preFirePath.get(i).getY(), preFirePathPaint);
            }
            this.drawPreFireCircle(canvas);
        }

        //绘制所有的子弹
        if(bulletsFire.size()==0){
        }else{
            //注意多线程的处理
            for (int i = bulletsFire.size() -1; i >= 0; i--)
            {
                //更新该子弹的位置
                bulletsFire.get(i).positionUpdate();
                //绘制子弹
                bulletsFire.get(i).drawSelf(canvas);
            }
        }
    }


    public List<MyBullet> getBulletsFire() {
        return bulletsFire;
    }

    public void setBulletsFire(List<MyBullet> bulletsFire) {
        this.bulletsFire = bulletsFire;
    }

    public void addBuleetFire(Bullet bullet) {
        //这里学习体会一下
        this.getBulletsFire().add((MyBullet) bullet);
    }


}

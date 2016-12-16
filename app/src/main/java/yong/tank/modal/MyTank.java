package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import yong.tank.modal.abstractGoup.Tank;
import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

/**
 * Created by hasee on 2016/10/27.
 */
//TODO 写成抽象方法，然后重写drawSelf方法？类似处理包括 bullet mytank blood.....考虑一下这种做法....可信性比较好.....
public class MyTank extends Tank {


    public MyTank(Bitmap tankPicture, Bitmap armPicture, int tankType, TankBascInfo tankBascInfo) {
        super(tankPicture, armPicture, tankType, tankBascInfo);
    }

    public void drawSelf(Canvas canvas){
        //TODO 考虑策略模式优化
        if(tankPosition_x<0){
            this.tankPosition_x=0;
        }else if((tankPosition_x+this.tankPicture.getWidth())>StaticVariable.LOCAL_SCREEN_WIDTH *3/7){
            this.tankPosition_x=(StaticVariable.LOCAL_SCREEN_WIDTH *3/7-this.tankPicture.getWidth());
        }else{
            //由于这里有余数，所以会让分辨率产生损失.....感觉这样没办法处理.....
            //Log.i(TAG,"move:"+(Tool.dip2px(StaticVariable.LOCAL_DENSITY, tankBascInfo.getSpeed())));
            this.tankPosition_x+=((Tool.dip2px(StaticVariable.LOCAL_DENSITY, tankBascInfo.getSpeed())/15)*tankDirectrion);
        }
        canvas.drawBitmap(this.tankPicture,this.tankPosition_x,this.tankPosition_y,null);
        Bitmap armPicture_tmp = Tool.reBuildImg(this.getArmPicture(),this.weaponDegree,1,1,false,false);
        //Bitmap armPicture_tmp_2 = Tool.reBuildImg(armPicture_tmp,0,1,1,true,false);
        //TODO 注意这两种方法，硬编码可以让不同分辨率的屏幕适应......
//        Log.i(TAG,"width:"+Tool.dip2px(StaticVariable.LOCAL_DENSITY, 22));
        int weaponPoxitionTemp_x = this.tankPosition_x+tankPicture.getWidth()*2/5;
        int weaponPoxitionTemp_y = this.tankPosition_y-armPicture_tmp.getHeight()+Tool.dip2px(StaticVariable.LOCAL_DENSITY, 22);
        canvas.drawBitmap(armPicture_tmp,
                weaponPoxitionTemp_x,
                //0,0,
                //注意这种角度的变化方法.....一定要加上图片本身的宽度....
                weaponPoxitionTemp_y,
                null);
        //坦克画在arm的后面

        // TODO 这里weapon的点要更精细一点
        this.weaponPoxition_x = weaponPoxitionTemp_x+armPicture_tmp.getWidth();
        this.weaponPoxition_y = weaponPoxitionTemp_y;

        //TODO 绘制预发射的子弹路径 绘制点的方法即可
        if(preFirePath!=null){
            Paint preFirePathPaint = new Paint();
            preFirePathPaint.setStrokeWidth(5);
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
                //绘制子弹
                bulletsFire.get(i).drawSelf(canvas);
            }
        }
    }
}

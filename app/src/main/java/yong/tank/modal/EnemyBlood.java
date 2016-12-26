package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import yong.tank.modal.abstractGoup.Blood;
import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

/**
 * Created by hasee on 2016/11/1.
 */

public class EnemyBlood extends Blood{
    public  transient int bloodBlock_x=0 ;
    public transient int bloodBlock_y=0;
    public EnemyBlood(Bitmap blood, Bitmap power, Bitmap bloodBlock, float bloodNum, float powerNum) {
        super(blood, power, bloodBlock, bloodNum, powerNum);
        //TODO 可能dip2px只能排除LOCAL_DENSITY的大小，不能排除尺寸的大小，所以不能用注释中的话，只能用
        //bloodBlock_x= Tool.dip2px(StaticVariable.LOCAL_DENSITY,485);
        bloodBlock_x=(int)(StaticVariable.LOCAL_SCREEN_WIDTH-bloodBlock.getWidth()-Tool.dip2px(StaticVariable.LOCAL_DENSITY,5));
        bloodBlock_y=Tool.dip2px(StaticVariable.LOCAL_DENSITY,5);
    }

    //这里配置一个其他的drawEnermy方法进行配置，可能会更好，或者将静态量提取出drawSelf出来
    public void drawSelf(Canvas canvas) {
        //计算血条的比例
        Rect src_blood = new Rect((int)(blood.getWidth()*(1-bloodNum)), 0, blood.getWidth(), blood.getHeight());
        Rect des_blood = new Rect(bloodBlock_x+Tool.dip2px(StaticVariable.LOCAL_DENSITY,5)+(int)(blood.getWidth()*(1-bloodNum)),
                bloodBlock_y+Tool.dip2px(StaticVariable.LOCAL_DENSITY,29),
                bloodBlock_x+Tool.dip2px(StaticVariable.LOCAL_DENSITY,5)+blood.getWidth(),
                bloodBlock_y+Tool.dip2px(StaticVariable.LOCAL_DENSITY,29)+blood.getHeight());
        //计算power的比例
        Rect src_power = new Rect((int)(power.getWidth()*(1-powerNum)), 0, power.getWidth(), power.getHeight());
        Rect des_power = new Rect(bloodBlock_x+Tool.dip2px(StaticVariable.LOCAL_DENSITY,15)+(int)(power.getWidth()*(1-powerNum)),
                bloodBlock_y+Tool.dip2px(StaticVariable.LOCAL_DENSITY,41),
                bloodBlock_x+Tool.dip2px(StaticVariable.LOCAL_DENSITY,15)+power.getWidth(),
                bloodBlock_y+Tool.dip2px(StaticVariable.LOCAL_DENSITY,41)+power.getHeight());


        //绘制装填进度条
        canvas.drawBitmap(power,src_power,des_power, null);
        //绘制框
        canvas.drawBitmap(bloodBlock,bloodBlock_x,bloodBlock_y, null);
        //绘制血条
        canvas.drawBitmap(blood, src_blood, des_blood, null);
        //TODO 这里设置装填的时间需要1s 硬编码
        if(powerNum<1){
            powerNum=powerNum+0.04;
        }else{
            //设置运行发射
            this.allowFire = true;
        }
    }

}

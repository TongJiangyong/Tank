package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/1.
 */

public class Bonus {
    private Bitmap bonusPicture;
    private int bonusDirection= 0;  //0为左 ，1为右边
    private int bonus_x;
    private int bonus_y;
    private static String TAG = "Bonus";
    private int pathPosition = 0;  //当前子弹位于的position 绘制敌方子弹主要的变量
    //bonus的路径点
    private List<Point> bonusPath;
    //bonus是否需要继续绘制非flag
    private boolean isDrawFlag=true;
    private int bonusType; //注意，这里的type和子弹的类型有关系，击中bonus后，产生相应的变化
    public Bonus(Bitmap bonusPicture, List<Point> bonusPath,int bonusType) {
        this.bonusPicture = bonusPicture;
        this.bonusPath = bonusPath;
        this.bonusType = bonusType;
    }

    public void drawSelf(Canvas canvas) {
        //TODO 绘制bonus 注意
        if(bonusPath!=null&&pathPosition<bonusPath.size()&&isDrawFlag){
            bonus_x=bonusPath.get(pathPosition).getX();
            bonus_y=bonusPath.get(pathPosition).getY();
            canvas.drawBitmap(this.bonusPicture, bonus_x,bonus_y, null);//绘制子弹
            //Log.w(TAG, "X:" + bonusPath.get(pathPosition).getX() +" Y:" + bonusPath.get(pathPosition).getY());
            pathPosition++;
        }
        //如果超出界线，则停止绘制
            if(bonus_x> StaticVariable.SCREEN_WIDTH||bonus_x<0){
                this.setDrawFlag(false);
            }
    }

    public int getBonus_x() {
        return bonus_x;
    }

    public void setBonus_x(int bonus_x) {
        this.bonus_x = bonus_x;
    }

    public int getBonus_y() {
        return bonus_y;
    }

    public void setBonus_y(int bonus_y) {
        this.bonus_y = bonus_y;
    }

    public int getPathPosition() {
        return pathPosition;
    }

    public void setPathPosition(int pathPosition) {
        this.pathPosition = pathPosition;
    }

    public List<Point> getBonusPath() {
        return bonusPath;
    }

    public int getBonusType() {
        return bonusType;
    }

    public void setDrawFlag(boolean drawFlag) {
        isDrawFlag = drawFlag;
    }

    public Bitmap getBonusPicture() {
        return bonusPicture;
    }
}

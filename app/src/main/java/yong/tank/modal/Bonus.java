package yong.tank.modal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.io.Serializable;
import java.util.List;

import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/1.
 */

public class Bonus implements Serializable {
    private transient Bitmap bonusPicture;
    private int bonusDirection= 0;  //1为左 ，-1为右边
    private float bonus_x;
    private float bonus_y;
    private boolean isBonusFired = false; //设置bonus的状态 是否被击中等
    private static String TAG = "Bonus";
    private int pathPosition = 0;  //当前子弹位于的position 绘制敌方子弹主要的变量
    //bonus的路径点
    private transient List<Point> bonusPath;
    //bonus是否需要继续绘制非flag
    private transient boolean isDrawFlag=true;
    private Point bonusCenter = new Point();
    private int bonusType; //注意，这里的type和子弹的类型有关系，击中bonus后，产生相应的变化
    //activity产生的bonus
    public Bonus(Bitmap bonusPicture, int bonusDirection,int bonusType) {
        this.bonusPicture = bonusPicture;
        this.bonusType = bonusType;
        if(bonusDirection==0){
            bonus_x=-bonusPicture.getWidth();
            this.bonusDirection = 1;
        }else{
            bonus_x=-bonusPicture.getWidth();
            this.bonusDirection = -1;
        }
    }
    //client产生的bonux
    public Bonus(Bitmap bonusPicture,int bonusType) {
        this.bonusPicture = bonusPicture;
        this.bonusType = bonusType;
    }

    public void positionUpdate(){
        //TODO 考虑策略模式优化;
        if(bonus_x>=-bonusPicture.getWidth()&&bonus_x<=StaticVariable.LOCAL_SCREEN_WIDTH){
   //         if (StaticVariable.CHOSED_MODE == StaticVariable.GAME_MODE.LOCAL) {
            bonus_x=bonus_x+StaticVariable.BONUS_SPEED;
                //注意这里除法是易错点
            bonus_y=StaticVariable.BONUS_Y_INIT +(float)((Math.sin(bonus_x/(StaticVariable.BONUS_SPEED*StaticVariable.BONUS_STEP))*StaticVariable.BONUS_SCALE));
 /*           }else{
            if(this.bonusDirection>0){
                bonus_x=bonus_x+StaticVariable.BONUS_SPEED*bonusDirection;
                //注意这里除法是易错点
                bonus_y=StaticVariable.BONUS_Y_INIT +(float)((Math.sin(bonus_x/(StaticVariable.BONUS_SPEED*StaticVariable.BONUS_STEP))*StaticVariable.BONUS_SCALE));
            }else{
                bonus_x=bonus_x+StaticVariable.BONUS_SPEED*(-bonusDirection);
                //注意这里除法是易错点
                bonus_y=StaticVariable.BONUS_Y_INIT +(float)((Math.sin(bonus_x/(StaticVariable.BONUS_SPEED*StaticVariable.BONUS_STEP))*StaticVariable.BONUS_SCALE));
            }*/
/*            }*/
            Log.i(TAG,"bonus_x:"+bonus_x+",bonus_y:"+bonus_y+",StaticVariable.BONUS_SPEED"+StaticVariable.BONUS_SPEED);
        }else{
            //TODO 将bonus删掉 并停止绘制
            this.setDrawFlag(false);
        }
    }

    public void drawSelf(Canvas canvas) {
//        if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY){
            //TODO 绘制bonus 注意
            if(isDrawFlag&&!isBonusFired){
                //bonus_x=bonusPath.get(pathPosition).getX();
                //bonus_y=bonusPath.get(pathPosition).getY();
                if(bonusDirection>0){
                    canvas.drawBitmap(this.bonusPicture, bonus_x,bonus_y, null);//绘制bonus
                }else{
                    canvas.drawBitmap(this.bonusPicture, (float)(StaticVariable.LOCAL_SCREEN_WIDTH-bonusPicture.getWidth())-bonus_x,bonus_y, null);//绘制bonus
                }
                //Log.w(TAG, "X:" + bonusPath.get(pathPosition).getX() +" Y:" + bonusPath.get(pathPosition).getY());
            }
/*        }else{
            if(isDrawFlag&&!isBonusFired){
                if(bonusDirection>0){
                    canvas.drawBitmap(this.bonusPicture, bonus_x,bonus_y, null);//绘制bonus
                }else{
                    canvas.drawBitmap(this.bonusPicture, (float)(StaticVariable.LOCAL_SCREEN_WIDTH-bonusPicture.getWidth())-bonus_x,bonus_y, null);//绘制bonus
                }
                //Log.w(TAG, "X:" + bonusPath.get(pathPosition).getX() +" Y:" + bonusPath.get(pathPosition).getY());
            }
        }*/

    }

    public float getBonus_x() {
        return bonus_x;
    }

    public void setBonus_x(int bonus_x) {
        this.bonus_x = bonus_x;
    }

    public float getBonus_y() {
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

    public boolean isBonusFired() {
        return isBonusFired;
    }

    public void setIsBonusFired(boolean isBonusFired) {
        this.isBonusFired = isBonusFired;
    }

    public Point getBonusCenter() {
        this.bonusCenter.setX(this.getBonus_x()+this.bonusPicture.getWidth()/2);
        this.bonusCenter.setY(this.getBonus_y()+this.bonusPicture.getHeight()/2);
        return bonusCenter;
    }

    public boolean isInBonusScope(float dx,float dy){
        if(Math.abs(dx-this.getBonusCenter().getX())<this.bonusPicture.getWidth()/2&&
           Math.abs(dy-this.getBonusCenter().getY())<this.bonusPicture.getHeight()/2){
            return true;
        }else{
            return false;
        }
    }


}

package yong.tank.Game_Activity.ViewThread;

import android.graphics.Canvas;

import yong.tank.Dto.GameDto;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */
//TODO 重点：测试GSOM能否转换非规则数据，入bitmap等数据......
public class BloodFrame {
    private boolean flag =true;
    private GameDto gameDto;
    private Canvas canvas;
    private static String TAG = "BloodThread";
    public BloodFrame(GameDto gameDto) {
        this.flag =true;
        this.gameDto = gameDto;
    }

    public void stopDrawing(){
        flag = false;
    }

    public void drawFrame(float interpolation,Canvas canvas) {
        //TODO 这里对canvas的使用有误,不能让所有的线程都使用canvas
            try {
                    //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
                    gameDto.getMyBlood().drawSelf(canvas);
                    if(gameDto.getEnemyBlood()!=null){
                        gameDto.getEnemyBlood().drawSelf(canvas);
                    }
                }
            catch (Exception e) {
                e.printStackTrace();
            }
    }

}

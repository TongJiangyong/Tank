package yong.tank.Game_Activity.ViewThread;

import android.graphics.Canvas;

import yong.tank.Dto.GameDto;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class GameFrame {
    private boolean flag =true;
    private GameDto gameDto;
    private Canvas canvas;
    private static String TAG = "GameThread";
    public GameFrame(GameDto gameDto) {
        this.flag =true;
        this.gameDto = gameDto;
    }

    public void stopDrawing(){
        flag = false;
    }

    public void drawFrame(float interpolation,Canvas canvas) {
        //TODO 这里对canvas的使用有误,不能让所有的线程都使用canvas
            try {
                if (flag) {
                    //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
                    //canvas.drawBitmap(gameDto.getMyTank().getTankPicture(),0,0,null);
                    //TODO 设计为同时绘制两个坦克
                    gameDto.getMyTank().drawSelf(canvas);
                    if (gameDto.getEnemyTank() != null) {
                        gameDto.getEnemyTank().drawSelf(canvas);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

    }

}

package yong.tank.Game_Activity.View;

import android.content.Context;
import android.graphics.Canvas;

import yong.tank.Dto.GameDto;
import yong.tank.Game_Activity.ViewThread.GameFrame;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class GameView extends ViewBase{

    private GameFrame gameFrame;
    //private ExplodeThread explodeThread;注意这里将explode的thread给了gameView，并没有放到单独的view中，可能会出现锁的问题
    private static String TAG = "GameView";


    public GameView(Context context, GameDto gameDto) {
        super(context, gameDto);
        this.gameFrame = new GameFrame(this.gameDto);
    }



    @Override
    public void stopDrawFrame() {
        if(this.gameFrame !=null){
            this.gameFrame.stopDrawing();
        }
        //this.explodeThread.stopThread();
    }

    @Override
    public void startDrawFrame(float interpolation ,Canvas drawCanvas) {
        this.gameFrame.drawFrame(interpolation,drawCanvas);
    }

}

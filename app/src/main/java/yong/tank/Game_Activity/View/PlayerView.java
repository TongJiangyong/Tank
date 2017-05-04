package yong.tank.Game_Activity.View;

import android.content.Context;
import android.graphics.Canvas;

import yong.tank.Dto.GameDto;
import yong.tank.Game_Activity.ViewThread.PlayerFrame;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class PlayerView extends ViewBase{

    private PlayerFrame playerFrame;
    private static String TAG = "PlayerView";
    public PlayerView(Context context, GameDto gameDto) {
        super(context, gameDto);
        this.playerFrame = new PlayerFrame(this.gameDto);
    }



    @Override
    public void stopDrawFrame() {
        if(this.playerFrame !=null){
            this.playerFrame.stopDrawing();
        }
    }

    @Override
    public void startDrawFrame(float interpolation, Canvas drawCanvas) {
        /*测试发现，initThread已经在其他的线程进行了调用....没有办法再在外部进行调用了
        * */
/*        if(this.playerThread==null){
            Log.i(TAG,"playerThread is  null");
        }else{
            Log.i(TAG,"playerThread is not null");
        }*/
        this.playerFrame.drawFrame(interpolation,drawCanvas);
    }
}

package yong.tank.Game_Activity.View;

import android.content.Context;
import android.graphics.Canvas;

import yong.tank.Dto.GameDto;
import yong.tank.Game_Activity.ViewThread.ExplodeFrame;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class ExplodeView extends ViewBase{

    private ExplodeFrame explodeFrame; //注意这里将explode的thread给了gameView，并没有放到单独的view中，可能会出现锁的问题
    private static String TAG = "ExplodeView";


    public ExplodeView(Context context, GameDto gameDto) {
        super(context, gameDto);
        this.explodeFrame = new ExplodeFrame(this.gameDto);
    }



    @Override
    public void stopDrawFrame() {
        if(this.explodeFrame !=null){
            this.explodeFrame.stopDrawing();
        }

    }

    @Override
    public void startDrawFrame(float interpolation ,Canvas drawCanvas) {
        this.explodeFrame.drawFrame(interpolation,drawCanvas);
    }
}

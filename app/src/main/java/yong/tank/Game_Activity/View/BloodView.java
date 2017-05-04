package yong.tank.Game_Activity.View;

import android.content.Context;
import android.graphics.Canvas;

import yong.tank.Dto.GameDto;
import yong.tank.Game_Activity.ViewThread.BloodFrame;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class BloodView extends ViewBase{

    private BloodFrame bloodFrame;
    private static String TAG = "BloodView";


    public BloodView(Context context, GameDto gameDto) {
        super(context, gameDto);
        this.bloodFrame = new BloodFrame(this.gameDto);
    }


    @Override
    public void stopDrawFrame() {
        if(this.bloodFrame !=null){
            this.bloodFrame.stopDrawing();
        }
    }

    //Blood为什么只有在init中才有用？？？？？
    @Override
    public void startDrawFrame(float interpolation ,Canvas drawCanvas) {
        this.bloodFrame.drawFrame(interpolation,drawCanvas);
    }


}

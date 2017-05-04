package yong.tank.Game_Activity.View;

import android.content.Context;
import android.graphics.Canvas;

import yong.tank.Dto.GameDto;
import yong.tank.Game_Activity.ViewThread.BonusFrame;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class BonusView extends ViewBase{

    private BonusFrame bonusFrame;
    private static String TAG = "BonusView";


    public BonusView(Context context, GameDto gameDto) {
        super(context, gameDto);
        this.bonusFrame = new BonusFrame(this.gameDto);
    }



    @Override
    public void stopDrawFrame() {
        if(this.bonusFrame !=null){
            this.bonusFrame.stopDrawing();
        }

    }
    @Override
    public void startDrawFrame(float interpolation ,Canvas drawCanvas) {
        this.bonusFrame.drawFrame(interpolation,drawCanvas);
    }

}

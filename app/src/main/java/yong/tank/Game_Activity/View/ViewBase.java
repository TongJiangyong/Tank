package yong.tank.Game_Activity.View;

import android.content.Context;
import android.graphics.Canvas;

import yong.tank.Dto.GameDto;

/**
 * Created by hasee on 2016/10/31.
 */

public abstract class ViewBase{

    public GameDto gameDto;
    public Canvas canvas;
    public Context context;
    public static String TAG = "ViewBase";
    public ViewBase(Context context, GameDto gameDto) {
        this.context = context;
        this.gameDto = gameDto;
    }

    //TODO 感觉这个方法没啥用....
    abstract public void stopDrawFrame();

    abstract public void startDrawFrame(float interpolation, Canvas drawCanvas);
}

package yong.tank.Game_Activity.View;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import yong.tank.Dto.GameDto;

/**
 * Created by hasee on 2016/10/31.
 */

public abstract class ViewBase extends SurfaceView implements SurfaceHolder.Callback{

    public ViewBase(Context context) {
        super(context);
    }
    public GameDto gameDto;
    public SurfaceHolder holder;
    public static String TAG = "ViewBase";
    public ViewBase(Context context, GameDto gameDto) {
        super(context);
        this.gameDto = gameDto;
        holder=this.getHolder(); //不加这个surfacecread不能启动....
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);  //设置holder为透明必须要加
        getHolder().addCallback(this); //不加的话，surfacehold会默认不启动，比较麻烦
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.initThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.stopThread();
    }

    abstract void initThread();

    abstract void stopThread();

}

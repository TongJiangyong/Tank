package yong.tank.Game.View;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;

import yong.tank.Dto.GameDto;
import yong.tank.Game.thread.BloodThread;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class BloodView extends ViewBase implements SurfaceHolder.Callback{

    private GameDto gameDto;
    private BloodThread bloodThread;
    private SurfaceHolder holder;
    private static String TAG = "BloodView";
    public BloodView(Context context, GameDto gameDto) {
        super(context);
        this.gameDto = gameDto;
        holder=this.getHolder(); //不加这个surfacecread不能启动....
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);  //设置holder为透明必须要加
        getHolder().addCallback(this); //不加的话，surfacehold会默认不启动，比较麻烦
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.bloodThread = new BloodThread(this.gameDto,this.holder);
        new Thread(this.bloodThread).start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.bloodThread.stopThread();
    }


}

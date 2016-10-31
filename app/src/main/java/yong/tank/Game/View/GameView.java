package yong.tank.Game.View;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;

import yong.tank.Dto.GameDto;
import yong.tank.Game.thread.GameThread;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class GameView extends ViewBase implements SurfaceHolder.Callback{

    private GameDto gameDto;
    private GameThread gameThread;
    private SurfaceHolder holder;
    private static String TAG = "GameView";
    public GameView(Context context,GameDto gameDto) {
        super(context);
        this.gameDto = gameDto;
        holder=this.getHolder(); //不加这个surfacecread不能启动....
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);  //设置holder为透明必须要加
        getHolder().addCallback(this); //不加的话，surfacehold会默认不启动，比较麻烦
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.gameThread = new GameThread(this.gameDto,this.holder);
        new Thread(this.gameThread).start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.gameThread.stopThread();
    }


}

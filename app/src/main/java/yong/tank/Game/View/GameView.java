package yong.tank.Game.View;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import yong.tank.Dto.GameDto;
import yong.tank.Game.thread.GameThread;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback{

    private GameDto gameDto;
    private GameThread gameThread;
    private SurfaceHolder holder;
    private static String TAG = "GameView";
    public GameView(Context context) {
        super(context);
        holder=this.getHolder();
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);  //设置holder为透明必须要加
        getHolder().addCallback(this); //不加的话，surfacehold会默认不启动，比较麻烦
    }

    public GameView(Context context,GameDto gameDto) {
        super(context);
        this.gameDto = gameDto;
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(TAG,"test......");

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        this.gameThread = new GameThread(this.gameDto,this.holder);
        new Thread(this.gameThread).start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.gameThread.stopThread();
    }




    public void setGameDto(GameDto gameDto) {
        this.gameDto = gameDto;
    }
}

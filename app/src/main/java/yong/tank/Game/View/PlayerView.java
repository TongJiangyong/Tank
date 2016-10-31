package yong.tank.Game.View;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import yong.tank.Dto.GameDto;
import yong.tank.Game.thread.PlayerThread;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class PlayerView extends SurfaceView implements SurfaceHolder.Callback{

    private GameDto gameDto;
    private PlayerThread playerThread;
    private SurfaceHolder holder;
    private static String TAG = "PlayerView";
    public PlayerView(Context context) {
        super(context);
        this.holder=this.getHolder();
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);  //设置holder为透明必须要加
        getHolder().addCallback(this); //不加的话，surfacehold会默认不启动，比较麻烦
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        this.playerThread = new PlayerThread(this.gameDto,this.holder);
        new Thread(this.playerThread).start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.playerThread.stopThread();
    }




    public void setGameDto(GameDto gameDto) {
        this.gameDto = gameDto;
    }
}

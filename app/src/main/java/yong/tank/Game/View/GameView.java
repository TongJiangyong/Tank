package yong.tank.Game.View;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import yong.tank.Dto.GameDto;
import yong.tank.Game.thread.GameThread;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class GameView extends GameBase implements SurfaceHolder.Callback{

    private GameDto gameDto;
    private GameThread gameThread;
    private SurfaceHolder holder;

    public GameView(Context context) {
        super(context);
    }
    public GameView(Context context, GameDto gameDto) {
        super(context);
        this.gameDto = gameDto;
        holder=this.getHolder();
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

    }


    @Override
    public void startThread() {

    }
}

package yong.tank.Game.View;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.SurfaceHolder;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class GameView extends Surface implements SurfaceHolder.Callback{
    public GameView(SurfaceTexture surfaceTexture) {
        super(surfaceTexture);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}

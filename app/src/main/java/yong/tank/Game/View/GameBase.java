package yong.tank.Game.View;

import android.content.Context;
import android.view.SurfaceView;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public abstract class  GameBase extends SurfaceView {

    public GameBase(Context context) {
        super(context);
    }
    public abstract void startThread();

}

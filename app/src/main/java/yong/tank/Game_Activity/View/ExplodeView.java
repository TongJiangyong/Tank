package yong.tank.Game_Activity.View;

import android.content.Context;

import yong.tank.Dto.GameDto;
import yong.tank.Game_Activity.thread.ExplodeThread;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class ExplodeView extends ViewBase{

    private ExplodeThread explodeThread; //注意这里将explode的thread给了gameView，并没有放到单独的view中，可能会出现锁的问题
    private static String TAG = "ExplodeView";

    public ExplodeView(Context context) {
        super(context);
    }

    public ExplodeView(Context context, GameDto gameDto) {
        super(context, gameDto);
    }

    @Override
    void initThread() {
        this.explodeThread = new ExplodeThread(this.gameDto,this.holder);
        new Thread(this.explodeThread).start();


    }

    @Override
    void stopThread() {
        this.explodeThread.stopThread();
    }

    @Override
    public void startThread() {
        new Thread(this.explodeThread).start();
    }
}

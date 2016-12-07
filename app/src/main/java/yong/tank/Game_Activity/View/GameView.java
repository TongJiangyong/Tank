package yong.tank.Game_Activity.View;

import android.content.Context;

import yong.tank.Dto.GameDto;
import yong.tank.Game_Activity.thread.ExplodeThread;
import yong.tank.Game_Activity.thread.GameThread;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class GameView extends ViewBase{

    private GameThread gameThread;
    private ExplodeThread explodeThread; //注意这里将explode的thread给了gameView，并没有放到单独的view中，可能会出现锁的问题
    private static String TAG = "GameView";

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, GameDto gameDto) {
        super(context, gameDto);
    }

    @Override
    void initThread() {
        this.gameThread = new GameThread(this.gameDto,this.holder);
        new Thread(this.gameThread).start();
        //TODO 测试发现，如果将explode的线程，写在这里，会发生很严重的问题.....
        //this.explodeThread = new ExplodeThread(this.gameDto,this.holder);
        //new Thread(this.explodeThread).start();


    }

    @Override
    void stopThread() {
        this.gameThread.stopThread();
        //this.explodeThread.stopThread();
    }


}

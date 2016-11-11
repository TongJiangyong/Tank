package yong.tank.Game.View;

import android.content.Context;

import yong.tank.Dto.GameDto;
import yong.tank.Game.thread.GameThread;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class GameView extends ViewBase{

    private GameThread gameThread;
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
    }

    @Override
    void stopThread() {
        this.gameThread.stopThread();
    }


}

package yong.tank.Game_Activity.View;

import android.content.Context;

import yong.tank.Dto.GameDto;
import yong.tank.Game_Activity.thread.PlayerThread;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class PlayerView extends ViewBase{

    private PlayerThread playerThread;
    private static String TAG = "PlayerView";
    public PlayerView(Context context) {
        super(context);
    }

    public PlayerView(Context context, GameDto gameDto) {
        super(context, gameDto);
    }

    @Override
    void initThread() {
        this.playerThread = new PlayerThread(this.gameDto,this.holder);
        new Thread(this.playerThread).start();
    }

    @Override
    void stopThread() {
        this.playerThread.stopThread();
    }

}

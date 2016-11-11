package yong.tank.Game.View;

import android.content.Context;

import yong.tank.Dto.GameDto;
import yong.tank.Game.thread.BloodThread;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class BloodView extends ViewBase{

    private BloodThread bloodThread;
    private static String TAG = "BloodView";

    public BloodView(Context context) {
        super(context);
    }

    public BloodView(Context context, GameDto gameDto) {
        super(context, gameDto);
    }

    @Override
    void initThread() {
        this.bloodThread = new BloodThread(this.gameDto,this.holder);
        new Thread(this.bloodThread).start();
    }

    @Override
    void stopThread() {
        this.bloodThread.stopThread();
    }


}

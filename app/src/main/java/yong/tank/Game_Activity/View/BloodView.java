package yong.tank.Game_Activity.View;

import android.content.Context;

import yong.tank.Dto.GameDto;
import yong.tank.Game_Activity.thread.BloodThread;

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

    //Blood为什么只有在init中才有用？？？？？
    @Override
    public void startThread() {
        new Thread(this.bloodThread).start();
    }


}

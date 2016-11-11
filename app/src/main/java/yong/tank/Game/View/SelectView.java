package yong.tank.Game.View;

import android.content.Context;

import yong.tank.Dto.GameDto;
import yong.tank.Game.thread.SelectThread;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class SelectView extends ViewBase{

    private SelectThread selectThread;
    private static String TAG = "SelectView";

    public SelectView(Context context) {
        super(context);
    }

    public SelectView(Context context, GameDto gameDto) {
        super(context, gameDto);
    }

    @Override
    void initThread() {
        this.selectThread = new SelectThread(this.gameDto,this.holder);
        new Thread(this.selectThread).start();
    }

    @Override
    void stopThread() {
        this.selectThread.stopThread();
    }


}

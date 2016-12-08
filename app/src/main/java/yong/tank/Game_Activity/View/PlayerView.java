package yong.tank.Game_Activity.View;

import android.content.Context;

import yong.tank.Dto.GameDto;
import yong.tank.Game_Activity.ViewThread.PlayerThread;

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
    public void stopThread() {
        this.playerThread.stopThread();
    }

    @Override
    public void startThread() {
        /*测试发现，initThread已经在其他的线程进行了调用....没有办法再在外部进行调用了
        * */
/*        if(this.playerThread==null){
            Log.i(TAG,"playerThread is  null");
        }else{
            Log.i(TAG,"playerThread is not null");
        }*/
        this.playerThread = new PlayerThread(this.gameDto,this.holder);
        new Thread(this.playerThread).start();
    }
}

package yong.tank.Game.thread;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import yong.tank.Dto.GameDto;

/**
 * Created by hasee on 2016/11/10.
 */

public class BonusThread implements Runnable  {
    private boolean flag =true;
    private GameDto gameDto;
    private SurfaceHolder holder;
    private Canvas canvas;
    private static String TAG = "BonusThread";
    public BonusThread(GameDto gameDto, SurfaceHolder holder) {
        this.flag =true;
        this.holder = holder;
        this.gameDto = gameDto;
    }

    public void stopThread(){
        flag = false;
    }

    public void run() {
        //TODO 这里对canvas的使用有误,不能让所有的线程都使用canvas
        while(flag){
            try {
                synchronized (holder){
                    Log.w(TAG,"TEST BonusThread");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally{
                if(canvas!= null){
                    holder.unlockCanvasAndPost(canvas);//结束锁定画图，并提交改变。
                }
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}

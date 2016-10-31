package yong.tank.Game.thread;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import yong.tank.Dto.GameDto;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class GameThread implements Runnable {
    private boolean flag =true;
    private GameDto gameDto;
    private SurfaceHolder holder;
    private Canvas canvas;
    public GameThread(GameDto gameDto,SurfaceHolder holder) {
        this.flag =true;
        this.holder = holder;
        this.gameDto = gameDto;
    }

    public void stopThread(){
        flag = false;
    }

    public void run() {
        //这里对canvas的使用有误
        while(flag){
            try {
                synchronized (holder){
                System.out.println(gameDto.getMyTank().getTankBascInfo().getTankName());
                canvas=this.holder.lockCanvas();
                canvas.drawBitmap(gameDto.getMyTank().getTankPicture(),0,0,null);
                gameDto.getMyTank().drawSelf(canvas);
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
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}

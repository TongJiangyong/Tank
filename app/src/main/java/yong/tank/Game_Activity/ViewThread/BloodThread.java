package yong.tank.Game_Activity.ViewThread;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.SurfaceHolder;

import yong.tank.Dto.GameDto;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */
//TODO 重点：测试GSOM能否转换非规则数据，入bitmap等数据......
public class BloodThread implements Runnable {
    private boolean flag =true;
    private GameDto gameDto;
    private SurfaceHolder holder;
    private Canvas canvas;
    private static String TAG = "BloodThread";
    public BloodThread(GameDto gameDto, SurfaceHolder holder) {
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
                    canvas=this.holder.lockCanvas();
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
                    gameDto.getMyBlood().drawSelf(canvas);
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
                //让装填的过程更流畅
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}

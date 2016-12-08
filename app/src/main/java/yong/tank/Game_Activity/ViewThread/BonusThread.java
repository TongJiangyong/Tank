package yong.tank.Game_Activity.ViewThread;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
                    //Log.w(TAG,"TEST BonusThread");
                    /*******************关于bonus的使用方法*******************************/
                    //参考别人的代码，也是要设置路径，考虑一下.....
                    //考虑设置什么路径呢？
                    //随机的方向，走正选即可....
                    //在gameThread哪里做一个定时器，定时30s产生一个随机的bonus，并生成好一个带所有点的路径
                    //加入bonus的list
                    //在这里遍历这个list,然后绘制bonus，绘制方法与子弹一样，绘制完成后，记得将点的路径制空即可.....
                    //TODO 这里以后想想，交互的话，该怎么办？
                        //Log.d(TAG,gameDto.getMyTank().getTankBascInfo().getTankName());
                    canvas=this.holder.lockCanvas();
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
                    if(this.gameDto.getBonus()!=null){
                        this.gameDto.getBonus().drawSelf(canvas);
                    }
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
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}

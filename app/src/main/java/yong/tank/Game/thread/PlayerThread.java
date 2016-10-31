package yong.tank.Game.thread;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.SurfaceHolder;

import yong.tank.Dto.GameDto;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class PlayerThread implements Runnable {
    private boolean flag =true;
    private GameDto gameDto;
    private SurfaceHolder holder;
    private Canvas canvas;
    private static String TAG = "PlayerThread";

    /***************/
    private Paint paint;
    private int RockerCircleX = 100;
     private int RockerCircleY = 100;
     private int RockerCircleR = 50;
    private float SmallRockerCircleX = 100;
    private float SmallRockerCircleY = 100;
    private float SmallRockerCircleR = 20;
    /***************/

    public PlayerThread(GameDto gameDto, SurfaceHolder holder) {
        this.flag =true;
        this.holder = holder;
        this.gameDto = gameDto;
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void stopThread(){
        flag = false;
    }

    public void run() {
        //TODO 这里对canvas的使用有误,不能让所有的线程都使用canvas
        while(flag){
/*            try {
                synchronized (holder){
                    //Log.d(TAG,gameDto.getMyTank().getTankBascInfo().getTankName());
                    canvas=this.holder.lockCanvas();
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
                    //canvas.drawBitmap(gameDto.getMyTank().getTankPicture(),0,0,null);
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
            }*/
            draw();
            Log.d(TAG,"just a test");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    public void draw() {
        try {
            canvas=this.holder.lockCanvas();
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
            Paint paint =new Paint();
            //设置透明度
            paint.setColor(0x70000000);
            //绘制摇杆背景
            canvas.drawCircle(RockerCircleX, RockerCircleY, RockerCircleR, paint);
            paint.setColor(0x70ff0000);
            //绘制摇杆
            canvas.drawCircle(SmallRockerCircleX, SmallRockerCircleY,
                    SmallRockerCircleR, paint);
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            try {
                if (canvas != null)
                    this.holder.unlockCanvasAndPost(canvas);
            } catch (Exception e2) {
            }
        }
    }

}

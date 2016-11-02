package yong.tank.Game.thread;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.SurfaceHolder;

import yong.tank.Dto.GameDto;
import yong.tank.tool.StaticVariable;

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
    private int outsideCircle_x = StaticVariable.SCREEN_WIDTH*4/5;
    private int outsideCircle_y = StaticVariable.SCREEN_HEIGHT*3/4;
    private int outsideCircle_r = StaticVariable.SCREEN_WIDTH*1/10;
    private float insideCircle_x = StaticVariable.SCREEN_WIDTH*4/5;
    private float insideCircle_y = StaticVariable.SCREEN_HEIGHT*3/4;
    private float insideCircle_r = StaticVariable.SCREEN_WIDTH*1/20;
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
            try {
                canvas=this.holder.lockCanvas();
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
                this.gameDto.getPlayerPain().drawSelf(canvas);
                //Log.d(TAG,"test player....");
            } catch (Exception e) {
                // TODO: handle exception
            } finally {
                try {
                    if (canvas != null)
                        this.holder.unlockCanvasAndPost(canvas);
                } catch (Exception e2) {
                }
            }
            try {
                Thread.sleep(20);
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
            canvas.drawCircle(outsideCircle_x, outsideCircle_y, outsideCircle_r, paint);
            paint.setColor(0x70ff0000);
            //绘制摇杆
            canvas.drawCircle(insideCircle_x, insideCircle_y,
                    insideCircle_r, paint);
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

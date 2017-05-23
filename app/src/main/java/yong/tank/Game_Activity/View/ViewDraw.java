package yong.tank.Game_Activity.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import yong.tank.Dto.GameDto;

/**
 * Created by hasee on 2016/10/31.
 */

public class ViewDraw extends SurfaceView implements SurfaceHolder.Callback {

    public ViewDraw(Context context) {
        super(context);
    }

    public GameDto gameDto;
    public SurfaceHolder holder;
    public Canvas canvas;
    private List<ViewBase> views;
    public static String TAG = "ViewDraw";

    public ViewDraw(Context context, GameDto gameDto, List<ViewBase> views) {
        super(context);
        this.gameDto = gameDto;
        holder = this.getHolder(); //不加这个surfacecread不能启动....
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);  //设置holder为透明必须要加
        getHolder().addCallback(this); //不加的话，surfacehold会默认不启动，比较麻烦
        this.views = views;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    public void stopDrawFrame(){
        for (ViewBase v : views) {
            v.stopDrawFrame();
        }
    }
    public void drawFrame(float interpolation) {
        try {
            synchronized (holder) {
                canvas = this.holder.lockCanvas();
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制前先進行清空操作....
                for (ViewBase v : views) {
                    v.startDrawFrame(interpolation, canvas);
                }
            }
        } catch (Exception e) {
                e.printStackTrace();
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);//结束锁定画图，并提交改变。
            }
        }
    }


    //获取画笔
    public Canvas getUseAbleCanvas() {
        return this.holder.lockCanvas();
    }

    //释放画笔
    public void releaseUseAbleCanvas() {
        this.holder.unlockCanvasAndPost(canvas);
    }

}

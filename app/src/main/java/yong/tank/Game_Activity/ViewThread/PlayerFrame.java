package yong.tank.Game_Activity.ViewThread;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import yong.tank.Dto.GameDto;
import yong.tank.tool.StaticVariable;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class PlayerFrame {
    private boolean flag =true;
    private GameDto gameDto;
    private SurfaceHolder holder;
    private Canvas canvas;
    private static String TAG = "PlayerThread";

    /***************/
    private Paint paint;
    private int outsideCircle_x = StaticVariable.LOCAL_SCREEN_WIDTH *4/5;
    private int outsideCircle_y = StaticVariable.LOCAL_SCREEN_HEIGHT *3/4;
    private int outsideCircle_r = StaticVariable.LOCAL_SCREEN_WIDTH *1/10;
    private float insideCircle_x = StaticVariable.LOCAL_SCREEN_WIDTH *4/5;
    private float insideCircle_y = StaticVariable.LOCAL_SCREEN_HEIGHT *3/4;
    private float insideCircle_r = StaticVariable.LOCAL_SCREEN_WIDTH *1/20;
    /***************/

    public PlayerFrame(GameDto gameDto) {
        this.flag =true;
        this.gameDto = gameDto;
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void stopDrawing(){
        flag = false;
    }

    public void drawFrame(float interpolation,Canvas canvas) {
        //TODO 这里对canvas的使用有误,不能让所有的线程都使用canvas
        //Log.i(TAG,"PlayerThread START_1");
            try {
                //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
                if(flag) {
                    this.gameDto.getPlayerPain().drawSelf(canvas);
                }
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

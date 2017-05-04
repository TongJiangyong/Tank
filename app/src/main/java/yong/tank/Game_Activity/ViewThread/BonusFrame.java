package yong.tank.Game_Activity.ViewThread;

import android.graphics.Canvas;

import yong.tank.Dto.GameDto;

/**
 * Created by hasee on 2016/11/10.
 */

public class BonusFrame {
    private boolean flag = true;
    private GameDto gameDto;
    private Canvas canvas;
    private static String TAG = "BonusThread";

    public BonusFrame(GameDto gameDto) {
        this.flag = true;
        this.gameDto = gameDto;
    }

    public void stopDrawing() {
        flag = false;
    }

    public void drawFrame(float interpolation, Canvas canvas) {
        //TODO 这里对canvas的使用有误,不能让所有的线程都使用canvas
        try {
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
            //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
            if (this.gameDto.getBonus() != null) {
                this.gameDto.getBonus().drawSelf(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

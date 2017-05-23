package yong.tank.Game_Activity.ViewThread;

import android.graphics.Canvas;

import java.util.List;

import yong.tank.Dto.GameDto;
import yong.tank.modal.Explode;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/8.
 */

public class ExplodeFrame {
    private static String TAG = "ExplodeThread";
    private List<Explode> explodes;
    private boolean flag =true;
    private GameDto gameDto;
    public ExplodeFrame(GameDto gameDto) {
        this.flag =true;
        this.gameDto = gameDto;
    }


    public void drawFrame(float interpolation,Canvas canvas) {
            try{
                //Log.w(TAG,"explodeTest");
                //在这个爆炸线程中，绘制爆炸的方法为：
                //在这里获取所有的爆炸点list
                //遍历每一个爆炸点，进行绘制
                //在每一个点的绘制方法中，绘制当前的一幅图像，并让图像+1
                //绘制完以后，检查这个点是否绘制完全，如果绘制完全，则将这个点从list中删除即可
                //增加的地方有很多个，但是删除的地方只有这一个即可......
                //每一个爆炸点需要的属性： 位置（x,y）,爆炸图像等....
                    //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色 这句话不能缺，缺了好像就不能将原画删除
                    //gameDto.getBlood().drawSelf(canvas);
                if(flag){
                    explodes = this.gameDto.getExplodes();
                //TODO 这里很容易出问题，因为逻辑不应该在这里完成....
                    if (explodes.size() != 0) {
                        for (int i = (explodes.size() - 1); i >= 0; i--) {
                            if ((explodes.get(i).getExplodeType() == StaticVariable.EXPLODE_TYPE_GROUND &&
                                    explodes.get(i).getCurrentFrame() == (StaticVariable.EXPLODESPICTURE_GROUND.length)) ||
                                    (explodes.get(i).getExplodeType() == StaticVariable.EXPLODE_TYPE_TANK &&
                                            explodes.get(i).getCurrentFrame() == (StaticVariable.EXPLODESPICTURE_TANKE.length))) {
                                //绘制完成
                                explodes.remove(i);
                            } else {
                                //继续绘制
                                explodes.get(i).drawSelf(canvas);
                            }
                        }
                    }
                    }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

    }
    public void stopDrawing(){
        flag = false;
    }
}

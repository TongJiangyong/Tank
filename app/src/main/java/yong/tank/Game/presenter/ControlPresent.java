package yong.tank.Game.presenter;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import yong.tank.Dto.GameDto;
import yong.tank.Game.control.GameControler;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/1.
 */

public class ControlPresent {
    private Context context;
    private static String TAG ="ControlPresent";
    private GameDto gameDto;
    private GameControler gameControler;
    //TODO 定义一个最好去定义一个view，而不是这个.....
    public ControlPresent(Context context, GameDto gameDto, GameControler gameControler){
        this.context= context;
        this.gameDto=gameDto;
        this.gameControler=gameControler;
    }
    //TODO 这个没弄懂，看API弄懂一下.....
    //TODO，这个逻辑，应该写在control中.....
    public void setMotion(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_MOVE:
                    int pointerCount = event.getPointerCount();
                    for (int i = 0; i < pointerCount; i++) {
                        int id = event.getPointerId(i); //同一点的id值保持不变
                        int dx=0;
                        int dy=0;
                        if(id<pointerCount){
                            dx = (int) event.getX(id);
                            dy = (int) event.getY(id);
                        }else{break;}
                        if(this.gameDto.getPlayerPain().isInCircle(dx,dy)){
                            this.gameDto.getPlayerPain().setInsideCircle_x(dx);
                            this.gameDto.getPlayerPain().setInsideCircle_y(dy);
                            Log.w(TAG,"TEST");
                        }else{
                            Log.w(TAG,"TEST1");
                        }
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    int pointerCount_2 = event.getPointerCount();
                    Log.w(TAG,"up_1"+pointerCount_2);
                    int dx=(int)event.getX();
                    int dy=(int)event.getY();
                    //TODO 这里的逻辑还要想一下......
                    if(this.gameDto.getPlayerPain().isInCircle(dx,dy)){
                        this.gameDto.getPlayerPain().setInsideCircle_x(StaticVariable.SCREEN_WIDTH*4/5);
                        this.gameDto.getPlayerPain().setInsideCircle_y(StaticVariable.SCREEN_HEIGHT*3/4);
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    int pointerCount_3 = event.getActionIndex ();
                    Log.w(TAG,"up_2"+pointerCount_3);
                    //TODO 这里的逻辑还要想一下......
                    if(this.gameDto.getPlayerPain().isInCircle((int)event.getX(),(int)event.getY())){
                        this.gameDto.getPlayerPain().setInsideCircle_x(StaticVariable.SCREEN_WIDTH*4/5);
                        this.gameDto.getPlayerPain().setInsideCircle_y(StaticVariable.SCREEN_HEIGHT*3/4);
                    }
                    break;
                default:
                    break;
            }

        }



}

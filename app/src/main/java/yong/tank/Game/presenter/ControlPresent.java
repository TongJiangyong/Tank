package yong.tank.Game.presenter;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import yong.tank.Dto.GameDto;
import yong.tank.Game.control.GameControler;
import yong.tank.tool.StaticVariable;

import static android.icu.text.RelativeDateTimeFormatter.Direction.THIS;

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
            /**测试发现，只有最后第一个手指的down，才会触发这个**/
                case MotionEvent.ACTION_DOWN:
                    int pointerCount_1 = event.getPointerCount();
                    //Log.w(TAG,"ACTION_DOWN "+pointerCount_1);
                 /**测试发现，所有的down，都会触发这个**/
                case MotionEvent.ACTION_POINTER_DOWN:
                    int pointerCount_2 = event.getPointerCount();
                    //Log.w(TAG,"ACTION_POINTER_DOWN "+pointerCount_2);
                case MotionEvent.ACTION_MOVE:
                    //TODO 这里的move以后做一个做一个技巧性设定，即，在坦克周围一定范围内的，才能被选中，然后预备弹射.....
                    int pointerCount_3 = event.getPointerCount();
                    //Log.w(TAG,"ACTION_MOVE "+pointerCount_3);
                    for (int i = 0; i < pointerCount_3; i++) {
                        int id = event.getPointerId(i); //同一点的id值保持不变
                        int dx=0;
                        int dy=0;
                        if(id<pointerCount_3){
                            dx = (int) event.getX(id);
                            dy = (int) event.getY(id);
                        }else{break;}
                        //TODO 这里以后做一个技巧性的设定，将跳出框弄大一点叫，以免很快移动就退出选择框 ，这里是坦克移动
                        if(this.gameDto.getPlayerPain().isInCircle(dx,dy)){
                            this.gameDto.getPlayerPain().setInsideCircle_x(dx);
                            this.gameDto.getPlayerPain().setInsideCircle_y(dy);
                            //TODO 坦克移动 坦克移动即传递方向即可
                            this.gameDto.getMyTank().move(this.gameDto.getPlayerPain().setTankDirectiron(dx));
                            //Log.w(TAG,"tankmove");
                        }else{
                            //Log.w(TAG,"TEST1");
                        }
                        //TODO 这里是坦克的发子弹，发子弹的基本原理是：捕捉在坦克周围一定半径内的移动即可.....并捕捉在坦克周围一定半径的释放动作
                        //首先设置
                        if(this.gameDto.getMyTank().isInCircle(dx,dy)){
                            //TODO 传递Fire的位置和角度，enableFire可不在这儿
                            //this.gameDto.getMyTank().enableFire();
                            //this.gameDto.getMyTank().myTankFire();
                            //Log.w(TAG,"TEST");
                        }else{
                           // Log.w(TAG,"TEST1");
                        }
                    }
                    //TODO 这里设置坦克的弹出
                    int testFlag=0;
                    for (int i = 0; i < pointerCount_3; i++) {
                        int id = event.getPointerId(i); //同一点的id值保持不变
                        int dx=0;
                        int dy=0;
                        if(id<pointerCount_3){
                            dx = (int) event.getX(id);
                            dy = (int) event.getY(id);
                        }else{
                            break;
                        }
                        if(this.gameDto.getPlayerPain().isInCircle(dx,dy)){
                           // Log.w(TAG,"ACTION_MOVE pointerCount_3_1:"+pointerCount_3);
                            //Log.w(TAG,"ACTION_MOVE testFlag_1:"+testFlag);
                        }else{
                            testFlag++;
                           // Log.w(TAG,"ACTION_MOVE pointerCount_3:"+pointerCount_3);
                            //Log.w(TAG,"ACTION_MOVE testFlag:"+testFlag);
                            if(testFlag==pointerCount_3){
                                this.gameDto.getPlayerPain().setInsideCircle_x(StaticVariable.SCREEN_WIDTH*4/5);
                                this.gameDto.getPlayerPain().setInsideCircle_y(StaticVariable.SCREEN_HEIGHT*3/4);
                                this.gameDto.getMyTank().move(StaticVariable.TANKESTOP);
                            }

                        }


                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    /**测试发现，只有最后一个手指的up，才会触发这个**/
                case MotionEvent.ACTION_UP:
                    int pointerCount_4 = event.getPointerCount();
                    int index_1=event.getActionIndex();
                    //Log.w(TAG,"ACTION_UP getX:"+event.getX(index_1));
                    //Log.w(TAG,"ACTION_UP "+pointerCount_4);
                    int dx=(int)event.getX(index_1);
                    int dy=(int)event.getY(index_1);
                    //TODO 这里的逻辑还要想一下......
                    if(this.gameDto.getPlayerPain().isInCircle(dx,dy)){
                        this.gameDto.getPlayerPain().setInsideCircle_x(StaticVariable.SCREEN_WIDTH*4/5);
                        this.gameDto.getPlayerPain().setInsideCircle_y(StaticVariable.SCREEN_HEIGHT*3/4);
                        this.gameDto.getMyTank().move(StaticVariable.TANKESTOP);
                    }
                    break;
            /**测试发现，只有除了最后一个手指的up，都会会触发这个**/
                case MotionEvent.ACTION_POINTER_UP:
                    int pointerCount_5 = event.getPointerCount();
                    int index_2=event.getActionIndex();
                   // Log.w(TAG,"ACTION_POINTER_UP getX:"+event.getX(index_2));
                    //Log.w(TAG,"ACTION_POINTER_UP "+pointerCount_5);
                    int dx_x=(int)event.getX(index_2);
                    int dy_y=(int)event.getY(index_2);
                    //TODO 如果up的那个手指在圆圈内，则跳出
                    if(this.gameDto.getPlayerPain().isInCircle(dx_x,dy_y)){
                        this.gameDto.getPlayerPain().setInsideCircle_x(StaticVariable.SCREEN_WIDTH*4/5);
                        this.gameDto.getPlayerPain().setInsideCircle_y(StaticVariable.SCREEN_HEIGHT*3/4);
                        this.gameDto.getMyTank().move(StaticVariable.TANKESTOP);
                    }

                    //TODO 这里的逻辑还要想一下......
                    break;
                default:
                    break;
            }

        }



}

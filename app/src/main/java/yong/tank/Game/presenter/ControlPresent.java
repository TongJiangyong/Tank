package yong.tank.Game.presenter;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import yong.tank.Dto.GameDto;
import yong.tank.Game.control.GameControler;
import yong.tank.modal.Point;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/1.
 */

public class ControlPresent {
    private Context context;
    private static String TAG ="ControlPresent";
    private GameDto gameDto;
    private GameControler gameControler;
    private Point startPoint=new Point();
    private Point releasePoint=new Point();
    //坦克的角度
    private int tankDegree=0;
    //距离
    private int distance = 0;

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
                    int index_3=event.getActionIndex();
                    //TODO 设置发射按下
                    int dx_tmp =(int)event.getX(index_3);
                    int dy_tmp = (int)event.getY(index_3);
                        if(this.gameDto.getMyTank().isInCircle(dx_tmp,dy_tmp)){
                            startPoint.setX(dx_tmp);
                            startPoint.setX(dy_tmp);
                            startPoint.setPointNotNull();
                            Log.w(TAG,"TEST_setStartPoint");
                        }
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
                    }

                    //停止坦克
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
                    //TODO 设置炮管的方向
                    for (int i = 0; i < pointerCount_3; i++) {
                        int id = event.getPointerId(i); //同一点的id值保持不变
                        int dx=0;
                        int dy=0;
                        if(id<pointerCount_3){
                            dx = (int) event.getX(id);
                            dy = (int) event.getY(id);
                        }else{break;}
                        //这里判断是否处于开火区域
                        if(this.gameDto.getMyTank().isInFireCircle(dx,dy)&&!startPoint.isPointNull()){
                            releasePoint.setX(dx);
                            releasePoint.setX(dy);
                            releasePoint.setPointNotNull();
                            //TODO 计算角度和距离，用于设计炮弹曲线
                            countBulletPath(this.gameDto.getMyTank().getTankCenter(),dx,dy);
                            //没办法，传入负值才行...
                            this.gameDto.getMyTank().weaponMove(-tankDegree);
                            //Log.w(TAG,"tankmove");
                        }
                        //这里判断是否处于释放区域
                        if(this.gameDto.getMyTank().isOutFireCircle(dx,dy)){
                            releasePoint.setPointNull();
                            startPoint.setPointNull();
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
                    //TODO 这里设置坦克释放炮弹的方法....
                    //这里判断是否处于开火区域
                    if(this.gameDto.getMyTank().isInFireCircle(dx,dy)&&!startPoint.isPointNull()&&!releasePoint.isPointNull()){
                        //this.gameDto.getMyTank().tankFire();
                        startPoint.setPointNull();
                        releasePoint.setPointNull();
                        this.gameDto.getMyTank().bulletFire(tankDegree,distance);
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
                    //TODO 这里设置坦克释放炮弹的方法....
                    //这里判断是否处于开火区域
                    if(this.gameDto.getMyTank().isInFireCircle(dx_x,dy_y)&&!startPoint.isPointNull()&&!releasePoint.isPointNull()){
                        //this.gameDto.getMyTank().tankFire();
                        startPoint.setPointNull();
                        releasePoint.setPointNull();
                        this.gameDto.getMyTank().bulletFire(tankDegree,distance);
                    }

                    break;
                default:
                    break;
            }

        }

    private void countBulletPath(Point tankCenter, int dx, int dy) {
        double test = (double)Math.abs(dy-tankCenter.getY())/(double)Math.abs(dx-tankCenter.getX());
        Log.w(TAG,"test:"+test);
        tankDegree=(int)Math.toDegrees(test);

        distance=(int)Math.sqrt((dy-tankCenter.getY())*(dy-tankCenter.getY())+(dx-tankCenter.getX())*(dx-tankCenter.getX()));
        Log.w(TAG,"tankDegree:"+tankDegree+" distance:"+distance);
    }


}

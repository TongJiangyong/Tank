package yong.tank.Game_Activity.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import yong.tank.Dto.GameDto;
import yong.tank.R;
import yong.tank.modal.Bullet;
import yong.tank.modal.Point;
import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

/**
 * Created by hasee on 2016/11/1.
 */

public class PlayControler {
    private Context context;
    private static String TAG ="PlayControler";
    private GameDto gameDto;
    private GameControler gameControler;
    private Point startPoint=new Point();
    private Point releasePoint=new Point();
    //坦克的角度
    private int tankDegree=0;
    //距离
    private double distance = 0;

    //TODO 定义一个最好去定义一个view，而不是这个.....
    public PlayControler(Context context, GameDto gameDto, GameControler gameControler){
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
                    //int pointerCount_1 = event.getPointerCount();
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
                            //Log.w(TAG,"TEST_setStartPoint");
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
                            //Log.w(TAG,"ACTION_MOVE pointerCount_3_1:"+pointerCount_3);
                        }else{
                            testFlag++;
                            //Log.w(TAG,"ACTION_MOVE pointerCount_3:"+pointerCount_3);
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
                        }else{
                            break;
                        }
                        //这里判断是否处于开火区域
                        if(this.gameDto.getMyTank().isInFireCircle(dx,dy)&&!startPoint.isPointNull()){
                            releasePoint.setX(dx);
                            releasePoint.setX(dy);
                            releasePoint.setPointNotNull();
                            //TODO 计算角度和距离，用于设计炮弹曲线
                            countBulletPath(this.gameDto.getMyTank().getTankCenter(),dx,dy);
                            //TODO 没办法，传入负值才行...,所以在setting函数中设为负数了
                            this.gameDto.getMyTank().weaponMove(tankDegree);
                            this.gameDto.getMyTank().setPreFirePath(this.getBulletPath(this.gameDto.getMyTank().getWeaponPoxition_x(),
                                                                                        this.gameDto.getMyTank().getWeaponPoxition_y(),
                                                                                        distance,
                                                                                        tankDegree,
                                                                                        true));
                            //这一段代码加了，可以在绘制出第三象限时，停止prefire
                            //测试发现，加了以后，游戏性很差......
/*                            if(!this.gameDto.getMyTank().isOutDirection(dx,dy)){
                                releasePoint.setPointNull();
                                startPoint.setPointNull();
                                //停止prePath的绘制
                                this.gameDto.getMyTank().setPreFirePath(null);
                            }*/
                        }
                        //这里判断是否处于释放区域,或者社稷位置不对.....
                        if(this.gameDto.getMyTank().isOutFireCircle(dx,dy)){
                            releasePoint.setPointNull();
                            startPoint.setPointNull();
                            //停止prePath的绘制
                            this.gameDto.getMyTank().setPreFirePath(null);
                        }

                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    /**测试发现，只有最后一个手指的up，才会触发这个**/
                case MotionEvent.ACTION_UP:
                    this.playerRelease(event);
                    break;
            /**测试发现，只有除了最后一个手指的up，都会会触发这个**/
                case MotionEvent.ACTION_POINTER_UP:
                    this.playerRelease(event);
                    break;
                default:
                    break;
            }
        }

    private void countBulletPath(Point tankCenter, int dx, int dy) {
        double test = (double)Math.abs(dy-tankCenter.getY())/(double)Math.abs(dx-tankCenter.getX());
        tankDegree=(int)Math.toDegrees(Math.atan (test));
        distance=Math.sqrt((dy-tankCenter.getY())*(dy-tankCenter.getY())+(dx-tankCenter.getX())*(dx-tankCenter.getX()))/
                (this.gameDto.getMyTank().getTankPicture().getWidth()*1.4);
        //Log.w(TAG,"tankDegree:"+tankDegree+" distance:"+distance);
    }




    //手指释放调用的方法
    private void playerRelease(MotionEvent event) {
        int index_2 = event.getActionIndex();
        int dx = (int) event.getX(index_2);
        int dy = (int) event.getY(index_2);
        //TODO 如果up的那个手指在圆圈内，则跳出
        if (this.gameDto.getPlayerPain().isInCircle(dx, dy)) {
            this.gameDto.getPlayerPain().setInsideCircle_x(StaticVariable.SCREEN_WIDTH * 4 / 5);
            this.gameDto.getPlayerPain().setInsideCircle_y(StaticVariable.SCREEN_HEIGHT * 3 / 4);
            this.gameDto.getMyTank().move(StaticVariable.TANKESTOP);
        }
        //TODO 这里设置坦克释放炮弹的方法....
        //这里判断是否处于开火区域
        if (this.gameDto.getMyTank().isInFireCircle(dx, dy) && !startPoint.isPointNull() && !releasePoint.isPointNull()) {
            //释放各个point
            startPoint.setPointNull();
            releasePoint.setPointNull();
            //释放previewPath
            this.gameDto.getMyTank().setPreFirePath(null);
            //TODO 在palyerControler中加入逻辑，然后进行判断.....
            //设置使能发射
            this.gameDto.getMyTank().setFireAction(true);
            //装载子弹并发射
            if(this.gameDto.getMyTank().getEnableFire()&&
                    this.gameDto.getBlood().getAllowFire()&&
                    this.gameDto.getMyTank().getSelectedBulletsNum()>0){
                //停止prePath的绘制
                this.gameDto.getMyTank().setPreFirePath(null);
                tankOnFire();
            }
            this.gameDto.getMyTank().setFireAction(false);
            //增加子弹为0的提醒
            if(this.gameDto.getMyTank().getSelectedBulletsNum()==0){
                Toast.makeText(this.context.getApplicationContext(), "当前子弹数量为0！", Toast.LENGTH_SHORT).show();// 显示时间较短
            }
        }
    }

        //1/发射子弹，2、并重置装填的时间，3、更新bullet的计数
        public void tankOnFire(){
            //***************发射子弹*************
            Bullet bullet = initBullet(this.gameDto.getMyTank().getSelectedBullets());
            //在tank中加入子弹
            this.gameDto.getMyTank().addBuleetFire(bullet);
            //***************重置装填的时间*************
            //如果子弹的类型不是连续弹，则设置装填时间
            if(this.gameDto.getMyTank().getSelectedBullets()==StaticVariable.S_S)
            {
                this.gameDto.getBlood().setPowerNum(1);
            }else{
                this.gameDto.getBlood().setPowerNum(0);
                //设置禁止发射,直到装填时间回复
                this.gameDto.getBlood().setAllowFire(false);
            }
            //***************更新bullet的计数*************
            //如果当前的子弹类型不为初始类型，则需要更新计数
            if(gameDto.getMyTank().getSelectedBullets()!=StaticVariable.ORIGIN){
                //子弹计数
                gameDto.getSelectButtons().get(R.id.selectButton_2).subtractBulletNum();
                this.gameDto.getMyTank().setSelectedBulletsNum(gameDto.getSelectButtons().get(R.id.selectButton_2).getBulletNum());
            }
        }


    //初始化子弹
    private Bullet initBullet(int bulletType){
        Bitmap bullet_temp = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.BUTTLE_BASCINFOS[bulletType].getPicture());
        Bitmap bulletPicture = Tool.reBuildImg(bullet_temp,0,1,1,false,false);
        Bullet bullet = new Bullet(bulletPicture,StaticVariable.BUTTLE_BASCINFOS[bulletType]);
        //初始化坦克的性能
        bullet.setBulletDegree(tankDegree);
        bullet.setBulletDistance(distance);
        //计算并初始化子弹的路径
        bullet.setFirePath(this.getBulletPath(this.gameDto.getMyTank().getWeaponPoxition_x(),
                                            this.gameDto.getMyTank().getWeaponPoxition_y(),
                                            distance,
                                            tankDegree,
                                            false));
        //允许绘制路径
        bullet.setDrawFlag(true);
        //初始化坦克的位置
        //bullet.setBulletPosition_x(this.gameDto.getMyTank().getWeaponPoxition_x());
        //bullet.setBulletPosition_y(this.gameDto.getMyTank().getWeaponPoxition_y());
        return bullet;
    }



    //将这个抽象为函数，然后调用....
    //路径计算好以后，怎么给子弹
    //写成一个函数，然后计算返回一系列的点和角度即可.....List<Point>
    private List<Point> getBulletPath(int init_x,int init_y,double bulletDistance,int bulletDegree,boolean isPreView) {
        //这里关联speed和distance，暂时不处理
        List<Point> bulletPath = new ArrayList<Point>();

        double bulletV_x=StaticVariable.BUTTLE_BASCINFOS[this.gameDto.getMyTank().getSelectedBullets()].getSpeed()*bulletDistance*Math.cos(Math.toRadians(bulletDegree));
        double bulletV_y=-(StaticVariable.BUTTLE_BASCINFOS[this.gameDto.getMyTank().getSelectedBullets()].getSpeed()*bulletDistance*Math.sin(Math.toRadians(bulletDegree)));
        int pathNum = 0;
        if (isPreView) {
            pathNum = StaticVariable.PREVIEWPATHLENGTH;
        } else {
            pathNum = StaticVariable.PATHLENGTH;
        }
        for (int i = 0; i < pathNum; i++) {
            //这里计算时，采用向下为正，向右为正的方法
            bulletV_y = bulletV_y + StaticVariable.GRAVITY * StaticVariable.INTERVAL_TIME;
            int newPosition_x = (int) (init_x + bulletV_x * StaticVariable.INTERVAL_TIME);
            //bulletPosition_x+=v_x*t;
            int newPosition_y = (int) (init_y + (bulletV_y * StaticVariable.INTERVAL_TIME + StaticVariable.GRAVITY * StaticVariable.INTERVAL_TIME * StaticVariable.INTERVAL_TIME / 2));
            //bulletPosition_y+=v_y*t-g*t*t/2;
            double test = bulletV_y / bulletV_x;
            bulletDegree = (int) Math.toDegrees(Math.atan(test));
            Point point = new Point(init_x,init_y, bulletDegree,false);
            bulletPath.add(point);
            init_x=newPosition_x;
            init_y=newPosition_y;
            //Log.w(TAG, "bulletV_x:" + bulletV_x + " bulletV_y:" + bulletV_y);
            //Log.w(TAG, "bulletDegree:" + bulletDegree + "bulletDistance:" + bulletDistance + " bulletPosition_x:" + init_x + " bulletPosition_y:" + init_y);
            //time = time + StaticVariable.INTERVAL;
        }
        return bulletPath;
    }

    //主要动作为：1、更换selectView的外观 2 、设置mytank的当前子弹类型
    public void setClick(View view) {
        switch (view.getId()) {
            //***************更换selectView的外观***************
            //这种配置方法并不好，不过设置一下也是可以的.....
            case R.id.selectButton_2:
                Log.w(TAG,"selectButton_2");
                //当选择时，如果为空则不能选上，如果不为空，则选上，并设置tank的子弹为选上的子弹
                if(this.gameDto.getSelectButtons().get(R.id.selectButton_2).isFilled()){
                    this.gameDto.getSelectButtons().get(R.id.selectButton_2).setButtonSelected();
                    this.gameDto.getSelectButtons().get(R.id.selectButton_1).setButtonNoSelected();
                    //***************设置mytank的当前子弹类型和数量***************
                    this.gameDto.getMyTank().setSelectedBullets(gameDto.getSelectButtons().get(R.id.selectButton_2).getBulletType());
                    this.gameDto.getMyTank().setSelectedBulletsNum(gameDto.getSelectButtons().get(R.id.selectButton_2).getBulletNum());
                }
                break;
            case R.id.selectButton_1:
                Log.w(TAG,"selectButton_1");
                if(this.gameDto.getSelectButtons().get(R.id.selectButton_1).isFilled()){
                    this.gameDto.getSelectButtons().get(R.id.selectButton_1).setButtonSelected();
                    this.gameDto.getSelectButtons().get(R.id.selectButton_2).setButtonNoSelected();
                    this.gameDto.getMyTank().setSelectedBullets(gameDto.getSelectButtons().get(R.id.selectButton_1).getBulletType());
                    this.gameDto.getMyTank().setSelectedBulletsNum(gameDto.getSelectButtons().get(R.id.selectButton_1).getBulletNum());
                }
                break;
            default:
                break;
        }
    }
}
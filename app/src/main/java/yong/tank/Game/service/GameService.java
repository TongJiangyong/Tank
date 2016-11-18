package yong.tank.Game.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import yong.tank.Dto.GameDto;
import yong.tank.modal.Bonus;
import yong.tank.modal.Explode;
import yong.tank.modal.Point;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/10.
 */

public class GameService {
    private GameDto gameDto;
    private boolean gameStateFlag=false;
    private static String TAG ="GameService";
    private GameThread gameThread;
    private Context context;
    private Timer timer;
    public GameService(GameDto gameDto,Context context) {
        this.gameDto = gameDto;
        this.context = context;
    }

    public void gameStart(){
        if(gameThread==null){
            gameThread= new GameThread();
            Thread thread = new Thread(gameThread);
            thread.start();
            //启动bonus的线程
            this.startMakeBonus();
        }else{
            Log.w(TAG,"gameThread is not null");
        }
    }


    public void startMakeBonus(){
        timer = new Timer();
        BonusMaker bonusMaker = new BonusMaker();
        //schedule(TimerTask task, long delay, long period)
        //等待试试10s后开始调度，每隔10s产生一个
        Log.w(TAG,"bonus start to maker");
        timer.schedule(bonusMaker,5000,10000);
    }
    public void stopMakeBonus(){
        timer.cancel();
    }

    public void gameStop(){
        if(gameThread!=null){
            gameThread.gameThreadStop();
            //TODO 这里设置一个游戏结束标识符，进行判断,然后置为null
        }else{
            Log.w(TAG,"gameThread is  null");
        }
    }

    class GameThread implements Runnable {

        private boolean threadFlag=true;

        @Override
        public void run() {
            while(threadFlag){
                //TODO 测试explode
                gameDto.getMyTank().setEnableFire(true);
                    int num=gameDto.getMyTank().getBulletsFire().size();
                    if(num!=0){
                        for(int i=(num-1);i>=0;i--){
                            //如果打中地面
                            if(gameDto.getMyTank().getBulletsFire().get(i).getBulletPosition_y()> StaticVariable.SCREEN_HEIGHT/4*3){
                                Explode explode = new Explode(StaticVariable.EXPLODESONGROND,
                                        gameDto.getMyTank().getBulletsFire().get(i).getBulletPosition_x(),
                                        gameDto.getMyTank().getBulletsFire().get(i).getBulletPosition_y(),
                                        StaticVariable.EXPLODE_TYPE_GROUND);
                                gameDto.getMyTank().getBulletsFire().remove(i);//移除子弹
                                gameDto.getExplodes().add(explode);//发生爆炸
                                //如果打中坦克
                            }else if(gameDto.getMyTank().getBulletsFire().get(i).getBulletPosition_x()> StaticVariable.SCREEN_WIDTH/4*3){
                                Explode explode = new Explode(StaticVariable.EXPLODESONTANK,
                                        gameDto.getMyTank().getBulletsFire().get(i).getBulletPosition_x(),
                                        gameDto.getMyTank().getBulletsFire().get(i).getBulletPosition_y(),
                                        StaticVariable.EXPLODE_TYPE_TANK);
                                gameDto.getMyTank().getBulletsFire().remove(i);//移除子弹
                                gameDto.getExplodes().add(explode);//发生爆炸
                            }
                        }
                    }
                try {
                    //逻辑用的时间短一点....
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void gameThreadStop() {
            this.threadFlag = true;
        }

    }

    //产生bonus的线程
    //这里注意，一定要确定，bonus会被直接用完
    public class BonusMaker extends TimerTask {

        @Override
        public void run() {
            //获取bonus的路径
            Log.w(TAG,"********************************产生一个bonus****************************");
            //随机产生一个bonus，注意这里的bonus和子弹是绑定的
            int bonusType =StaticVariable.BONUSPICTURE[new Random().nextInt(StaticVariable.BONUSPICTURE.length)];
            Bitmap bonusPicture = BitmapFactory.decodeResource(context.getResources(),bonusType);//0~length-1之间的数
            List<Point> bonusPath =getBonusPath(bonusPicture);
            Bonus bonus = new Bonus(bonusPicture,bonusPath,bonusType);
            //设置bonus
            gameDto.setBonus(bonus);
        }
    }

    //计算bonus的路径
    private List<Point> getBonusPath(Bitmap bonusPicture) {
        //这里关联speed和distance，暂时不处理
        List<Point> bulletPath = new ArrayList<>();
        int direction =new Random().nextInt(2); //生成随机方向
        int bonus_x; //这里应该等于bonuspicture的宽度
        int bonus_y_init = StaticVariable.SCREEN_HEIGHT/StaticVariable.BONUS_Y;  //初始为1/5处的地方
        int bonus_y;
        int speed=StaticVariable.BONUS_SPEED;
        //TODO 振幅为图片的宽度乘以比例
        int scale = bonusPicture.getHeight();
        if(direction==0){
            bonus_x=0;
        }else{
            bonus_x= StaticVariable.SCREEN_WIDTH;
            speed = -speed;
        }
        while(bonus_x>=0&&bonus_x<=StaticVariable.SCREEN_WIDTH){
            bonus_x=bonus_x+speed;
            //注意这里除法是易错点
            bonus_y=bonus_y_init +(int)(Math.sin((double)bonus_x/StaticVariable.BONUS_STEP)*scale);
            Point point = new Point(bonus_x,bonus_y,0,false);
            bulletPath.add(point);
        }
        return bulletPath;
    }
}

package yong.tank.Game.service;

import android.util.Log;

import yong.tank.Dto.GameDto;
import yong.tank.modal.Explode;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/10.
 */

public class GameService {
    private GameDto gameDto;
    private boolean gameStateFlag=false;
    private static String TAG ="GameService";
    private GameThread gameThread;
    public GameService(GameDto gameDto) {
        this.gameDto = gameDto;
    }

    public void gameStart(){
        if(gameThread==null){
            gameThread= new GameThread();
            Thread thread = new Thread(gameThread);
            thread.start();
        }else{
            Log.w(TAG,"gameThread is not null");
        }
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

}

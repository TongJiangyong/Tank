package yong.tank.Game.service;

import android.util.Log;

import yong.tank.Dto.GameDto;

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
                Log.w(TAG,"gameThread。。。。RUNNING");
                try {
                    Thread.sleep(40);
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

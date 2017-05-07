package yong.tank.Game_Activity.control;

import android.content.Context;
import android.util.Log;

import java.util.List;

import yong.tank.Communicate.InterfaceGroup.ClientCommunicate;
import yong.tank.Game_Activity.View.ViewBase;
import yong.tank.Game_Activity.View.ViewDraw;
import yong.tank.Game_Activity.service.GameService;
import yong.tank.tool.StaticVariable;

import static yong.tank.tool.Tool.getCurrentTimeCount;

/**
 * Created by hasee on 2016/11/1.
 * 这个类非常重要，在这个类中，组合所有的模块，并控制（service）game中的所有逻辑，然后，将逻辑结果传给modal即可......
 */

public class GameControler {
    private GameService gameService;
    private static String TAG ="GameControler";
    private Context context;
    //游戏主程序的flag
    private boolean gameStartFlag = false;
    //游戏的时间控制：
    private long gameTimeCount = 0;
    //游戏跳过帧数：
    private long gameLoop = 0;

    //游戏渲染插值
    private float interpolation = 0;
    //需要绘制的图像
    private List<ViewBase> views;
    //画笔
    private ViewDraw viewDraw;
    ClientCommunicate clientCommunicate;
    //注入部分包括 service data communicate三部分内容
    public GameControler(GameService gameService, Context context, ViewDraw viewDraw) {
        this.gameService = gameService;
        this.context =  context;
        this.viewDraw = viewDraw;
    }

    //开启gameService的线程
    //这里将作为游戏界面的入口地址
    public void startGame() {
        GameThread gameThread = new GameThread();
        new Thread(gameThread).start();
    }

    //TODO 不用线程控制，采用其他的停止方式
    public void stopGame(){
        //启动关闭程序的界面
        for(ViewBase v: this.views){
            //以后再开启
            v.stopDrawFrame();
        }
        //停止程序的逻辑
        this.gameService.gameStop();
        //恢复初始化变量
        StaticVariable.remotePrepareInitFlag=false;
    }

    public GameService getGameService() {
        return gameService;
    }

    class GameThread implements  Runnable{

        @Override
        public void run() {
            gameStartFlag = true;
            gameTimeCount = getCurrentTimeCount();
            Log.i(TAG,"************************into restart Game*************************");
            while (gameStartFlag) {
                gameLoop = 0;
                while (getCurrentTimeCount() > gameTimeCount && gameLoop < StaticVariable.MAX_FRAMESKIP){
                    //启动程序的逻辑 ，逻辑设定为25帧，更新游戏数据
                    //一般情况下，执行完这个logicalUpdate，不到1毫秒
                    long before = getCurrentTimeCount();
                    gameService.logicalUpdate();
                    long after = getCurrentTimeCount();
                    gameTimeCount+=StaticVariable.SKIP_TICKS;
                    gameLoop++;
                    Log.i(TAG,"******************getCurrentTimeCount() is +"+getCurrentTimeCount()+",gameTimeCount ："+gameTimeCount+",gameLoop:"+gameLoop+" and  cost time is :"+(after-before)+"*************************");
                }
                //TODO 如果有必要，计算一个插值的系数....系数在0~1之间
                interpolation = (float)( getCurrentTimeCount() + StaticVariable.SKIP_TICKS - gameTimeCount ) / (float)( StaticVariable.SKIP_TICKS );
                Log.i(TAG,"interpolation is:"+ interpolation);
                long before_2 = getCurrentTimeCount();
                viewDraw.drawFrame(interpolation);
                long after_2 = getCurrentTimeCount();
                Log.i(TAG,"******************渲染帧数："+1000/(after_2-before_2)+"*************************");
            }

        }
    }
}

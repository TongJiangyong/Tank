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
        StaticVariable.REMOTE_PREPARED_INIT_FLAG =false;
    }

    public GameService getGameService() {
        return gameService;
    }

    class GameThread implements  Runnable{

        @Override
        public void run() {
            gameStartFlag = true;
            gameTimeCount = getCurrentTimeCount();
            long previousLogicalTimeCount = 0;
            long currentLogicalTimeCount = gameTimeCount;
            long previousFrameTimeCount = gameTimeCount;
            long currentFrameTimeCount = gameTimeCount;
            Log.i(TAG,"************************into restart Game*************************");
            while (gameStartFlag) {
                gameLoop = 0;
                while (getCurrentTimeCount() >= gameTimeCount && gameLoop < StaticVariable.MAX_FRAMESKIP){
                    //启动程序的逻辑 ，逻辑设定为25帧，更新游戏数据
                    //一般情况下，执行完这个logicalUpdate，不到1毫秒
                    currentLogicalTimeCount = getCurrentTimeCount();
                    //dValue = StaticVariable.SKIP_TICKS - (currentGameTimeCount - previousGameTimeCount-StaticVariable.SKIP_TICKS);


                    long before = getCurrentTimeCount();
                    gameService.logicalUpdate();
                    //viewDraw.drawFrame(interpolation);
                    long after = getCurrentTimeCount();
                    //gameTimeCount+=(dValue);
                    gameTimeCount+=StaticVariable.SKIP_TICKS;
                    gameLoop++;
                    long count = currentLogicalTimeCount-previousLogicalTimeCount;
                    if(count ==0){
                        count =1;
                    }
                    //Log.i(TAG,"********Logical Frame is: "+1000/count+"**********Time cost  is "+(currentLogicalTimeCount-previousLogicalTimeCount)+" and  current duration is :"+(after-before)+"*************************"+",gameTimeCount ："+gameTimeCount+",gameLoop:"+gameLoop);
                    //previousLogicalTimeCount = currentLogicalTimeCount;
                }
                //TODO 如果有必要，计算一个插值的系数....系数在0~1之间
                interpolation = (float)( getCurrentTimeCount() + StaticVariable.SKIP_TICKS - gameTimeCount ) / (float)( StaticVariable.SKIP_TICKS );
                //Log.i(TAG,"interpolation is:"+ interpolation);
                currentFrameTimeCount = getCurrentTimeCount();
                //这里处理有一点例外，因为设置的子弹速度很快，所以是按frame频率走的，所以这么做虽然不好，但是也只能这样了....
                if(gameService.getLocalGameProcess()!=null){
                    gameService.getLocalGameProcess().tankBulletShot();
                }
                viewDraw.drawFrame(interpolation);
                long after = getCurrentTimeCount();
                Log.i(TAG,"******************draw Frame："+1000/(after-currentFrameTimeCount)+"******current duration  is "+(currentLogicalTimeCount-previousFrameTimeCount)+"*******************");
                previousFrameTimeCount = currentFrameTimeCount;
            }

        }
    }
}

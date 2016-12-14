package yong.tank.Game_Activity.control;

import android.content.Context;

import java.util.List;

import yong.tank.Communicate.InterfaceGroup.ClientCommunicate;
import yong.tank.Game_Activity.View.ViewBase;
import yong.tank.Game_Activity.service.GameService;

/**
 * Created by hasee on 2016/11/1.
 * 这个类非常重要，在这个类中，组合所有的模块，并控制（service）game中的所有逻辑，然后，将逻辑结果传给modal即可......
 */

public class GameControler {
    private GameService gameService;
    private static String TAG ="GameControler";
    private Context context;
    private List<ViewBase> views;
    ClientCommunicate clientCommunicate;
    //注入部分包括 service data communicate三部分内容
    public GameControler(GameService gameService, Context context, List<ViewBase> views) {
        this.gameService = gameService;
        this.context =  context;
        this.views = views;
    }

    //开启gameService的线程
    public void startGame(){
        //TODO 初始化完成后的工作
        //启动程序的界面
        for(ViewBase v: this.views){
            v.startThread();
        }
        //启动程序的逻辑
        this.gameService.gameStart();
    }

    //关闭所有的线程
    public void stopGame(){
        //启动关闭程序的界面
        for(ViewBase v: this.views){
            //以后再开启
            //v.stopThread();
        }
        //停止程序的逻辑
        //this.gameService.gameStop();
    }

    public GameService getGameService() {
        return gameService;
    }
}

package yong.tank.Game.control;

import android.content.Context;

import yong.tank.Communicate.InterfaceGroup.ClientCommunicate;
import yong.tank.Communicate.InternetCommunicate.ClentInternet;
import yong.tank.Game.service.GameService;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/1.
 * 这个类非常重要，在这个类中，控制（service）game中的所有逻辑，然后，将逻辑结果传给modal即可......
 */

public class GameControler {
    private GameService gameService;
    private static String TAG ="GameControler";
    private Context context;
    ClientCommunicate clientCommunicate;
    //注入部分包括 service data communicate三部分内容
    public GameControler(GameService gameService,Context context) {
        this.gameService = gameService;
        this.context =  context;
        if(StaticVariable.CHOSED_MODE == StaticVariable.GAME_MODE.INTERNET) {
            this.clientCommunicate = new ClentInternet(StaticVariable.SERVER_IP, StaticVariable.SERVER_PORT);
        }else if(StaticVariable.CHOSED_MODE == StaticVariable.GAME_MODE.BLUETOOTH){
            this.clientCommunicate = new ClentInternet(StaticVariable.SERVER_IP, StaticVariable.SERVER_PORT);
        }else{
            this.clientCommunicate = new ClentInternet(StaticVariable.SERVER_IP, StaticVariable.SERVER_PORT);
        }
        //给service设置通讯借口
        this.gameService.setClientCommunicate(this.clientCommunicate);
    }

    //开启gameService的线程
    public void startGame(){
        this.gameService.gameStart();
    }

    //关闭所有哦线程
    public void stopGame(){
        this.gameService.gameStop();
    }

}

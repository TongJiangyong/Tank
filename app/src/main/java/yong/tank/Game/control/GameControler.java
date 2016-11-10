package yong.tank.Game.control;

import yong.tank.Game.service.GameService;

/**
 * Created by hasee on 2016/11/1.
 * 这个类非常重要，在这个类中，控制（service）game中的所有逻辑，然后，将逻辑结果传给modal即可......
 */

public class GameControler {
    private GameService gameService;
    private static String TAG ="GameControler";
    public GameControler(GameService gameService) {
        this.gameService = gameService;
    }

    //开启gameService的线程
    public void startGame(){
        this.gameService.gameStart();
    }



}

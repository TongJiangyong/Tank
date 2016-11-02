package yong.tank.Game.control;

import yong.tank.Dto.GameDto;

/**
 * Created by hasee on 2016/11/1.
 * 这个类非常重要，在这个类中，控制（service）game中的所有逻辑，然后，将逻辑结果传给modal即可......
 */

public class GameControler {
    private GameDto gameDto;

    public GameControler(GameDto gameDto) {
        this.gameDto = gameDto;
    }
}

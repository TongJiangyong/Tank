package yong.tank.Dto;

import yong.tank.modal.Blood;
import yong.tank.modal.PlayerPain;
import yong.tank.modal.MyTank;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class GameDto {
    //这里最好每一个modal，都会在里面.....
    private MyTank myTank;
    private Blood blood;
    private PlayerPain playerPain;
    public MyTank getMyTank() {
        return myTank;
    }
    public void setMyTank(MyTank myTank) {
        this.myTank = myTank;
    }

    public Blood getBlood() {
        return blood;
    }

    public void setBlood(Blood blood) {
        this.blood = blood;
    }

    public PlayerPain getPlayerPain() {
        return playerPain;
    }

    public void setPlayerPain(PlayerPain playerPain) {
        this.playerPain = playerPain;
    }
}

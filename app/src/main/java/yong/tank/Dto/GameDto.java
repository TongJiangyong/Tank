package yong.tank.Dto;

import java.util.ArrayList;
import java.util.List;

import yong.tank.modal.Blood;
import yong.tank.modal.Explode;
import yong.tank.modal.MyTank;
import yong.tank.modal.PlayerPain;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class GameDto {
    //这里最好每一个modal，都会在里面.....
    private MyTank myTank;
    private Blood blood;
    private PlayerPain playerPain;
    private List<Explode> explodes;
    public MyTank getMyTank() {
        return myTank;
    }
    public void setMyTank(MyTank myTank) {
        this.myTank = myTank;
        explodes = new ArrayList<Explode>();
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

    public List<Explode> getExplodes() {
        return explodes;
    }

    public void setExplodes(List<Explode> explodes) {
        this.explodes = explodes;
    }
}

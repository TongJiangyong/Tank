package yong.tank.Dto;

import yong.tank.modal.MyTank;
import yong.tank.modal.Tank;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class GameDto {
    private MyTank myTank;

    public MyTank getMyTank() {
        return myTank;
    }

    public void setMyTank(MyTank myTank) {
        this.myTank = myTank;
    }
}

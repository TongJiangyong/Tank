package yong.tank.Dto;

import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import yong.tank.modal.Bonus;
import yong.tank.modal.EnemyBlood;
import yong.tank.modal.EnemyTank;
import yong.tank.modal.Explode;
import yong.tank.modal.MyBlood;
import yong.tank.modal.MyTank;
import yong.tank.modal.PlayerPain;
import yong.tank.modal.SelectButton;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class GameDto implements Serializable{
    //初始化必备的
    private int tankType ;
    private int mapType ;
    private MyTank myTank;
    private MyBlood myBlood;
    private transient EnemyTank enemyTank;
    private transient EnemyBlood enemyBlood;
    //这里最好每一个modal，都会在里面.....
    private transient PlayerPain playerPain;
    private List<Explode> explodes;   //爆炸场景为在lcoal进行绘制
    private Bonus bonus = null;
    private transient Map<Integer,SelectButton> selectButtons;
    private transient TextView msgText;

    public MyTank getMyTank() {
        return myTank;
    }
    public void setMyTank(MyTank myTank) {
        this.myTank = myTank;
        explodes = new ArrayList<Explode>();
    }

    public MyBlood getMyBlood() {
        return myBlood;
    }

    public void setMyBlood(MyBlood myBlood) {
        this.myBlood = myBlood;
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

    public Bonus getBonus() {
        return bonus;
    }

    public void setBonus(Bonus bonus) {
        //将原来的bonus置为null
        this.bonus=null;
        this.bonus = bonus;
    }

    public Map<Integer, SelectButton> getSelectButtons() {
        return selectButtons;
    }

    public void setSelectButtons(Map<Integer, SelectButton> selectButtons) {
        this.selectButtons = selectButtons;
    }

    public int getTankType() {
        return tankType;
    }

    public void setTankType(int tankType) {
        this.tankType = tankType;
    }

    public int getMapType() {
        return mapType;
    }

    public void setMapType(int mapType) {
        this.mapType = mapType;
    }


    public EnemyTank getEnemyTank() {
        return enemyTank;
    }

    public void setEnemyTank(EnemyTank enemyTank) {
        this.enemyTank = enemyTank;
    }

    public EnemyBlood getEnemyBlood() {
        return enemyBlood;
    }

    public void setEnemyBlood(EnemyBlood enemyBlood) {
        this.enemyBlood = enemyBlood;
    }

    public TextView getMsgText() {
        return msgText;
    }

    public void setMsgText(TextView msgText) {
        this.msgText = msgText;
    }
}

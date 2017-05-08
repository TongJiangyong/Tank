package yong.tank.Data;

import java.io.Serializable;

/**
 * Created by hasee on 2017/5/7.
 */

public class GameSendingData implements Serializable {
    public GameSendingData(int dataFlag) {
        this.dataFlag = dataFlag;
    }

    /**判断是本地的信息，还是服务器的信息
     * 0为本地，1为服务器
     */
    public int dataFlag = 0;
    /**服务器相关参数
     *
     */
    private int serverFrame;
    //bonus相关参数
    private boolean enableBonus;

    private int bonusType;

    private int bonusDirction;

    /**我的坦克相关参数
     *
     */
    //我的坦克的方向
    private int MyTankDirection;
    //我的坦克角度
    private int MyTankDegree;
    //我的坦克是否要发射
    private boolean MyTankEnableFire;
    //我的坦克发射力度
    private int MyTankBulletDistance;

    //我的血条比例
    private double MyTankBloodNum;
    //我的子弹种类：
    private int MyBulletType;

    /**敌方坦克相关参数
     *
     */
    //敌方的坦克方向
    private int EnemyTankDirection;
    //敌方的坦克角度
    private int EnemyTankDegree;
    //敌方的坦克是否要发射
    private Boolean EnemyTankEnableFire;
    //敌方的坦克发射力度
    private double EnemyTankBulletDistance;
    //敌方的血条比例
    private double EnemyTankBloodNum;
    //地方的子弹种类
    private int EnemyTankBulletType;

    public int getMyTankDirection() {
        return MyTankDirection;
    }

    public void setMyTankDirection(int myTankDirection) {
        MyTankDirection = myTankDirection;
    }

    public int getMyTankDegree() {
        return MyTankDegree;
    }

    public void setMyTankDegree(int myTankDegree) {
        MyTankDegree = myTankDegree;
    }

    public boolean getMyTankEnableFire() {
        return MyTankEnableFire;
    }

    public void setMyTankEnableFire(boolean myTankEnableFire) {
        MyTankEnableFire = myTankEnableFire;
    }

    public int getMyTankBulletDistance() {
        return MyTankBulletDistance;
    }

    public void setMyTankBulletDistance(int myTankBulletDistance) {
        MyTankBulletDistance = myTankBulletDistance;
    }



    public double getMyTankBloodNum() {
        return MyTankBloodNum;
    }

    public void setMyTankBloodNum(double myTankBloodNum) {
        MyTankBloodNum = myTankBloodNum;
    }

    public int getEnemyTankDirection() {
        return EnemyTankDirection;
    }

    public void setEnemyTankDirection(int enemyTankDirection) {
        EnemyTankDirection = enemyTankDirection;
    }

    public int getEnemyTankDegree() {
        return EnemyTankDegree;
    }

    public void setEnemyTankDegree(int enemyTankDegree) {
        EnemyTankDegree = enemyTankDegree;
    }

    public Boolean getEnemyTankEnableFire() {
        return EnemyTankEnableFire;
    }

    public void setEnemyTankEnableFire(Boolean enemyTankEnableFire) {
        EnemyTankEnableFire = enemyTankEnableFire;
    }

    public double getEnemyTankBulletDistance() {
        return EnemyTankBulletDistance;
    }

    public void setEnemyTankBulletDistance(double enemyTankBulletDistance) {
        EnemyTankBulletDistance = enemyTankBulletDistance;
    }



    public double getEnemyTankBloodNum() {
        return EnemyTankBloodNum;
    }

    public void setEnemyTankBloodNum(double enemyTankBloodNum) {
        EnemyTankBloodNum = enemyTankBloodNum;
    }

    public int getServerFrame() {
        return serverFrame;
    }

    public void setServerFrame(int serverFrame) {
        this.serverFrame = serverFrame;
    }

    public int getMyBulletType() {
        return MyBulletType;
    }

    public void setMyBulletType(int myBulletType) {
        MyBulletType = myBulletType;
    }

    public int getEnemyTankBulletType() {
        return EnemyTankBulletType;
    }

    public void setEnemyTankBulletType(int enemyTankBulletType) {
        EnemyTankBulletType = enemyTankBulletType;
    }

    public boolean getEnableBonus() {
        return enableBonus;
    }

    public void setEnableBonus(boolean enableBonus) {
        this.enableBonus = enableBonus;
    }

    public int getBonusType() {
        return bonusType;
    }

    public void setBonusType(int bonusType) {
        this.bonusType = bonusType;
    }

    public int getBonusDirction() {
        return bonusDirction;
    }

    public void setBonusDirction(int bonusDirction) {
        this.bonusDirction = bonusDirction;
    }
}

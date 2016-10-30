package yong.tank.modal;

/**
 * Created by hasee on 2016/10/30.
 */

public class TankBascInfo {
    private int type;
    private int blood;
    private int speed;
    private int power;
    private int picture;
    private String tankName; //不联网为坦克名，联网则为自定义名字
    private String describeInfo;


    public TankBascInfo(int type, int blood, int speed, int power, int picture, String tankName, String describeInfo) {
        this.type = type;
        this.blood = blood;
        this.speed = speed;
        this.power = power;
        this.picture = picture;
        this.tankName = tankName;
        this.describeInfo = describeInfo;
    }

    public String getTankName() {
        return tankName;

    }

    public void setTankName(String tankName) {
        this.tankName = tankName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescribeInfo() {
        return describeInfo;
    }

    public void setDescribeInfo(String describeInfo) {
        this.describeInfo = describeInfo;
    }

    public int getBlood() {
        return blood;
    }

    public void setBlood(int blood) {
        this.blood = blood;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }
}

package yong.tank.modal;

import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/10.
 */

public class BulletBascInfo {
    private int type;
    private double speed;
    private double power;
    private int picture;
    private String bulletName;
    public BulletBascInfo(int type, double speed, double power, int picture, String bulletName) {
        this.type = type;
        this.speed = speed;
        this.power = power;
        this.picture = picture;
        this.bulletName = bulletName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    //速度都是按s算
    public double getSpeed() {
        return (double)(speed* StaticVariable.LOCAL_SCREEN_WIDTH);
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getPower() {
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

    public String getBulletName() {
        return bulletName;
    }

    public void setBulletName(String bulletName) {
        this.bulletName = bulletName;
    }
}

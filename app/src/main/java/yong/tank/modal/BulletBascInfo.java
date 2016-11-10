package yong.tank.modal;

/**
 * Created by hasee on 2016/11/10.
 */

public class BulletBascInfo {
    private int type;
    private int speed;
    private int power;
    private int picture;
    private String bulletName;

    public BulletBascInfo(int type, int speed, int power, int picture, String bulletName) {
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

    public String getBulletName() {
        return bulletName;
    }

    public void setBulletName(String bulletName) {
        this.bulletName = bulletName;
    }
}

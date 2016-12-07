package yong.tank.Title_Activity.presenter;

import android.content.Intent;

/**
 * Created by hasee on 2016/10/27.
 */

public interface ITitlePresenter {
    void toComputer();
    void toBluetooth();
    void toNet();
    void tohelp();
    void enableBluetooth();
    void toBlueTankChose(int resultCode, Intent data);
    void turnOffBluetooth();
}

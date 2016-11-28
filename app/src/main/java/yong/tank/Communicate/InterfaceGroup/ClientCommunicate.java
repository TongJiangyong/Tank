package yong.tank.Communicate.InterfaceGroup;

import android.os.Handler;

/**
 * Created by hasee on 2016/11/28.
 */

public interface ClientCommunicate {
    void addInfoObserver(ObserverInfo observerInfo);
    void addMsgObserver(ObserverMsg observerMsg);
    void addCommandObserver(ObserverCommand observerCommand);
    void setMyHandle(Handler myHandle);
    void  sendInfo(String info);
    void startCommunicate();
    void stopCommunicate();
}

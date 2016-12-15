package yong.tank.Communicate.InterfaceGroup;

import yong.tank.Communicate.ComData.ComDataF;

/**
 * Created by hasee on 2016/11/26.
 */

public interface Subject {
    void addMsgObserver(ObserverMsg observerMsg);
    void removeMsgObserver(ObserverMsg observerMsg);
    void addInfoObserver(ObserverInfo oberserInfo);
    void removeInfoObserver(ObserverInfo oberserInfo);
    void addCommandObserver(ObserverCommand oberserCommand);
    void removeCommandObserver(ObserverCommand oberserCommand);
    void notifyWatchers(ComDataF comDataF);
}

package yong.tank.Communicate.InterfaceGroup;

import android.os.Handler;

/**
 * Created by hasee on 2016/11/28.
 */

public interface ClientCommunicate {
    //数据监听
    void addInfoObserver(ObserverInfo observerInfo);
    //消息监听
    void addMsgObserver(ObserverMsg observerMsg);
    //命令监听
    void addCommandObserver(ObserverCommand observerCommand);
    //设置handle的处理
    void setMyHandle(Handler myHandle);
    //发送数据方法
    void sendInfo(String info);
    //启动相应的线程
    void startCommunicate();
    //停止相应的线程
    void stopCommunicate();
}

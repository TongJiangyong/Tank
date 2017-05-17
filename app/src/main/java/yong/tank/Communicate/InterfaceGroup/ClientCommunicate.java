package yong.tank.Communicate.InterfaceGroup;

import android.bluetooth.BluetoothDevice;
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
    //Activity专门发送程序运行数据给service的方法 ，这个是很不好的处理方式，没办法
    void writeToService(String info);
    //启动相应的线程
    void startCommunicate();
    //停止相应的线程
    void stopCommunicate();
    //停止相应的线程
    void connectDevice(BluetoothDevice device, boolean secure);

    /**
     * 这一个设计模式出了问题，实际上只能由clientLocal来使用了，因为client和
     *实际公用一个线程，其他的是分开的.....
     * * *********************************/
    void updateRemoteInfo();

}

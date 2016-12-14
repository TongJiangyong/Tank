package yong.tank.Communicate.LocalCommunicate;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

import yong.tank.Communicate.InterfaceGroup.ClientCommunicate;
import yong.tank.Communicate.InterfaceGroup.ObserverCommand;
import yong.tank.Communicate.InterfaceGroup.ObserverInfo;
import yong.tank.Communicate.InterfaceGroup.ObserverMsg;

/**
 * Created by hasee on 2016/11/26.
 */

//最好将其写成单利模式
//这个类即一个通信的工具代理类 代理类均可以写成接口的形式，就是如此，即通信均通过代理类来实现
public class ClientLocal implements  Runnable,ClientCommunicate {
    private AImaker aImaker;
    private String ip;
    private int port;
    private static String TAG ="ClientLocal";
    private boolean connectFlag =false;
    private int connectCount = 0;
    private Handler mHandler;
    // 存放观察者
    private List<ObserverMsg> observerMsgs = new ArrayList<ObserverMsg>();
    private List<ObserverCommand> observerCommands = new ArrayList<ObserverCommand>();
    private List<ObserverInfo> observerInfos = new ArrayList<ObserverInfo>();
    //穿进来要建立拿一个tank的对象信息即可
    public ClientLocal(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void run() {
        //TODO 这里启动AI的线程
        
    }

    //封装发送信息的方法 这里不用发信息，所以没用
    public void sendInfo (String msg){

    }

    @Override
    public void startCommunicate() {
        //启动自身....
        new Thread(this).start();
    }

    //开始启动AI
    @Override
    public void stopCommunicate() {

    }

    //接口方法，什么都不做
    @Override
    public void connectDevice(BluetoothDevice device, boolean secure) {

    }


    @Override
    public void addInfoObserver(ObserverInfo observerInfo) {
        this.aImaker.addInfoObserver(observerInfo);
    }

    @Override
    public void addMsgObserver(ObserverMsg observerMsg) {
        this.aImaker.addMsgObserver(observerMsg);
    }

    @Override
    public void addCommandObserver(ObserverCommand observerCommand) {
        this.aImaker.addCommandObserver(observerCommand);
    }

    @Override
    public void setMyHandle(Handler myHandler) {
        this.mHandler = myHandler;
    }


}

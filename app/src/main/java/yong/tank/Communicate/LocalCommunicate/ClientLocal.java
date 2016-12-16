package yong.tank.Communicate.LocalCommunicate;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

import yong.tank.Communicate.InterfaceGroup.ClientCommunicate;
import yong.tank.Communicate.InterfaceGroup.ObserverCommand;
import yong.tank.Communicate.InterfaceGroup.ObserverInfo;
import yong.tank.Communicate.InterfaceGroup.ObserverMsg;
import yong.tank.Dto.GameDto;

/**
 * Created by hasee on 2016/11/26.
 */

//最好将其写成单利模式
//TODO clientLocal相关
//这个类即一个通信的工具代理类 代理类均可以写成接口的形式，就是如此，即通信均通过代理类来实现
//为了实现通用的通信借口 ，这里采用的逻辑是，将gameDto每隔20ms就主动发送一次数据，然后接收端做同样的处理即可
public class ClientLocal implements  Runnable,ClientCommunicate {
    private AImaker aImaker;
    private static String TAG ="ClientLocal";
    private boolean connectFlag =false;
    private int connectCount = 0;
    private GameDto gameDto;
    private Handler mHandler;
    // 存放观察者
    private List<ObserverMsg> observerMsgs = new ArrayList<ObserverMsg>();
    private List<ObserverCommand> observerCommands = new ArrayList<ObserverCommand>();
    private List<ObserverInfo> observerInfos = new ArrayList<ObserverInfo>();
    //穿进来要建立拿一个tank的对象信息即可
    public ClientLocal(GameDto gameDto) {
        this.gameDto = gameDto;
    }

    public void run() {
        //TODO 这里启动AI的线程
        //线程的主要作用 1、每隔20ms，发送一次gameDto的数据信息
        //2、自动检测子弹使能的时间，如果有子弹使能，则发射子弹（一律为普通弹）
        
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

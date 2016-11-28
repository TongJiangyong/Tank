package yong.tank.Communicate.InternetCommunicate;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.InetSocketAddress;
import java.net.Socket;

import yong.tank.Communicate.InterfaceGroup.ClientCommunicate;
import yong.tank.Communicate.InterfaceGroup.ObserverCommand;
import yong.tank.Communicate.InterfaceGroup.ObserverInfo;
import yong.tank.Communicate.InterfaceGroup.ObserverMsg;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/26.
 */

//最好将其写成单利模式
//这个类即一个通信的工具代理类 代理类均可以写成接口的形式，就是如此，即通信均通过代理类来实现
public class ClentInternet implements  Runnable,ClientCommunicate {
    private Socket client;
    private ClientThread clientThread;
    private String ip;
    private int port;
    private static String TAG ="ClentCommunicate";
    private boolean connectFlag =false;
    private int connectCount = 0;
    private Handler myHandle;

    public ClentInternet(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void run() {
            client = new Socket();
            while(!connectFlag){
                try{
                    client.connect(new InetSocketAddress(ip, port), 3000);
                }catch (Exception e){
                    Log.w(TAG,"connect error :"+e);
                }
                Log.w(TAG,"ConnectedTest.....");
                connectCount++;
                if(client.isConnected()){
                    connectFlag = true;
                    Log.w(TAG,"Connected to server...");
                    clientThread = new ClientThread(client);
                    new Thread(clientThread).start();
                    connectCount=0;
                    Message msg = myHandle.obtainMessage();
                    msg.what = StaticVariable.MSG_CONNECT_SUCCESS;
                    myHandle.sendMessage(msg);
                }
                //判断连接失败
                if(connectCount>3){
                    Log.w(TAG,"ConnectedError.....");
                    Message msg = myHandle.obtainMessage();
                    msg.what = StaticVariable.MSG_CONNECT_ERROR;
                    myHandle.sendMessage(msg);
                    break;
                }
            }
    }

    // 直接通过client得到读线程
    public ClientInputThread getClientInputThread() {
        return clientThread.getIn();
    }

    // 直接通过client得到写线程  通过借口实现，没有使用这个.....
    public ClientOutputThread getClientOutputThread() {
        return clientThread.getOut();
    }

    //封装发送信息的方法
    public void sendInfo (String msg){
        this.clientThread.getOut().setMsg(msg);
    }

    @Override
    public void startCommunicate() {
        //启动自身....
        new Thread(this).start();
    }

    @Override
    public void stopCommunicate() {
        this.setIsStart(false);
    }

    public void addMsgObserver(ObserverMsg observerMsg) {
        this.getClientInputThread().addMsgObserver(observerMsg);
    }
    public void addCommandObserver(ObserverCommand observerCommand) {
        this.getClientInputThread().addCommandObserver(observerCommand);
    }
    public void addInfoObserver(ObserverInfo observerInfo) {
        this.getClientInputThread().addInfoObserver(observerInfo);
    }
    // 直接通过client停止读写消息
    public void setIsStart(boolean isStart) {
        clientThread.getIn().setStart(isStart);
        clientThread.getOut().setStart(isStart);
    }

    public void setMyHandle(Handler myHandle) {
        this.myHandle = myHandle;
    }

    public class ClientThread implements Runnable {

        private ClientInputThread in;
        private ClientOutputThread out;

        public ClientThread(Socket socket) {
            in = new ClientInputThread(socket);
            out = new ClientOutputThread(socket);
        }

        public void run() {
            in.setStart(true);
            out.setStart(true);
            //设置handle处理
            in.setMyHander(myHandle);
            out.setMyHander(myHandle);
            new Thread(in).start();
            new Thread(out).start();
        }

        // 得到读消息线程
        public ClientInputThread getIn() {
            return in;
        }

        // 得到写消息线程
        public ClientOutputThread getOut() {
            return out;
        }
    }
}

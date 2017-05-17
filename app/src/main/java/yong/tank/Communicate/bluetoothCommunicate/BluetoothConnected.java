package yong.tank.Communicate.bluetoothCommunicate;

/**
 * Created by hasee on 2016/12/1.
 */

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import yong.tank.Communicate.ComData.ComDataF;
import yong.tank.Communicate.ComData.ComDataPackage;
import yong.tank.Communicate.InterfaceGroup.ObserverCommand;
import yong.tank.Communicate.InterfaceGroup.ObserverInfo;
import yong.tank.Communicate.InterfaceGroup.ObserverMsg;
import yong.tank.Communicate.InterfaceGroup.Subject;
import yong.tank.Communicate.ServerService.ServerService;
import yong.tank.tool.StaticVariable;

/**
 * This thread runs during a connection with a remote device.
 * It handles all incoming and outgoing transmissions.
 * 这里用两个线程来处理试试
 */
public class BluetoothConnected extends Thread implements Subject {
    private final BluetoothSocket mmSocket;
    private InputStream input;
    private ClientBluetooth clientBluetooth;
    private BlueOutputThread blueOutputThread;
    private Handler myHander;
    private static final String TAG = "BluetoothConnected";
    private byte[] readBuffer = new byte[StaticVariable.READ_BYTE];
    // 存放观察者
    private List<ObserverMsg> observerMsgs = new ArrayList<ObserverMsg>();
    private List<ObserverCommand> observerCommands = new ArrayList<ObserverCommand>();
    private List<ObserverInfo> observerInfos = new ArrayList<ObserverInfo>();
    private boolean readFlag = true;
    private ServerService serverService;
    public BluetoothConnected(BluetoothSocket socket, String socketType, ClientBluetooth clientBluetooth) {
        Log.i(TAG, "create ConnectedThread: " + socketType);
        mmSocket = socket;
        this.clientBluetooth = clientBluetooth;
        myHander=this.clientBluetooth.getMyHandle();
        // Get the BluetoothSocket input and output streams
        try {
            this.input = socket.getInputStream();
            //启动另外读的线程即可
            blueOutputThread = new BlueOutputThread(socket);
            new Thread(blueOutputThread).start();
            Log.i(TAG,"AcceptThread in STATE_CONNECTING");
        } catch (IOException e) {
            Log.e(TAG, "temp sockets not created", e);
        }
        //如果成功连上蓝牙，并是activity模式，则创建一个服务器线程，并开始处理
        if(StaticVariable.CHOSED_RULE==StaticVariable.GAME_RULE.ACTIVITY){
            ServerService serverService = new ServerService();
            //启动sevice线程
            new Thread(serverService).start();
            this.serverService= serverService;
        }
    }

    public void run() {
        //主线程开始前，先给serverService配置蓝牙适配器
        if(StaticVariable.CHOSED_RULE==StaticVariable.GAME_RULE.ACTIVITY){
            this.serverService.setBlutoothAdapter(this);
        }

        Log.i(TAG, "BEGIN mConnectedThread ---->允许蓝牙开始读写....");
         //byte[] buffer = new byte[1024];
        // Keep listening to the InputStream while connected
        while (readFlag) {
            try {
                // Read from the InputStream
                //TODO 改写为byte试试......
                while(input.read(readBuffer)!=-1){
                    //数据收到就有问题.....
                    String readline=new String(readBuffer).trim();
                    //需要重新分配，不然会有问题......
                    readBuffer = new byte[StaticVariable.READ_BYTE];
                    Log.w(TAG, "*******************************input Thread 收到的数据 *****************************************");
                    String[] readInfos  =readline.split("&");
                    Log.w(TAG,"数据长度："+readInfos.length);
                    //解析每一个消息
                    for(int i=0;i<readInfos.length;i++){
                        Log.i(TAG, "input Thread 收到的信息_1: "+readInfos[i]);
                        /**
                         * 注意这里，对 activity和passive端的处理方式。两者各有不同，这里仅仅对标签为StaticVariable.COMMAND_INFO
                         * activity的处理方式为，直接将收到passvie的数据发送给serverService线程，并由serverService线程处理后序
                         * 后序处理分为两步：
                         * 1、activity数据,因为activity实际为server，所以需要把数据转给server
                         * 2、passive数据 ，通过蓝牙线程的write函数，sending出去....
                         * passive端的处理方式为传送给notifyWatchers
                         */
                        ComDataF comDataF=null;
                        try {
                            comDataF = ComDataPackage.unpackToF(readInfos[i]);
                            //这里表示是数据的信息
                            if(comDataF.getComDataS().getCommad().equals(StaticVariable.COMMAND_INFO)){
                                //如果数据是从passive端传入，则直接转发给server
                                if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY){
                                    //即，如果本机是ACTIVITY，收到COMMAND_INFO的数据，判定数据是从passive端传过来，直接转发给server
                                    Log.i(TAG, "这里是activy端收到数据，转发给server: "+readInfos[i]);
                                    this.serverService.reciveDataFromPassive(comDataF);
                                    //如果本机是passive，收到COMMAND_INFO的数据，判定数据是从activity端传过来，直接转发给自己
                                }else{
                                    //即，这里是server转发过来的数据，直接转走即可
                                    Log.i(TAG, "这里是passive端收到数据，直接转发给游戏: "+readInfos[i]);
                                    this.notifyWatchers(comDataF);
                                }
                                //如果不是数据的信息，则直接转发
                            }else{
                                //如果不是commandinfo，则直接转发该数据
                                this.notifyWatchers(comDataF);
                            }


                        }
                        catch (Exception e)
                        {
                            System.out.println("input Thread error parse file "+e);
                            System.out.println("error info is "+readInfos[i]);
                            continue;
                        }
                    }
                }
                Message msg = myHander.obtainMessage();
                msg.what = StaticVariable.BLUE_COMMUNICATE_ERROR;
                myHander.sendMessage(msg);
            }catch (Exception e) {
                Log.e(TAG, "bluetooth read error", e);
                clientBluetooth.connectionLost();
                Message msg = myHander.obtainMessage();
                msg.what = StaticVariable.BLUE_COMMUNICATE_ERROR;
                myHander.sendMessage(msg);
                readFlag =false;
                // Start the service over to restart listening mode
                break;
            }
        }
        Log.i(TAG, "end of mConnectedThread ---->蓝牙读写关闭....");
    }

    /**
     * Write to the connected OutStream.
     * @param msg  The bytes to write
     */
    //public void write(byte[] buffer) {
    public void write(String msg) {
        /**
         * 注意这里，对 activity和passive端的处理方式。两者各有不同
         * activity的处理方式为，直接将预备发送的activity的数据发送给serverService线程（不包括还没有准备完全的情况.....），并由serverService线程处理后序
         * passive端的处理方式为，将数据 发送给output的另一端
         *
         */
        //if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY){
        //    this.serverService.reciveDataFromActivity(msg);
        //}else{
            this.blueOutputThread.setMsg(msg);
        //}
    }

    public void writeToService(String msg) {
        /**
         * 注意这里，对 activity和passive端的处理方式。两者各有不同
         * activity的处理方式为，直接将预备发送的activity的数据发送给serverService线程（不包括还没有准备完全的情况.....），并由serverService线程处理后序
         * passive端的处理方式为，将数据 发送给output的另一端
         *
         */
        //if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY){
            this.serverService.reciveDataFromActivity(msg);
        //}else{
            //this.blueOutputThread.setMsg(msg);
        //}

    }

    public void cancel() {
        try {
            Log.i(TAG,"连接断开");
            input.close();
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "close() of connect socket failed", e);
        }
    }


    public class BlueOutputThread implements Runnable{
        private BluetoothSocket socket;
        private BufferedWriter outupt;
        private boolean isStart = true;
        private String msg;

        public BlueOutputThread(BluetoothSocket socket) {
            this.socket = socket;
            try {
                outupt = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void setStart(boolean isStart) {
            this.isStart = isStart;
        }

        // 这里处理跟服务器是一样的
        public void setMsg(String msg) {
            this.msg = msg;
            //这里是通过notify/wait来控制线程的方法
            synchronized (this) {
                notify();
            }
        }
        @Override
        public void run() {
            try {
                // 没有消息写出的时候，线程等待
                while (isStart) {
                    /**
                     * 注意这里，对 activity和passive端的处理方式。两者各有不同
                     * activity的处理方式为，直接将预备发送的activity的数据发送给serverService线程，并由serverService线程处理后序
                     * passive端的处理方式为，将数据 发送给output的另一端
                     */
                    if (msg != null) {
                        Log.i(TAG,"sendInfo is_1 :"+msg);
                        outupt.write(msg+"&");
                        outupt.flush();
                        msg =null;
                    }
                    //感觉下面没啥用，写一下呗
                    synchronized (this) {
                        try {
                            wait();// 发送完消息后，线程进入等待状态
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.w(TAG,"error 1");
                        }
                    }
                }
                outupt.close();// 循环结束后，关闭输出流和socket
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                Message msg = myHander.obtainMessage();
                msg.what = StaticVariable.BLUE_COMMUNICATE_ERROR;
                myHander.sendMessage(msg);
                e.printStackTrace();
            }
        }

    }



    public void setMyHander(Handler myHander) {
        this.myHander = myHander;
    }

    @Override
    public void addMsgObserver(ObserverMsg observerMsg) {
        observerMsgs.add(observerMsg);
    }

    @Override
    public void removeMsgObserver(ObserverMsg observerMsg) {
        observerMsgs.remove(observerMsg);
    }

    @Override
    public void addInfoObserver(ObserverInfo observerInfo) {
        observerInfos.add(observerInfo);
    }

    @Override
    public void removeInfoObserver(ObserverInfo observerInfo) {
        observerInfos.remove(observerInfos);
    }

    @Override
    public void addCommandObserver(ObserverCommand observerCommand) {
        observerCommands.add(observerCommand);
    }

    @Override
    public void removeCommandObserver(ObserverCommand observerCommand) {
        observerCommands.remove(observerCommand);
    }

    @Override
    public void notifyWatchers(ComDataF comDataF) {

        //处理聊天信息
        if(comDataF.getComDataS().getCommad().equals(StaticVariable.COMMAND_MSG)){
            for(ObserverMsg o:observerMsgs){
                //传入string
                o.msgRecived(comDataF.getComDataS().getObject());
            }
            //处理info信息
        }else if(comDataF.getComDataS().getCommad().equals(StaticVariable.COMMAND_INFO)){
            for(ObserverInfo o:observerInfos){
                //传入对象
                o.infoRecived(ComDataPackage.packageToObject(comDataF.getComDataS().getObject()));
            }
            //处理command相关的信息
        }else {
            for(ObserverCommand o:observerCommands){
                //传入command
                o.commandRecived(comDataF);
            }
        }
    }
}
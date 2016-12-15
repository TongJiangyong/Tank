package yong.tank.Communicate.bluetoothCommunicate;

/**
 * Created by hasee on 2016/12/1.
 */

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import yong.tank.Communicate.ComData.ComDataF;
import yong.tank.Communicate.ComData.ComDataPackage;
import yong.tank.Communicate.InterfaceGroup.ObserverCommand;
import yong.tank.Communicate.InterfaceGroup.ObserverInfo;
import yong.tank.Communicate.InterfaceGroup.ObserverMsg;
import yong.tank.Communicate.InterfaceGroup.Subject;
import yong.tank.tool.StaticVariable;

/**
 * This thread runs during a connection with a remote device.
 * It handles all incoming and outgoing transmissions.
 * 这里用两个线程来处理试试
 */
public class BluetoothConnected extends Thread implements Subject {
    private final BluetoothSocket mmSocket;
    private BufferedReader input;
    private ClientBluetooth clientBluetooth;
    private BlueOutputThread blueOutputThread;
    private Handler myHander;
    private static final String TAG = "BluetoothConnected";
    // 存放观察者
    private List<ObserverMsg> observerMsgs = new ArrayList<ObserverMsg>();
    private List<ObserverCommand> observerCommands = new ArrayList<ObserverCommand>();
    private List<ObserverInfo> observerInfos = new ArrayList<ObserverInfo>();
    private boolean readFlag = true;
    public BluetoothConnected(BluetoothSocket socket, String socketType, ClientBluetooth clientBluetooth) {
        Log.i(TAG, "create ConnectedThread: " + socketType);
        mmSocket = socket;
        this.clientBluetooth = clientBluetooth;
        myHander=this.clientBluetooth.getMyHandle();
        // Get the BluetoothSocket input and output streams
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            //启动另外读的线程即可
            blueOutputThread = new BlueOutputThread(socket);
            new Thread(blueOutputThread).start();
            Log.i(TAG,"AcceptThread in STATE_CONNECTING");
        } catch (IOException e) {
            Log.e(TAG, "temp sockets not created", e);
        }
    }

    public void run() {
        Log.i(TAG, "BEGIN mConnectedThread ---->允许蓝牙开始读写....");
         //byte[] buffer = new byte[1024];
        // Keep listening to the InputStream while connected
        while (readFlag) {
            try {
                // Read from the InputStream
                //后期尝试改为byte试试......
               String msg = input.readLine();
                //input.read();
                Log.w(TAG,"bluetooth Connected and read msg is :"+msg);
                this.write(msg);
                //传送数据
                //notifyWatchers(msg);
                // Send the obtained bytes to the UI Activity
/*                    mHandler.obtainMessage(BluetoothChat.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();*/
            }catch (Exception e) {
                Log.e(TAG, "bluetooth read error", e);
                clientBluetooth.connectionLost();
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
        this.blueOutputThread.setMsg(msg);
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
                    if (msg != null) {
                        outupt.write(msg);
                        outupt.write('\n');
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
                o.commandRecived(comDataF.getComDataS().getCommad());
            }
        }
    }
}
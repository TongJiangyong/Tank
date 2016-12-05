package yong.tank.Communicate.bluetoothCommunicate;

/**
 * Created by hasee on 2016/12/1.
 */

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
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
 */
public class BluetoothConnected extends Thread implements Subject {
    private final BluetoothSocket mmSocket;
    private BufferedReader input;
    private BufferedWriter output;
    private ClientBluetooth clientBluetooth;
    private Handler myHander;
    private static final String TAG = "BluetoothConnected";
    // 存放观察者
    private List<ObserverMsg> observerMsgs = new ArrayList<ObserverMsg>();
    private List<ObserverCommand> observerCommands = new ArrayList<ObserverCommand>();
    private List<ObserverInfo> observerInfos = new ArrayList<ObserverInfo>();
    public BluetoothConnected(BluetoothSocket socket, String socketType, ClientBluetooth clientBluetooth) {
        Log.i(TAG, "create ConnectedThread: " + socketType);
        mmSocket = socket;
        this.clientBluetooth = clientBluetooth;
        myHander=this.clientBluetooth.getMyHandle();
        // Get the BluetoothSocket input and output streams
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            Log.e(TAG, "temp sockets not created", e);
        }
    }

    public void run() {
        Log.i(TAG, "BEGIN mConnectedThread");

        // Keep listening to the InputStream while connected
        while (true) {
            try {
                // Read from the InputStream
                //后期尝试改为byte试试......
                String msg = input.readLine();
                Log.w(TAG,"data:"+msg);
                //传送数据
                //notifyWatchers(msg);
                // Send the obtained bytes to the UI Activity
/*                    mHandler.obtainMessage(BluetoothChat.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();*/
            } catch (IOException e) {
                Log.e(TAG, "disconnected", e);
                clientBluetooth.connectionLost();
                // Start the service over to restart listening mode
                clientBluetooth.startConnectService();
                break;
            }
        }
    }

    /**
     * Write to the connected OutStream.
     * @param msg  The bytes to write
     */
    //public void write(byte[] buffer) {
    public void write(String msg) {
        try {
            Log.i(TAG,"sendMes:" +msg);
            output.write(msg);
            Log.w(TAG,"sendMes");
            // Share the sent message back to the UI Activity
/*                mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();*/
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "close() of connect socket failed", e);
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
    public void notifyWatchers(String msg) {
        ComDataF comDataF  = ComDataPackage.unpackToF(msg);
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
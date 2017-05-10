package yong.tank.Communicate.bluetoothCommunicate;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import yong.tank.Communicate.InterfaceGroup.ClientCommunicate;
import yong.tank.Communicate.InterfaceGroup.ObserverCommand;
import yong.tank.Communicate.InterfaceGroup.ObserverInfo;
import yong.tank.Communicate.InterfaceGroup.ObserverMsg;
import yong.tank.Communicate.ServerService.ServerService;
import yong.tank.tool.StaticVariable;
//TODO 这里出现一个很严重的问题，可能需要AIDL来处理......因为蓝牙的建立是在一个线程，但是使用在其他的线程...
//TODO 或者只能委屈求全，在game的activity建立蓝牙的选择等处理流程.......没有办法......
/**
 * 这个类做了所有蓝牙连接相关的工作 ，他有一个线程监听蓝牙,一个线程连接蓝牙
 * 和一个线程在连接后传送数据
 * 该类初始化的函数主要有三个
 * 1、初始化的时候建立对象，什么都没做   ------>在出现蓝牙的list前调用这一步
 * 2、start()方法 建立了一个守护用的线程
 * 3、调用connectDevice()方法，及通过传入device，调用特定的devices 并进入ConnectThread线程
 * 在ConnectThread线程中，关闭守护用的线程，并建立BluetoothConnected线程，进入正式的蓝牙使用过程！！！
 * 即这里告诉我们，如果要使用蓝牙，为了和联网的不同，在蓝牙的使用过程中要调用两个方法，即start和conncetDevice
 * 为了方便，变成，将client的连接和设置者两个方法，其中intenet的start是和web进行通讯.....做这样的设置即可.....
 */
public class ClientBluetooth implements ClientCommunicate {
    // Debugging
    private static final String TAG = "ClientBluetooth";
    private static final boolean D = true;
    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private final BluetoothAdapter mAdapter;
    private  Handler mHandler ;
    private AcceptThread mSecureAcceptThread;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    //TODO 改为private 测试
    private BluetoothConnected mConnectedThread;
    private int mState;

    private ServerService serverService;
    // Constants that indicate the current connection state
    //将其定义为其他的变量......
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    /**
     * Constructor. Prepares a new BluetoothChat session.
     * Handler to send messages back to the UI Activity
     */
    public ClientBluetooth() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
    }


    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D) Log.i(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        //TODO 蓝牙发送状态数据
        //mHandler.obtainMessage().sendToTarget();
    }

    /**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void startListening() {
        Log.i(TAG, "startListening");
        mAdapter.enable();
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            Log.i(TAG,"mConnectedThread end_1");
            mConnectedThread.cancel(); mConnectedThread = null;}

        setState(STATE_LISTEN);

        // Start the thread to listen on a BluetoothServerSocket
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread(true);
            mSecureAcceptThread.start();
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread(false);
            mInsecureAcceptThread.start();
        }
        Log.i(TAG, "startListening successed");
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connectDevice(BluetoothDevice device, boolean secure) {
        if (D) Log.i(TAG, "try to connect to device: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            Log.i(TAG,"mConnectedThread end_2");
            mConnectedThread.cancel();
            mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    @Override
    public void updateRemoteInfo() {

    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
        Log.i(TAG, "connected, Socket Type:" + socketType);
        // Cancel the thread that completed the connection
        //这个不必要，会引起很麻烦的问题.......
        //if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            Log.i(TAG,"mConnectedThread end_2");
            mConnectedThread.cancel();
            mConnectedThread = null;}
        // Cancel the accept thread because we only want to connect to one device
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }
        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new BluetoothConnected(socket, socketType,this);
        mConnectedThread.setMyHander(mHandler);
        mConnectedThread.start();
        Message msg = new Message();
        //发送允许通讯的通知
        /***
         * 注意这里，自身为主动模式，则是等待对方连接接入，自己做所有的计算
         * 自身为被动模式，则是接入对方的房间，然后等待对方传输数据
         */
        //Log.i(TAG,"game_rule is_3:"+StaticVariable.CHOSED_RULE);
        if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY){
            msg.what = StaticVariable.BLUE_CONNECT_SUCCESS_ACTIVE;
        }else{
            msg.what = StaticVariable.BLUE_CONNECT_SUCCESS_PASSIVE;
        }
        getMyHandle().sendMessage(msg);
        Log.i(TAG,"AcceptThread in STATE_CONNECTING");

        setState(STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D) Log.i(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            Log.i(TAG,"mConnectedThread end_4");
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }
        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param msg The bytes to write
     * @see BluetoothConnected(String)
     */
    public void write(String msg) {
        mConnectedThread.write(msg);
        //Log.i(TAG,"write info :"+msg);
       // r.write(msg);

    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity

        // Start the service over to restart listening mode
        Log.i(TAG,"connectionFailed");
        Message msg = new Message();
        msg.what = StaticVariable.BLUE_CONNECT_ERROR;
        getMyHandle().sendMessage(msg);
        ClientBluetooth.this.startListening();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    public void connectionLost() {
        // Send a failure message back to the Activity

        // Start the service over to restart listening mode
        Log.i(TAG,"connectionLost");
        Message msg = new Message();
        msg.what = StaticVariable.BLUE_CONNECT_ERROR;
        getMyHandle().sendMessage(msg);
        ClientBluetooth.this.startListening();
    }

    @Override
    public void addInfoObserver(ObserverInfo observerInfo) {
        this.mConnectedThread.addInfoObserver(observerInfo);
    }

    @Override
    public void addMsgObserver(ObserverMsg observerMsg) {
        this.mConnectedThread.addMsgObserver(observerMsg);
    }

    @Override
    public void addCommandObserver(ObserverCommand observerCommand) {
        this.mConnectedThread.addCommandObserver(observerCommand);
    }

    @Override
    public void setMyHandle(Handler myHandle) {
        this.mHandler = myHandle;
    }

    public Handler getMyHandle() {
        return this.mHandler;
    }

    @Override
    public void  sendInfo(String info) {
        this.write(info);
    }

    @Override
    public void startCommunicate() {
        this.startListening();
    }

    @Override
    public void stopCommunicate() {
        this.stop();
    }


    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     * AcceptThread用于启动监控用的线程......
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;

        public AcceptThread(boolean secure)
        {
            BluetoothServerSocket tmp = null;
            mSocketType = secure ? "Secure":"Insecure";

            // Create a new listening server socket
            try {
                if (secure) {
                    tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,
                            MY_UUID_SECURE);
                } else {
                    tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
                            NAME_INSECURE, MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "Socket Type: " + mSocketType +
                    "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + mSocketType);

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                    Log.i(TAG, "AcceptThread is in " + this);
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + mSocketType + " accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (ClientBluetooth.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                Log.i(TAG,"connected by others");
                                //StaticVariable.CHOSED_RULE = StaticVariable.GAME_RULE.ACTIVITY;
                                connected(socket, socket.getRemoteDevice(),
                                        mSocketType);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                Log.i(TAG,"AcceptThread in STATE_CONNECTED");
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            if (D) Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);

        }

        public void cancel() {
            if (D) Log.i(TAG, "Socket Type" + mSocketType + "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
            }
        }
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if (secure) {
                    tmp = device.createRfcommSocketToServiceRecord(
                            MY_UUID_SECURE);
                } else {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(
                            MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            //Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);
            Log.i(TAG, "set mSocketType successed");
            mAdapter.cancelDiscovery();
            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                //主动连接状态
                mmSocket.connect();
                connected(mmSocket, mmSocket.getRemoteDevice(),
                        mSocketType);
                Log.i(TAG,"activity  connected successed");
            } catch (IOException e) {
                // Close the socket
                Log.e(TAG,"connect error :"+e);
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType +
                            " socket during connection failure", e2);
                }
                //出错并进入监听模式
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (ClientBluetooth.this) {
                mConnectThread = null;
            }
/*
            // Start thonnected thread
            Log.i(TAG,"try to start connected thread");
            connected(mmSocket, mmDevice, mSocketType);
            Log.i(TAG,"started connected thread");*/
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }

    public BluetoothConnected getBluetoothConnected() {
        if(mConnectedThread==null){
            Log.i(TAG,"mConnectedThread return null");
            return null;
        }else{
            return mConnectedThread;
        }
    }
}


package yong.tank.Communicate.bluetoothCommunicate;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import yong.tank.Communicate.InterfaceGroup.ClientCommunicate;
import yong.tank.Communicate.InterfaceGroup.ObserverCommand;
import yong.tank.Communicate.InterfaceGroup.ObserverInfo;
import yong.tank.Communicate.InterfaceGroup.ObserverMsg;

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
    public BluetoothConnected mConnectedThread;
    private int mState;

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
    public synchronized void startConnectService() {
        Log.i(TAG, "startConnectService");
        mAdapter.enable();
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

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
        Log.i(TAG, "startConnectService successed");
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
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
        setState(STATE_CONNECTING);
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
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

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
        mConnectedThread.start();
        Log.i(TAG,"BluetoothConnected");

        // Send the name of the connected device back to the UI Activity
/*        Message msg = mHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString(StaticVariable.BLUE_DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);*/

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
        // Create temporary object
        Log.i(TAG,"write info_1:"+msg);
        BluetoothConnected r;
        // Synchronize a copy of the ConnectedThread
/*        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }*/
        // Perform the write unsynchronized
        mConnectedThread.write(msg);
        Log.i(TAG,"write info_2 :"+msg);
       // r.write(msg);

    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
/*        Message msg = mHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString(StaticVariable.BLUE_FAILED_MESSAGE, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);*/

        // Start the service over to restart listening mode
        ClientBluetooth.this.startConnectService();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    public void connectionLost() {
        // Send a failure message back to the Activity
/*        Message msg = mHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString(StaticVariable.BLUE_LOST_MESSAGE, "设备连接中断");
        msg.setData(bundle);
        mHandler.sendMessage(msg);*/

        // Start the service over to restart listening mode
        ClientBluetooth.this.startConnectService();
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
        this.mHandler = mHandler;
    }

    public Handler getMyHandle() {
        return this.mHandler;
    }

    @Override
    public void sendInfo(String info) {
        Log.w(TAG,"sendInfo "+info);
        this.write(info);
    }

    @Override
    public void startCommunicate() {
        this.startConnectService();
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

        public AcceptThread(boolean secure) {
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
                    Log.i(TAG, "AcceptThread is listening " + this);
                    socket = mmServerSocket.accept();
                    Log.i(TAG, "AcceptThread is in " + this);
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (ClientBluetooth.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice(),
                                        mSocketType);
                                Log.i(TAG,"AcceptThread in STATE_CONNECTING");
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
            Log.i(TAG, "........");
            setName("ConnectThread" + mSocketType);
            Log.i(TAG, "set mSocketType successed");
            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();
            Log.i(TAG, "ccancel discovery");
            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                Log.i(TAG,"try to connected");
                mmSocket.connect();
                Log.i(TAG,"connected successed");
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType +
                            " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }
            Log.i(TAG,"is in this place?");
            // Reset the ConnectThread because we're done
            synchronized (ClientBluetooth.this) {
                mConnectThread = null;
            }

            // Start thonnected thread
            Log.i(TAG,"try to start connected thread");
            connected(mmSocket, mmDevice, mSocketType);
            Log.i(TAG,"started connected thread");
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }


}


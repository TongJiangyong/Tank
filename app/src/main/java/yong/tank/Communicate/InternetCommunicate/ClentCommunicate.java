package yong.tank.Communicate.InternetCommunicate;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/26.
 */

public class ClentCommunicate implements  Runnable {
    private Socket client;
    private ClientThread clientThread;
    private String ip;
    private int port;
    private static String TAG ="ClentCommunicate";
    private boolean connectFlag =false;
    private int connectCount = 0;
    private Handler myHandle;

    public ClentCommunicate(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void run() {
        try {
            client = new Socket();
            // client.connect(new InetSocketAddress(Constants.SERVER_IP,
            // Constants.SERVER_PORT), 3000);
            while(!connectFlag){
                client.connect(new InetSocketAddress(ip, port), 3000);
                Log.w(TAG,"ConnectedTest.....");
                connectCount++;
                if(client.isConnected()){
                    connectFlag = true;
                    Log.w(TAG,"Connected to server...");
                    clientThread = new ClientThread(client);
                    new Thread(clientThread).start();
                    connectCount=0;
                    Message msg = new Message();
                    msg.what = StaticVariable.MSG_CONNECT_SUCCESS;
                    myHandle.sendMessage(msg);
                }
                if(connectCount>3){
                    Log.w(TAG,"ConnectedError.....");
                    Message msg = new Message();
                    msg.what = StaticVariable.MSG_CONNECT_ERROR;
                    myHandle.sendMessage(msg);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 直接通过client得到读线程
    public ClientInputThread getClientInputThread() {
        return clientThread.getIn();
    }

    // 直接通过client得到写线程
    public ClientOutputThread getClientOutputThread() {
        return clientThread.getOut();
    }

    // 直接通过client停止读写消息
    public void setIsStart(boolean isStart) {
        clientThread.getIn().setStart(isStart);
        clientThread.getOut().setStart(isStart);
    }

    public Handler getMyHandle() {
        return myHandle;
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

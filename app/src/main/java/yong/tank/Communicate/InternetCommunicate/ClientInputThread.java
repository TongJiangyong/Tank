package yong.tank.Communicate.InternetCommunicate;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import yong.tank.Communicate.ComData.ComDataPackage;
import yong.tank.Communicate.InterfaceGroup.ObserverCommand;
import yong.tank.Communicate.InterfaceGroup.ObserverInfo;
import yong.tank.Communicate.InterfaceGroup.ObserverMsg;
import yong.tank.Communicate.InterfaceGroup.Subject;

/**
 * Created by hasee on 2016/11/26.
 */

public class ClientInputThread implements Runnable,Subject{
    private Socket socket;
    private String msg;
    private boolean isStart = true;
    private BufferedReader in;
    public static final String TAG = "ClientInputThread";
    private Charset charset = Charset.forName("UTF-8");
    // 存放观察者
    private List<ObserverMsg> observerMsgs = new ArrayList<ObserverMsg>();
    private List<ObserverCommand> observerCommands = new ArrayList<ObserverCommand>();
    private List<ObserverInfo> observerInfos = new ArrayList<ObserverInfo>();

    public ClientInputThread(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    @Override
    public void run() {
        try {
            while (isStart) {
                //先测试这个方法......
                Log.w(TAG,"readStart。。。。");
                char[] buff = new char[1024];
                //CharBuffer buff = CharBuffer.allocate(1024);
                msg=in.readLine();
                //msg =CharBuffer.wrap(buff).toString();
                //msg =charset.encode(msg);
                Log.w(TAG,"msg:"+msg);
                this.notifyWatchers(msg);

            }
            in.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void removeInfoObserver(ObserverInfo observerInfos) {
        observerInfos.infoRecived(observerInfos);
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
        //再这里解析，并进行遍历传输
        //Log.w(TAG,msg);
       //进行包的解析工作
        //格式为：
        //msg:{"comDataS":{"commad":"1","object":{"id":12,"name":"test"}},"flag":"654321#"}
        Log.w(TAG,"***********************解析包***********************");
        Log.w(TAG, "flag:"+ ComDataPackage.unpackToF(msg).getFlag());
        Log.w(TAG, "cmmand:"+ComDataPackage.unpackToF(msg).getComDataS().getCommad());
        //Log.w(TAG, "info:"+(String) ComDataPackage.unpackToF(msg).getComDataS().getObject());
        for(ObserverMsg o:observerMsgs){
            o.msgRecived(msg);
        }
        for(ObserverInfo o:observerInfos){
            o.infoRecived(msg);
        }
        for(ObserverCommand o:observerCommands){
            o.commandRecived(msg);
        }

    }
}

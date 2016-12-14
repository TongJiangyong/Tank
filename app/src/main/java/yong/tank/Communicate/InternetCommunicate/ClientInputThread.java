package yong.tank.Communicate.InternetCommunicate;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;
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
 * Created by hasee on 2016/11/26.
 */

public class ClientInputThread implements Runnable,Subject{
    private Socket socket;
    private String msg;
    private boolean isStart = true;
    private BufferedReader in;
    public static final String TAG = "ClientInputThread";
    private Charset charset = Charset.forName("UTF-8");
    private Handler myHander;
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
                Log.w(TAG,"intenet input msg:"+msg);
                this.notifyWatchers(msg);

            }
            in.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            Message msg = myHander.obtainMessage();
            msg.what = StaticVariable.MSG_COMMUNICATE_ERROR;
            myHander.sendMessage(msg);
            e.printStackTrace();
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
        //再这里解析，并进行遍历传输
        //Log.w(TAG,msg);
       //进行包的解析工作
        //格式为：
        //msg:{"comDataS":{"commad":"1","object":{"id":12,"name":"test"}},"flag":"654321#"}
        //解析的逻辑为，将info解析为string，然后统一以string进行处理......
        //Log.w(TAG,"***********************解析包***********************");
        //Log.w(TAG, "flag:"+ ComDataPackage.unpackToF(msg,"testTdo").getFlag());
        //Log.w(TAG, "cmmand:"+ComDataPackage.unpackToF(msg,"testTdo").getComDataS().getCommad());
        //这个解析多了两毫秒......没办法，就这样吧......
        //testDto testDto = (testDto)ComDataPackage.unpackToF(msg,"testTdo").getComDataS().getObject();
        //String test=ComDataPackage.unpackToF(msg,"testTdo").getComDataS().getObject();
        //testDto testDto = ComDataPackage.packageToObject(test);
        //Log.w(TAG, "object::"+testDto.toString());
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

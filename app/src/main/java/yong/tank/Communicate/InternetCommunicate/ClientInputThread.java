package yong.tank.Communicate.InternetCommunicate;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
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
    private boolean isStart = true;
    private InputStream in;
    public static final String TAG = "ClientInputThread";
    private Charset charset = Charset.forName("UTF-8");
    private Handler myHander;
    private byte[] readBuffer = new byte[4096];
    // 存放观察者
    private List<ObserverMsg> observerMsgs = new ArrayList<ObserverMsg>();
    private List<ObserverCommand> observerCommands = new ArrayList<ObserverCommand>();
    private List<ObserverInfo> observerInfos = new ArrayList<ObserverInfo>();

    public ClientInputThread(Socket socket) {
        this.socket = socket;
        try {
            //in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            in = socket.getInputStream();
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
                //Log.w(TAG,"readStart。。。。");
                //char[] buff = new char[1024];
                //CharBuffer buff = CharBuffer.allocate(1024);

                //如果出错，就判定为断开连接......就这么做吧 简化起见.....
                while(in.read(readBuffer)!=-1){
                    //数据收到就有问题.....
                    String readline=new String(readBuffer).trim();
                    //需要重新分配，不然会有问题......
                    readBuffer = new byte[StaticVariable.READ_BYTE];
                    //Log.w(TAG, "*******************************input Thread 收到的数据 *****************************************");
                    String[] readInfos  =readline.split("&");
                    //解析每一个消息
                    for(int i=0;i<readInfos.length;i++){
                        //Log.w(TAG, "input Thread 收到的id: "+readInfos[i]);
                        ComDataF comDataF=null;
                        try {
                            comDataF = ComDataPackage.unpackToF(readInfos[i]);
                            this.notifyWatchers(comDataF);
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
                msg.what = StaticVariable.MSG_COMMUNICATE_ERROR;
                myHander.sendMessage(msg);
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
                //Log.w(TAG, "input Thread 收到的id: "+((testDto)ComDataPackage.packageToObject(comDataF.getComDataS().getObject())).id );
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

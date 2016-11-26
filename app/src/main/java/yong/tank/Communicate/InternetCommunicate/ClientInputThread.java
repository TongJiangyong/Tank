package yong.tank.Communicate.InternetCommunicate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import yong.tank.Communicate.observerInterface.ObserverCommand;
import yong.tank.Communicate.observerInterface.ObserverInfo;
import yong.tank.Communicate.observerInterface.ObserverMsg;
import yong.tank.Communicate.observerInterface.Subject;

/**
 * Created by hasee on 2016/11/26.
 */

public class ClientInputThread implements Runnable,Subject{
    private Socket socket;
    private String msg;
    private boolean isStart = true;
    private ObjectInputStream ois;
    // 存放观察者
    private List<ObserverMsg> observerMsgs = new ArrayList<ObserverMsg>();
    private List<ObserverCommand> observerCommands = new ArrayList<ObserverCommand>();
    private List<ObserverInfo> observerInfos = new ArrayList<ObserverInfo>();

    public ClientInputThread(Socket socket) {
        this.socket = socket;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
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
                msg = (String) ois.readObject();
                // 每收到一条消息，就调用接口的方法，并传入该消息对象，外部在实现接口的方法时，就可以及时处理传入的消息对象了
                // 我不知道我有说明白没有？
                //sendMessager
                this.notifyWatchers(msg);

            }
            ois.close();
            if (socket != null)
                socket.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
    public void notifyWatchers(Object object) {
        //再这里解析，并进行遍历传输

    }
}

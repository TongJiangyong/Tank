package yong.tank.Communicate.InternetCommunicate;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by hasee on 2016/11/26.
 * 实际上只有client一个用户在用，而且远程使用的nio的方法，避免的拥堵的问题
 */

public class ClientOutputThread implements Runnable{
    private Socket socket;
    private ObjectOutputStream oos;
    private boolean isStart = true;
    private String msg;

    public ClientOutputThread(Socket socket) {
        this.socket = socket;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
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
        synchronized (this) {
            notify();
        }
    }

    @Override
    public void run() {
        try {
            while (isStart) {
                if (msg != null) {
                    oos.writeObject(msg);
                    oos.flush();
                }
            }
            oos.close();// 循环结束后，关闭输出流和socket
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


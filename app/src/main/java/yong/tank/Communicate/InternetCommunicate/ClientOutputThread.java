package yong.tank.Communicate.InternetCommunicate;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by hasee on 2016/11/26.
 * 实际上只有client一个用户在用，而且远程使用的nio的方法，避免的拥堵的问题
 */

public class ClientOutputThread implements Runnable{
    private Socket socket;
    private BufferedWriter outupt;
    private static String TAG ="ClientOutputThread";
    private boolean isStart = true;
    private String msg;

    public ClientOutputThread(Socket socket) {
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
                    Log.w(TAG,"send info："+msg);
                    outupt.write(msg);
                    outupt.flush();
                    msg =null;
                }
                //感觉下面没啥用，写一下呗
                synchronized (this) {
                    try {
                        wait();// 发送完消息后，线程进入等待状态
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            outupt.close();// 循环结束后，关闭输出流和socket
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


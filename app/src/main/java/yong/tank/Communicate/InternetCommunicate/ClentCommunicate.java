package yong.tank.Communicate.InternetCommunicate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by hasee on 2016/11/26.
 */

public class ClentCommunicate {
    private Socket client;
    private ClientThread clientThread;
    private String ip;
    private int port;

    public ClentCommunicate(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public boolean start() {
        try {
            client = new Socket();
            // client.connect(new InetSocketAddress(Constants.SERVER_IP,
            // Constants.SERVER_PORT), 3000);
            client.connect(new InetSocketAddress(ip, port), 3000);
            if (client.isConnected()) {
                // System.out.println("Connected..");
                clientThread = new ClientThread(client);
                new Thread(clientThread).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
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

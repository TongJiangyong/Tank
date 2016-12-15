package yong.tank.Game_Activity.presenter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import yong.tank.Communicate.ComData.ComDataF;
import yong.tank.Communicate.ComData.ComDataPackage;
import yong.tank.Communicate.InterfaceGroup.ClientCommunicate;
import yong.tank.Dto.testDto;
import yong.tank.Game_Activity.BlutToothActivty;
import yong.tank.Game_Activity.GameActivity;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/10/27.
 * 不知道为啥，其他的蓝牙只能采用被动连接的方式.....otz
 */

public class GamePresenter {
    private static String TAG = "GamePresenter";
    private Context context;
    private GameActivity gameActivity;
    private BluetoothAdapter bluetoothadpter=null;
    private ClientCommunicate clientCommunicate ;
    private Gson gson = new Gson();
    public GamePresenter(Context context,GameActivity gameActivity){
        this.context=context;
        this.gameActivity = gameActivity;
    }
    //处理Internet、蓝牙连接的一些反馈信息
    private Handler myHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what){
                case StaticVariable.BLUE_TOAST:
                    Toast.makeText(context.getApplicationContext(),  msg.getData().getString("message"), Toast.LENGTH_SHORT).show();// 显示时间较
                    break;
                //蓝牙连接失败
                case StaticVariable.BLUE_CONNECT_ERROR:
                    gameActivity.showToast("蓝牙主动连接错误，转入被动模式...");
                    Log.i(TAG,"blueTooth connect error and turn to passivity mode");
                    if(clientCommunicate==null){
                        Log.i(TAG,"clientBluetooth ERROR_1");
                    }
                    StaticVariable.BLUE_STATE = 0;
                    break;
                //蓝牙主动连接成功
                case StaticVariable.BLUE_CONNECT_SUCCESS_ACTIVE:
                    Log.i(TAG,"蓝牙主动连接成功");
                    StaticVariable.BLUE_STATE = 1;
                    StaticVariable.CHOSED_RULE =StaticVariable.GAME_RULE.ACTIVITY;
                    gameActivity.showToast("蓝牙主动连接成功");
                    testDto testDto_1 = new testDto(12,"test");
                    ComDataF comDataF_1 = ComDataPackage.packageToF("654321#","1",gson.toJson(testDto_1));
                    clientCommunicate.sendInfo(gson.toJson(comDataF_1));
                    //TODO 确认连接成功后，开始蓝牙通信的初始化工作
                    gameActivity.initCommunicate();
                    break;
                //蓝牙被动连接成功
                case StaticVariable.BLUE_CONNECT_SUCCESS_PASSIVE:
                    gameActivity.showToast("蓝牙被动连接成功");
                    StaticVariable.BLUE_STATE = 1;
                    StaticVariable.CHOSED_RULE =StaticVariable.GAME_RULE.PASSIVE;
                    Log.i(TAG,"blueTooth passivity connect success");
                    //TODO 确认连接成功后，开始蓝牙通信的初始化工作
                    gameActivity.initCommunicate();
                    break;
                //允许蓝牙传输
                case StaticVariable.BLUE_ENABLE_SEND_WRITE:
                    gameActivity.showToast("enable blue communicate");
                    Log.i(TAG,"enable blue communicate");
                    break;
                //蓝牙连接故障
                case StaticVariable.BLUE_COMMUNICATE_ERROR:
                    gameActivity.showToast("blueTooth communicate error");
                    Log.i(TAG,"blueTooth communicate error");
                    StaticVariable.BLUE_STATE = 0;
                    //关闭蓝牙对象
                    Log.i(TAG,"BLUE_COMMUNICATE_ERROR");
                    turnOffCommunicate();
                    break;
                //网络连接失败
                case StaticVariable.MSG_CONNECT_ERROR:
                    connectError();
                    break;
                //网络连接成功
                case StaticVariable.MSG_CONNECT_SUCCESS:
                    //connectInit();
                    Toast.makeText(context.getApplicationContext(),  "与TCP服务器连接成功", Toast.LENGTH_SHORT).show();
                    //TODO 确认连接成功后，开始网络通信的初始化工作
                    gameActivity.initCommunicate();
                    break;
                //网络连接故障
                case StaticVariable.MSG_COMMUNICATE_ERROR:
                    communicateError();
                    break;
                //网络连接故障
                case StaticVariable.MSG_COMMUNICATE_OUT:
                    communicateOut();
                    break;
            }
        }
    };


    public void toBluetoothConnectTest(){
        //这一部分，在game的地方配置
        //gameActivity.showToast("蓝牙模式开发中..");
        bluetoothadpter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothadpter.isEnabled()){
            gameActivity.showToast("此程序会默认打开你的蓝牙");
            //	bluetoothadpter.enable();
        }
        // TODO 这里的逻辑整理一下.....
        if(StaticVariable.BLUE_STATE != 1){
            if(bluetoothadpter.getState() == bluetoothadpter.STATE_ON){
                Log.i(TAG,"show device");
                Intent intent = new Intent(this.context,BlutToothActivty.class);
                //建立clientBluetooth的对象
                clientCommunicate.setMyHandle(myHandler);
                //没办法，为了使用这个starrfor result 只能传入MainActivity
                //打开蓝牙list的显示线程
                StaticVariable.CHOSED_MODE = StaticVariable.GAME_MODE.BLUETOOTH;
                this.gameActivity.startActivityForResult(intent,StaticVariable.CHOSED_BLUT_DEVICE);
            }
            else{
                gameActivity.showToast("等待蓝牙开启...");
                // 请求打开 Bluetooth
                Intent requestBluetoothOn = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);

                // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
                requestBluetoothOn
                        .setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

                // 设置 Bluetooth 设备可见时间
                requestBluetoothOn.putExtra(
                        BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                        200);
                // 请求开启 Bluetooth
                this.gameActivity.startActivityForResult(requestBluetoothOn,
                        StaticVariable.REQUEST_CODE_BLUETOOTH_ON);

            }
            //gameActivity.showToast("请先连接蓝牙");
        }
        else{
            gameActivity.showToast("蓝牙程序已启动....不能进入设备选择界面");
        }
    }



    public void tcpConnectTest(){
        //向TCP发送自身Id的注册数据
        clientCommunicate.setMyHandle(myHandler);
        //在这里启动一个线程进行测试编程：.....
        CommunicateThread communicateThread = new CommunicateThread();
        //schedule(TimerTask task, long delay, long period)
        //等待试试10s后开始调度，每隔10s产生一个
        Log.w(TAG,"start to communicate");
        //就用100ms进行测试
        Timer timer  = new Timer();
        timer.schedule(communicateThread,3000,20);
    }

    private SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        //互相communicate的线程
    public class CommunicateThread extends TimerTask {
            int count  =0;
        @Override
        public void run() {
            testDto testDto = new testDto(count,"test");
            ComDataF comDataF = ComDataPackage.packageToF("654321#","8",gson.toJson(testDto));
            Log.i(TAG,"发送数据时间为："+formatTime.format(new Date()));
            clientCommunicate.sendInfo(gson.toJson(comDataF));
            count++;
        }
    }



    public void enableBluetooth() {
        bluetoothadpter.isEnabled();
    }


    public void toBlueTankChose(int resultCode, Intent data) {
        //TODO 准备blue_socket的连接 跳转到tank的选择界面即可.....
        //这个如何和BluetoothChatService结合起来，可以看一些代码
        Uri address = data.getData();
        System.out.println("我是那个远程设备的地址"+address.toString());
        //测试蓝牙的连接情况
        // Get the BLuetoothDevice object
        BluetoothDevice device = bluetoothadpter.getRemoteDevice(address.toString());
        clientCommunicate.connectDevice(device,false);
        // Attempt to connect to the device
    }


    public void turnOffCommunicate() {
        Log.i(TAG,"turnOffBluetooth");
        if(clientCommunicate!=null){
            Log.i(TAG,"end clientBlueTooth");
            clientCommunicate.stopCommunicate();
            clientCommunicate = null;
        }
    }



    public void prepareInternet(ClientCommunicate clientCommunicate) {
        this.clientCommunicate = clientCommunicate;
        //启动Internet通讯
        this.clientCommunicate.startCommunicate();
        //启动Internet测试------以后再加吧....这只是一个确认作用，但是加上去会很麻烦 ，确定连接成功就可以通讯
        this.tcpConnectTest();
    }

    public void prepareBlue(ClientCommunicate clientCommunicate) {
        this.clientCommunicate = clientCommunicate;
        //启动蓝牙通讯
        this.clientCommunicate.startCommunicate();
        //初始化连接bluetooth------以后再加吧....这只是一个确认作用，但是加上去会很麻烦，确定连接成功就可以通讯
        this.toBluetoothConnectTest();
    }

    public void prepareLocal(ClientCommunicate clientCommunicate) {

    }

    private void localConnectTest() {
    }


    private void connectError() {
        Toast.makeText(context.getApplicationContext(),  "与对手连接失败", Toast.LENGTH_SHORT).show();
        //TODO 延迟执行退出程序
    }

    private void communicateError() {
        Toast.makeText(context.getApplicationContext(),  "网络通信故障", Toast.LENGTH_SHORT).show();
    }
    private void communicateOut() {
        Toast.makeText(context.getApplicationContext(),  "对方断开连接", Toast.LENGTH_SHORT).show();
    }
}

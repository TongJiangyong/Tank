package yong.tank.Communicate.ServerService;

import android.util.Log;

import com.google.gson.Gson;

import java.util.Random;

import yong.tank.Communicate.ComData.ComDataF;
import yong.tank.Communicate.ComData.ComDataPackage;
import yong.tank.Communicate.InterfaceGroup.ServerCommunicate;
import yong.tank.Communicate.bluetoothCommunicate.BluetoothConnected;
import yong.tank.Data.GameSendingData;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2017/5/9.
 * 分析后，认为，server和activity之间的通信，主要还是通过管道来处理比较好
 * 管道处理还是稍微有些复杂，折中的处理方式为：
 * 将ServerService线程，写入BlueToothConnected 以及clientInternet的类中，直接调用这两个类的借口，在逻辑上比较说的通
 * //避免出现互相借调的情况..........
 */

public class ServerService implements Runnable,ServerCommunicate{
    /**
     *     需要在GameService中处于activity状态的主机，设计一个服务器程序
     *     服务器程序的主要作用：
     *     1、在确定两方都可以通信后，服务器首先给两方同时发送一个帧开始的命令信号
     *     接收两方发来的数据，并设置帧同步的信息，初始状态使用固定的帧同步信息
     */
    private long serverFrame = 0;
    private boolean serverFlag =false;
    private boolean serverSendingLock = false;
    private boolean isPassiveFrameArrive = false;
    private boolean isActivityFrameArrive = false;
    private GameSendingData activityData;
    private GameSendingData passiveData;
    private static final String TAG = "ServerService";
    private static Gson gson = new Gson();
    //蓝牙通讯所需的适配器
    private BluetoothConnected bluetoothConnected;

    public ServerService() {
        this.serverFlag= true;
    }

    public void serverStop() {
        checkIsDataPrepared();
        this.serverFlag= false;
    }

    @Override
    public void run() {
        //TODO 处理主线程开始前第一次发送所需要的内容
        serverFrame = 0;
        Log.i(TAG,"..........server starding..........");
        while(this.serverFlag){
            synchronized (this) {
                try {
                    wait();// 发送完消息后，线程进入等待状态
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.w(TAG,"error 1");
                }
            }
            Log.i(TAG,"..........server into serverFrame is："+serverFrame+" activityData.getServerFrame():"+activityData.getServerFrame() +" passiveData.getServerFrame():"+passiveData.getServerFrame() );
            //如果帧的序列相同
            if(passiveData!=null&&activityData!=null&&passiveData.getServerFrame()==activityData.getServerFrame()){
                serverSendingLock = true;
                //这里判断每隔9s则刷出一个bonus ，并填充bonus
                if(serverFrame%(StaticVariable.LOGICAL_FRAME*9)==0){
                    Log.i(TAG,"..........server into serverFrame is："+serverFrame+" ,and try to makebonus");
                    int bonusDirectionTemp = new Random().nextInt(2);
                    int bonusTypeTemp = new Random().nextInt(StaticVariable.BONUSPICTURE.length);
                    passiveData.setEnableBonus(true);
                    //将其设置为相反的方向.....
                    passiveData.setBonusDirction(1-bonusDirectionTemp);
                    passiveData.setBonusType(bonusTypeTemp);
                    activityData.setEnableBonus(true);
                    activityData.setBonusDirction(bonusDirectionTemp);
                    activityData.setBonusType(bonusTypeTemp);
                }else{
                    passiveData.setEnableBonus(false);
                    activityData.setEnableBonus(false);
                }
                serverFrame =serverFrame+StaticVariable.KEY_FRAME_COUNT;
                //TODO 处理每一次线程所需要处理的内容......
                //填充passiveData数据，
                passiveData.setEnemyTankDirection(activityData.getMyTankDirection());
                passiveData.setEnemyTankDegree(activityData.getMyTankDegree());
                passiveData.setEnemyTankBulletDistance(activityData.getMyTankBulletDistance());
                passiveData.setEnemyTankBloodNum(activityData.getMyTankBloodNum());
                passiveData.setEnemyTankEnableFire(activityData.getMyTankEnableFire());
                passiveData.setEnemyTankBulletType(activityData.getMyBulletType());
                passiveData.setServerFrame(serverFrame);
                //填充activityData数据
                activityData.setEnemyTankDirection(passiveData.getMyTankDirection());
                activityData.setEnemyTankDegree(passiveData.getMyTankDegree());
                activityData.setEnemyTankBulletDistance(passiveData.getMyTankBulletDistance());
                activityData.setEnemyTankBloodNum(passiveData.getMyTankBloodNum());
                activityData.setEnemyTankEnableFire(passiveData.getMyTankEnableFire());
                activityData.setEnemyTankBulletType(passiveData.getMyBulletType());
                activityData.setServerFrame(serverFrame);
                //发送给activivity
                this.sendDataToActivity(ComDataPackage.packageToF(StaticVariable.REMOTE_DEVICE_ID + "#", StaticVariable.COMMAND_INFO, gson.toJson(activityData)));
                //发送给passive
                this.sendDataToPassive(gson.toJson(ComDataPackage.packageToF(StaticVariable.REMOTE_DEVICE_ID + "#", StaticVariable.COMMAND_INFO, gson.toJson(passiveData))));
                isPassiveFrameArrive =false;
                isActivityFrameArrive =false;
                activityData = null;
                passiveData = null;
                serverSendingLock = false;
            }
        }

    }


    // 这里处理跟服务器是一样的
    public void checkIsDataPrepared() {
        Log.i(TAG,"checkIsDataPrepared.....");
        if((isPassiveFrameArrive&&isActivityFrameArrive)||this.serverFlag==false){
            isPassiveFrameArrive = false;
            isActivityFrameArrive = false;
            synchronized (this) {
                notify();
            }
        }

    }

    /**
     * server 发送数据给Activity端，需要调用bluetoothConnected的方法notify
     * @param comDataF
     */
    @Override
    public void sendDataToActivity(ComDataF comDataF) {
        Log.i(TAG,"sendDataToActivity.....");
        this.bluetoothConnected.notifyWatchers(comDataF);
    }

    /**
     * server 发送数据给Passive端，需要调用bluetoothConnected的方法write
     * @param msg
     */
    @Override
    public void sendDataToPassive(String msg) {
        Log.i(TAG,"sendDataToPassive.....");
        this.bluetoothConnected.write(msg);
    }

    @Override
    public void reciveDataFromActivity(String msg) {
        //TODO 收到数据，置位flag，检查是否可发送
        Log.i(TAG,"reciveDataFromActivity :"+msg);
        ComDataF comDataF = ComDataPackage.unpackToF(msg);
        GameSendingData activityDataTemp = ComDataPackage.packageToObject(comDataF.getComDataS().getObject());
        //如果处于sending状态，则暂时不向下运行
        while(serverSendingLock);
        activityData = activityDataTemp;
        //Log.i(TAG,"reciveDataFromActivity :"+activityData);
        Log.i(TAG,"reciveDataFromActivity and dataFrame is:"+activityData.getServerFrame());
        isActivityFrameArrive = true;
        checkIsDataPrepared();
    }

    @Override
    public void reciveDataFromPassive(ComDataF comDataF) {
        //TODO 收到数据，置位flag，检查是否可发送
        Log.i(TAG,"reciveDataFromPassive :"+comDataF.getComDataS().getCommad());
        GameSendingData passiveDataTemp = ComDataPackage.packageToObject(comDataF.getComDataS().getObject());
        while(serverSendingLock);
        passiveData = passiveDataTemp;
        Log.i(TAG,"reciveDataFromPassive and dataFrame is:"+passiveData.getServerFrame());
        isPassiveFrameArrive = true;
        checkIsDataPrepared();
    }

    public void setBlutoothAdapter(BluetoothConnected bluetoothConnected) {
        this.bluetoothConnected = bluetoothConnected;
    }
}

package yong.tank.Game_Activity.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import yong.tank.Communicate.ComData.ComDataF;
import yong.tank.Communicate.ComData.ComDataPackage;
import yong.tank.Communicate.InterfaceGroup.ClientCommunicate;
import yong.tank.Communicate.InterfaceGroup.ObserverCommand;
import yong.tank.Communicate.InterfaceGroup.ObserverInfo;
import yong.tank.Communicate.InterfaceGroup.ObserverMsg;
import yong.tank.Data.GameSendingData;
import yong.tank.Dto.GameDto;
import yong.tank.LocalRecord.LocalRecord;
import yong.tank.R;
import yong.tank.modal.Bonus;
import yong.tank.modal.DeviceInfo;
import yong.tank.modal.EnemyBlood;
import yong.tank.modal.EnemyBullet;
import yong.tank.modal.EnemyTank;
import yong.tank.modal.Explode;
import yong.tank.modal.MyBlood;
import yong.tank.modal.MyBullet;
import yong.tank.modal.MyTank;
import yong.tank.modal.PlayerPain;
import yong.tank.modal.User;
import yong.tank.modal.abstractGoup.Blood;
import yong.tank.modal.abstractGoup.Tank;
import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

import static yong.tank.tool.StaticVariable.LOCAL_SCREEN_WIDTH;
import static yong.tank.tool.StaticVariable.REMOTE_DEVICE_ID;
import static yong.tank.tool.StaticVariable.REMOTE_PREPARED_INIT_FLAG;
import static yong.tank.tool.StaticVariable.SCALE_SCREEN_HEIGHT;
import static yong.tank.tool.StaticVariable.SCALE_SCREEN_WIDTH;

/**
 * Created by hasee on 2016/11/10.
 */

public class GameService implements ObserverInfo, ObserverMsg, ObserverCommand {
    private GameDto gameDto;
    private static String TAG = "GameService";
    private LocalGameProcess localGameProcess;
    private boolean isGameStartFlag = true;
    private long gameCurrentFrame = 0;
    private int remoteWaitTime = 0;
    //TODO 完成蓝牙相关的线程.....
    //private RemoteGameProcess remoteGameProcess;
    private Context context;
    private Timer timerBonus;
    private Timer timerCommunnicate;
    private LocalRecord<User> localUser = new LocalRecord<User>();
    private Queue<GameSendingData> remoteGameData = new LinkedList<GameSendingData>();
    //初始化远程DTO信息完成，即已完成全部初始化工作
    private boolean remoteDataInitFlag = false;
    //初始化远程交换完成，但是远程DTO等变量还未初始化
    private boolean remoteDeviceACKflag = false;

    //用于制造bonus
    private BonusMaker bonusMaker=null;
    //用于判断自己的tanke是否fire的标志位，主要方便发送数据的时候进行检测
    private boolean isLocalTankOnfire = false;
    private SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    //TODO 测试通讯的代码
    private ClientCommunicate clientCommunicate;
    private Gson gson = new Gson();


    //这是一个专门用于启动程序的handler......，因为架构设计出错，导致service和actiactivity层之间要建立联系，非常不对.....
    private Handler gameActivityHandler;

    //gameDto中设置的remote端的信息暂存处
    private int remoteTankType = 0;

    public GameService(GameDto gameDto, Context context, Handler gameActivityHandler) {
        this.gameDto = gameDto;
        this.context = context;
        this.gameActivityHandler = gameActivityHandler;
        this.isGameStartFlag = true;
    }


    //设置handle的处理
    //注意这里为主线程....handler默认使用主线程的looper
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {//此方法在ui线程运行
            switch (msg.what) {
                case StaticVariable.MSG_TOAST:
                    Toast.makeText(context.getApplicationContext(), msg.getData().getString("message"), Toast.LENGTH_SHORT).show();// 显示时间较
                    break;
            }

        }

    };


    //TODO 一定要保证，调用这个logical函数之前，两台设备之间所有的初始化工作已经完全完成了.......

    /**
     * this.gameDto.getGameProcessFrameCount() 为当前游戏进程的帧，本地每一次执行logicalUpdate则需要+1
     * StaticVariable.KEY_FRAME 此轮游戏的关键帧，按顺序增长
     * gameCurrentFrame 在当前函数中获取的getGameProcessFrameCount()
     * 初始时，各个frame均为0
     */
    public void logicalUpdate() {
        /**
         ***********************如如果是本地模式，则采用本地模式的处理*****************************************
         */
        //Log.i(TAG,"******************logicalUpdate getCurrentTimeCount() is +"+getCurrentTimeCount());
        if (StaticVariable.CHOSED_MODE == StaticVariable.GAME_MODE.LOCAL) {
            //对初始化的处理
            if (localGameProcess == null) {
                //可以在gameThrea中多写点内容
                localGameProcess = new LocalGameProcess();
                //如果是主动模式则启动bonus的线程 被动端不启动Bonus
                //if (StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY) {
                    //启动bonus的线程
                    this.bonusControl();
                //}
                //如果不是本地模式，则启动一个20ms的数据生产者线程
/*                if (StaticVariable.CHOSED_MODE != StaticVariable.GAME_MODE.LOCAL) {
                    Timer timer = new Timer();
                    timer.schedule(new productorThread(), 100, 30);
                }*/
            }
            //Log.i(TAG, "************gameThread.runGame()*************");
            //更新tankControl的逻辑
            localGameProcess.runTank();
            //更新Bonus的位置
            localGameProcess.runBonus();
            //更新血条：
            localGameProcess.runBlood();
            //更新远程的tank信息 只针对本地的模式local mode
            localGameProcess.runLocalEnemy();
        }else{
            /**
             ***********************如果不是本地模式，则采用帧同步的算法*****************************************
             */
            gameCurrentFrame = this.gameDto.getGameProcessFrameCount();
            //对初始化的处理
            if (localGameProcess == null) {
                //可以在gameThrea中多写点内容
                localGameProcess = new LocalGameProcess();
                this.bonusControl();
            }
            //如果当前帧是关键帧，则需要获取服务器的更新数据：
            Log.i(TAG,"check is key_frame gameCurrentFrame "+gameCurrentFrame);
            //如果是第0帧，则需要发送一个空包过去,并运行一些只执行一次的方法
            if(gameCurrentFrame == 0){
                GameSendingData gameFirstData = new GameSendingData(1);
                gameFirstData.setMyTankDirection(this.gameDto.getMyTank().getTankDirection());
                gameFirstData.setMyTankDegree(this.gameDto.getMyTank().getWeaponDegree());
                gameFirstData.setMyTankBulletDistance(this.gameDto.getMyTank().getFirePower());
                gameFirstData.setMyTankBloodNum(this.gameDto.getMyBlood().getBloodNum());
                gameFirstData.setMyBulletType(this.gameDto.getMyTank().getSelectedBullets());
                gameFirstData.setMyTankEnableFire(false);
                gameFirstData.setServerFrame(0);
                //不断发送信息数据
                ComDataF comFirstDataF = ComDataPackage.packageToF(StaticVariable.REMOTE_DEVICE_ID + "#", StaticVariable.COMMAND_INFO, gson.toJson(gameFirstData));
                //专门用来发送信息的方法
                if(StaticVariable.CHOSED_RULE==StaticVariable.GAME_RULE.ACTIVITY){
                    //即，如果本机是ACTIVITY，直接转发给server
                    clientCommunicate.writeToService(gson.toJson(comFirstDataF));
                }else{
                    //即，如果本机是Passvie，发送给activity端
                    clientCommunicate.sendInfo(gson.toJson(comFirstDataF));
                }

                Log.i(TAG,"**************sending first packet****************");
                gameCurrentFrame++;
                Log.i(TAG,"set 0 frame over : "+gameCurrentFrame);
                this.gameDto.setGameProcessFrameCount(gameCurrentFrame);
            }else{
                Log.i(TAG,"**************into recive packet gameCurrentFrame is "+gameCurrentFrame +" StaticVariable.KEY_FRAME is:"+StaticVariable.KEY_FRAME);
                if(gameCurrentFrame == StaticVariable.KEY_FRAME){
                    //查看是否有服务器的更新包,
                    GameSendingData gameData = null;
                    Log.i(TAG,"remoteGameData size is :"+remoteGameData.size());
                    //while(remoteGameData.size()<=0){
                        //等待
                     //   Log.i(TAG,"....wait for data......");
                    //}
                    while(remoteGameData.size()>0&&isGameStartFlag) {
                        Log.i(TAG,"into remoteGameData polling and gameCurrentFrame is :"+gameCurrentFrame);
                        if(remoteWaitTime >= StaticVariable.LOGICAL_FRAME){
                            Log.i(TAG,"***************wait to long********************");
                            remoteWaitTime = 0;
                        }
                        //出列一个数据
                        gameData = remoteGameData.poll();
                        //如果给的数据的帧等于当前的关键帧，则程序向下执行
                        Log.i(TAG,"if break to deal with key frame gameData.getServerFrame():"+gameData.getServerFrame()+" gameCurrentFrame:"+gameCurrentFrame);
                        if (gameData.getServerFrame()==gameCurrentFrame)
                        {
                            Log.i(TAG,"into break");
                            break;
                        }
                        remoteWaitTime++;
                    }
                    //TODO  暂时先做不为空处理
                        Log.i(TAG,"check gameData! = null "+gameData);
                        //更新本地的关键帧序列
                        if (gameData !=null&& (gameData.getServerFrame()==gameCurrentFrame)) {
                            Log.i(TAG,"reciveData Frame is :" +gameData.getServerFrame() +" gameCurrentFrame is: "+gameCurrentFrame);
                            long nextKeyFrame = gameCurrentFrame + StaticVariable.KEY_FRAME_COUNT;
                            //*******************收集准备发送的信息,包括：坦克的移动方向，开火的判断 血条的基本信息***************************
                            //服务器主要负责有：bonus的生成，胜利的判断（血条）   （数据的转发）   击中的判断先将帧同步做好再说，再看怎么处理
                            //*
                            //TODO 初始化要完成的工作........，即付给remote相应的变量.....将当前采集的数据，作为输入包发送 ，发送自己的数据
                            //不断发送信息数据
                            GameSendingData gameSendingData = new GameSendingData(1);
                            gameSendingData.setMyTankDirection(this.gameDto.getPlayerPain().getTankDirection());
                            gameSendingData.setMyTankDegree(this.gameDto.getPlayerPain().getTankDegree());
                            gameSendingData.setMyTankBulletDistance(this.gameDto.getMyTank().getFirePower());
                            //gameSendingData.setMyTankBloodNum(this.gameDto.getMyBlood().getBloodNum());
                            gameSendingData.setMyBulletType(this.gameDto.getMyTank().getSelectedBullets());
                            gameSendingData.setServerFrame(nextKeyFrame);
                            gameSendingData.setMyTankEnableFire(false);
                            //使用tank发送进行判断即可.......
                            if (this.isLocalTankOnfire()) {
                                gameSendingData.setMyTankEnableFire(true);
                                this.setLocalTankOnfire(false);
                            }
                            //不断发送信息数据
                            ComDataF comDataF = ComDataPackage.packageToF(StaticVariable.REMOTE_DEVICE_ID + "#", StaticVariable.COMMAND_INFO, gson.toJson(gameSendingData));
                            if(StaticVariable.CHOSED_RULE==StaticVariable.GAME_RULE.ACTIVITY){
                                Log.i(TAG,"ACTIVITY sendingData to server");
                                clientCommunicate.writeToService(gson.toJson(comDataF));
                            }else{
                                Log.i(TAG,"Passive sendingData to server");
                                clientCommunicate.sendInfo(gson.toJson(comDataF));
                            }
                            //发送完成后，获取服务器得到的数据进行更新：
                            //服务器发过来的数据，包括自己和对方两个部分
                            //更新包括敌方的和自身的数据信息

                            //主要是填充自己和对方的坦克两种：
                            //Log.i(TAG, "gameData.getMyTankDegree is:" + gameData.getMyTankDegree());
                            this.remoteSetMyTank(gameData.getMyTankDegree(), gameData.getMyTankDirection(), gameData.getMyTankEnableFire(),gameData.getMyBulletType());
                            this.remoteSetEnmyTank(gameData.getEnemyTankDegree(), gameData.getEnemyTankDirection(), gameData.getEnemyTankEnableFire(),gameData.getEnemyTankBulletDistance(),gameData.getEnemyTankBulletType());
                            //处理bonus的过程,即如果广播有bonus产生，则产生bonus
                            if (gameData.getEnableBonus()) {
                                bonusMaker.bonusProductor(gameData.getBonusDirction(), gameData.getBonusType());
                            }
                            //填充tank的血条信息-----不必填充，因为血条也是计算得到......
                            //this.gameDto.getMyBlood().setBloodNum(gameData.getMyTankBloodNum());
                            //this.gameDto.getEnemyBlood().setBloodNum(gameData.getEnemyTankBloodNum());
                            //TODO 这里对关键帧的数据填充后，需要进一步计算，如何处理计算方面的内容？？？？
                            StaticVariable.KEY_FRAME = nextKeyFrame;
                            gameCurrentFrame++;
                            this.gameDto.setGameProcessFrameCount(gameCurrentFrame);
                            remoteWaitTime = 0;
                        }else{
                            remoteWaitTime++;
                            if(remoteWaitTime >= StaticVariable.LOGICAL_FRAME){
                                Log.i(TAG,"***************wait to long********************");
                                remoteWaitTime = 0;
                            }
                        }
                }else{
                    //TODO 这里非关键帧的处理方式，,非关键帧，不随大流的变化而变化，可能需要进一步的处理？？？
                    //非关键帧的处理方式：
                    gameCurrentFrame++;
                    Log.i(TAG,"deal with no key frame "+gameCurrentFrame);
                    this.gameDto.setGameProcessFrameCount(gameCurrentFrame);
                }
            }


            //TODO 这里确认一下，需不需要进行处理
            //更新tankControl的逻辑
            localGameProcess.runTank();
            //更新Bonus的位置
            localGameProcess.runBonus();
            //更新血条：
            localGameProcess.runBlood();

            //TODO: 2017/5/8**********************************************************************************
            /*1、写服务器程序
            2、完善蓝牙通讯之间的初始化方法
            3、调试关键帧之外的方法
            4、设计击中判断等机制*/
        }



    }

//这里本地模式调用的bonus产生方法........
    public void bonusControl() {
        timerBonus = new Timer();
        bonusMaker = new BonusMaker();
        //schedule(TimerTask task, long delay, long period)
        //等待试试10s后开始调度，每隔10s产生一个
        if (StaticVariable.CHOSED_MODE == StaticVariable.GAME_MODE.LOCAL) {
            Log.w(TAG, "*********bonus start to maker**************");
            timerBonus.schedule(bonusMaker, 5000, 9000);
        }
    }

    public void stopMakeBonus() {
        timerBonus.cancel();
    }

    public void gameStop() {
        if (localGameProcess != null) {
            //关闭bonus线程
            if (StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY) {
                this.stopMakeBonus();
                this.bonusMaker = null;
            }
            //停止initRemoteActivity函数的循环
            this.isGameStartFlag = false;


            //还原各种初始化的变量：
            resetAllInitParam();
            resetAllObject();
            //设置逻辑部分为0
            localGameProcess = null;
            //还原所有动态创建的对象：坦克，子弹， 血条，爆炸等

        } else {
            Log.w(TAG, "gameThread is  null");
        }
    }
    public void resetAllInitParam(){
        //还原帧数:
        this.gameCurrentFrame = 0;
        StaticVariable.KEY_FRAME = 3;
        this.gameDto.setGameProcessFrameCount(this.gameCurrentFrame);
        //还原等待时间
        this.remoteWaitTime = 0;
        //还原初始化是否完全的变量
        StaticVariable.REMOTE_PREPARED_INIT_FLAG = false;
        //还原蓝牙状态
        StaticVariable.BLUE_STATE = 0;
        //还原确认状态
        this.remoteDataInitFlag = false;
        //初始化远程交换完成，但是远程DTO等变量还未初始化
        this.remoteDeviceACKflag = false;
        //用于判断自己的tanke是否fire的标志位，主要方便发送数据的时候进行检测
        this.isLocalTankOnfire = false;
    }

    public void resetAllObject(){
        this.gameDto.setMyTank(null);
        this.gameDto.setBonus(null);
        this.gameDto.setExplodes(null);
        this.gameDto.setEnemyTank(null);
        this.gameDto.setMyBlood(null);
        this.gameDto.setEnemyBlood(null);
    }


    //GameThread主要用来检测tank爆炸，以及在本地子弹击中bonus的反应
    //之前是使用独立的线程进行运行和检测，这里考虑其他的处理方法.....：
    public class LocalGameProcess {
        public LocalGameProcess() {
            gameDto.getMyTank().setEnableFire(true);
        }
        public void runBonus(){
            if(gameDto.getBonus()!=null){
                gameDto.getBonus().positionUpdate();
            }
        }
        //控制远程的线程运动
        public void runLocalEnemy(){
            if(clientCommunicate!=null){
                clientCommunicate.updateRemoteInfo();
            }
        }

        public void runBlood(){
            if(gameDto.getMyBlood()!=null){
                gameDto.getMyBlood().positionUpdate();
            }
            if(gameDto.getEnemyBlood()!=null){
                gameDto.getEnemyBlood().positionUpdate();
            }
        }

        public void runTank() {

            //更新本地的坦克位置
            gameDto.getMyTank().positionUpdate();
            //更新地方tank的位置信息
            gameDto.getEnemyTank().positionUpdate();
        }

        //这里是判断tank击中相关的代码
        public void tankBulletShot(){
            //下面是本地模式的相关代码 即 本地模式下，Enemy的数据是直接在这里获取的
            //这里也用于判断爆炸和判断bonus击中，即本地用于判断击中 的部分
            //if (StaticVariable.CHOSED_MODE == StaticVariable.GAME_MODE.LOCAL) {
            int num = gameDto.getMyTank().getBulletsFire().size();
            if (num != 0) {
                for (int i = (num - 1); i >= 0; i--) {
                    //TODO 这里注意，统一在爆炸中删除所有子弹，并只能放在最后，不然会有问题
                    //这里设置continue语句来解决这个问题.....
                    //测试击中bonus后用的方法
                    if (myFireBonus(i)) {
                        continue;
                    }
                    //测试爆炸用的方法
                    if (myTankExplode(i)) {
                        continue;
                    }

                }
            }
            if (remoteDataInitFlag) {
                //TODO 检查一下，这个enermyNum为啥为0
                int enermyNum = gameDto.getEnemyTank().getBulletsFire().size();
                //Log.i(TAG,"enermyNum is :"+enermyNum);
                //TODO 这里加上并测试本地模式的程序流程
                if (enermyNum != 0) {
                    for (int i = (enermyNum - 1); i >= 0; i--) {
                        //TODO 这里注意，统一在爆炸中删除所有子弹，并只能放在最后，不然会有问题
                        if (enermyFireBonus(i)) {
                            continue;
                        }
                        if (enermyTankExplode(i)) {
                            continue;
                        }
                    }
                }
                //}
            }
        }
    }

    //测试我的坦克击中bonus的反应
    //1、停止bonus 2、设置selectView 4/产生爆炸并移除子弹，5、根据当前的tank子弹状态，更新子弹状态
    private boolean myFireBonus(int bullet) {
        if (gameDto.getBonus() != null) {
            if (gameDto.getBonus().isInBonusScope(gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                    gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_y())) {
                //***************设置selectView***************
                //设置selectView的图像和文字，并设置tank的子弹属性
                // 注意这里因为是bonus比bullet的种类少一个,所以为：bulletType+1
                int bulletType = this.gameDto.getBonus().getBonusType() + 1;
                //TODO 将message设为方法
                //这里涉及上主线程UI更新的问题，比较麻烦，在android中只能采用message通知的方法
                // selectView并不是采用实时刷新的方法做的，所以比较麻烦
                Message msg = new Message();
                msg.what = StaticVariable.MSG_UPDATE_SELECTBUTTON;
                Bundle bundle = new Bundle();
                bundle.putInt("bullletNum", StaticVariable.BUTTON_NUM_ORIGN);  //设置子弹数量
                bundle.putInt("bullletType", bulletType);  //设置子弹的种类
                bundle.putInt("bullletPicture", StaticVariable.BUTTLE_BASCINFOS[(bulletType)].getPicture());  //设置子弹的图片
                msg.setData(bundle);//mes利用Bundle传递数据
                gameDto.getSelectButtons().get(R.id.selectButton_2).getMyHandler().sendMessage(msg);
                //设置selected为填充状态
                gameDto.getSelectButtons().get(R.id.selectButton_2).setFilled(true);
                //***************根据当前的tank子弹状态，更新子弹状态***************
                //如果当前更新的子弹是bonus的子弹，则更新为最新的bonus子弹
                if (gameDto.getMyTank().getSelectedBullets() != StaticVariable.ORIGIN) {
                    this.gameDto.getMyTank().setSelectedBullets(bulletType);
                    this.gameDto.getMyTank().setSelectedBulletsNum(StaticVariable.BUTTON_NUM_ORIGN);
                }
                //***************设置bonus停止***************
                gameDto.getBonus().setIsBonusFired(true);
                gameDto.setBonus(null);
                //***************产生爆炸***************
                addExplode(gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                        gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_y(),
                        StaticVariable.EXPLODE_TYPE_TANK);

                gameDto.getMyTank().getBulletsFire().remove(bullet);//移除子弹
                return true;
            }
        }
        return false;
    }

    //测试敌方坦克击中bonus的反应
    //1、停止bonus 4/产生爆炸并移除子弹
    private boolean enermyFireBonus(int bullet) {
        if (gameDto.getBonus() != null) {
            if (gameDto.getBonus().isInBonusScope(gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                    gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_y())) {
                //***************设置bonus停止***************
                gameDto.getBonus().setIsBonusFired(true);
                gameDto.setBonus(null);
                //***************产生爆炸***************
                addExplode(gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                        gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_y(),
                        StaticVariable.EXPLODE_TYPE_TANK);
                gameDto.getEnemyTank().getBulletsFire().remove(bullet);//移除子弹
                return true;
            }
        }
        return false;
    }


    //我方坦克的子弹击中效果
    private boolean myTankExplode(int bullet) {
        //如果发现子弹已经停止绘制，则移除
        if (!gameDto.getMyTank().getBulletsFire().get(bullet).isDrawFlag()) {
            gameDto.getMyTank().getBulletsFire().remove(bullet);//移除子弹
            return true;
        }
        //如果打中地方坦克   1、产生爆炸 2、移除子弹 3、削减血条  3、检查游戏是否结束
        if (gameDto.getEnemyTank().isInCircle(gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_y())) {
            /*设置敌方坦克的血条*/
            //***************削减血条***************
            double boldSubtraction = gameDto.getMyTank().getBulletsFire().get(bullet).getBulletBascInfo().getPower();
            //TODO 这里好好想一下，如何实现注册，然后回调函数.....
            gameDto.getEnemyBlood().subtractionBlood(boldSubtraction);
            //***************产生爆炸，移除子弹***************
            addExplode(gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                    gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_y(),
                    StaticVariable.EXPLODE_TYPE_TANK);
            gameDto.getMyTank().getBulletsFire().remove(bullet);//移除子弹
            //***************提示地方坦克的血量,并检查游戏是否结束***************
            if (gameDto.getEnemyBlood().getBloodNum() < 0) {
                //不知道为啥要加这一条Looper.prepare();Looper.loop();可能是TOAST在其他thread中运行的机制？
/*                Message msgInfo = myHandler.obtainMessage();
                msgInfo.what = StaticVariable.MSG_TOAST;
                Bundle bundleMsg = new Bundle();
                bundleMsg.putString("message", "敌方坦克血条已空，我方胜利");  //设置子弹数量
                msgInfo.setData(bundleMsg);//mes利用Bundle传递数据
                myHandler.sendMessage(msgInfo);*/
                //TODO 测试，用于重启游戏
                //在这里进行结束即可.....
                //gameDto.getEnemyBlood().setBloodNum(1);
                Log.i(TAG,"result my tank result:over");
                Message msgResultInfo = gameActivityHandler.obtainMessage();
                msgResultInfo.what = StaticVariable.GAME_OVER;
                Bundle bundleResult = new Bundle();
                bundleResult.putInt("gameResult",StaticVariable.GAME_WIN);  //传递游戏胜利的消息
                msgResultInfo.setData(bundleResult);//mes利用Bundle传递数据
                gameActivityHandler.sendMessage(msgResultInfo);
            }
            return true;
        }

        //如果打中地面 1、产生爆炸 2、移除子弹
        if (gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_y() > StaticVariable.GAMME_GROUND_POSITION) {
            gameDto.getMyTank().getBulletsFire().get(bullet).setDrawFlag(false); //停止绘制
            addExplode(gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                    //gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_y(),
                    StaticVariable.GAMME_GROUND_POSITION,
                    StaticVariable.EXPLODE_TYPE_GROUND);
            gameDto.getMyTank().getBulletsFire().remove(bullet);//移除子弹
            return true;
        }
        return false;
    }


    //敌方坦克的子弹击中效果
    // 包括1、判断是否应该移除子弹 2、增加爆炸的效果.....
    private boolean enermyTankExplode(int bullet) {
        //TODO 测试explodeewr
        //如果发现子弹已经停止绘制，则移除
        if (!gameDto.getEnemyTank().getBulletsFire().get(bullet).isDrawFlag()) {
            gameDto.getEnemyTank().getBulletsFire().remove(bullet);//移除子弹
            return true;
        }
        //如果打中我方坦克
        if (gameDto.getMyTank().isInCircle(gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_y())) {
            /*设置敌方坦克的血条*/
            //***************削减血条***************
            double boldSubtraction = gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletBascInfo().getPower();
            //TODO 这里好好想一下，如何实现注册，然后回调函数.....
            gameDto.getMyBlood().subtractionBlood(boldSubtraction);
            //***************产生爆炸，移除子弹***************
            addExplode(gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                    gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_y(),
                    StaticVariable.EXPLODE_TYPE_TANK);
            gameDto.getEnemyTank().getBulletsFire().remove(bullet);//移除子弹
            //***************提示敌方坦克的血量,并检查游戏是否结束***************
            if (gameDto.getMyBlood().getBloodNum() < 0) {
                //不知道为啥要加这一条Looper.prepare();Looper.loop();可能是TOAST在其他thread中运行的机制？
/*                Message msgInfo = myHandler.obtainMessage();
                msgInfo.what = StaticVariable.MSG_TOAST;
                Bundle bundleMsg = new Bundle();
                bundleMsg.putString("message", "我方坦克血条已空，敌方胜利");  //设置子弹数量
                msgInfo.setData(bundleMsg);//mes利用Bundle传递数据
                myHandler.sendMessage(msgInfo);*/
                //测试，用于重启游戏
                //gameDto.getMyBlood().setBloodNum(1);
                //gameDto.getEnemyBlood().setBloodNum(1);
                Log.i(TAG,"result enemy tank result:over");
                Message msgResultInfo = gameActivityHandler.obtainMessage();
                msgResultInfo.what = StaticVariable.GAME_OVER;
                Bundle bundleResult = new Bundle();
                bundleResult.putInt("gameResult",StaticVariable.GAME_LOST);  //传递游戏胜利的消息
                msgResultInfo.setData(bundleResult);//mes利用Bundle传递数据
                gameActivityHandler.sendMessage(msgResultInfo);
            }
            return true;
        }
        //如果打中地面
        if (gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_y() > StaticVariable.GAMME_GROUND_POSITION) {
            gameDto.getEnemyTank().getBulletsFire().get(bullet).setDrawFlag(false); //停止绘制
            addExplode(gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                    //gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_y(),
                    StaticVariable.GAMME_GROUND_POSITION,
                    StaticVariable.EXPLODE_TYPE_GROUND);
            gameDto.getEnemyTank().getBulletsFire().remove(bullet);//移除子弹
            return true;
        }
        return false;
    }


    //产生爆炸的方法
    public void addExplode(float dx, float dy, int type) {
        Explode explode = null;
        if (type == StaticVariable.EXPLODE_TYPE_GROUND) {
            explode = new Explode(StaticVariable.EXPLODESONGROND,
                    dx,
                    dy,
                    type);
        } else {
            explode = new Explode(StaticVariable.EXPLODESONTANK,
                    dx,
                    dy,
                    type);
        }
        gameDto.getExplodes().add(explode);//发生爆炸
        //每次增加爆炸，则向client发送一个bonus
/*        if (StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY) {
            Tool.sendNewExplode(clientCommunicate, explode);
        }*/
    }


    //产生bonus的线程
    //这里注意，一定要确定，bonus会被直接用完
    public class BonusMaker extends TimerTask {
        @Override
        public void run() {
            //获取bonus的路径
            Log.w(TAG, "********************************产生一个bonus****************************");
            //随机产生一个bonus，注意这里的bonus和子弹是绑定的
            int bonusType = new Random().nextInt(StaticVariable.BONUSPICTURE.length);
            int bonusDirection =new Random().nextInt(2); //生成随机方向
            this.bonusProductor(bonusDirection,bonusType);
        }

        public void bonusProductor(int bonusDirection,int bonusType){
            Log.w(TAG, "********************************产生一个bonus****************************");
            //随机产生一个bonus，注意这里的bonus和子弹是绑定的
            Bitmap bonusPicture = BitmapFactory.decodeResource(context.getResources(), StaticVariable.BONUSPICTURE[bonusType]);//0~length-1之间的数
            Bonus bonus = new Bonus(bonusPicture, bonusDirection, bonusType);
            //设置bonus
            gameDto.setBonus(bonus);
        }

    }

    public ClientCommunicate getClientCommunicate() {
        return clientCommunicate;
    }

    public void setClientCommunicate(ClientCommunicate clientCommunicate) {
        //设置后communicate的接口后，立刻设置监听
        this.clientCommunicate = clientCommunicate;
        this.clientCommunicate.addInfoObserver(this);
        this.clientCommunicate.addMsgObserver(this);
        this.clientCommunicate.addCommandObserver(this);
        //下面是与非本地模式相关的代码,即启动先关的消耗线程即可.......
        if (!(StaticVariable.CHOSED_MODE == StaticVariable.GAME_MODE.LOCAL)) {
            //启动一个消耗的线程
            Timer timer = new Timer();
            //发送和接收的时间均为33.....
            //timer.schedule(new consumeThread(), 3000, 30);
        }
    }

/*    public class productorThread extends TimerTask {
        @Override
        public void run() {
            //如果初始化prepare完成,就开始传输数据
            if (REMOTE_PREPARED_INIT_FLAG) {
                ComDataF comDataF = null;
                String gameDtoString = null;
                try {
                    //不断发送信息数据
                    comDataF = ComDataPackage.packageToF(StaticVariable.REMOTE_DEVICE_ID + "#", StaticVariable.COMMAND_INFO, gson.toJson(gameDto));
                    clientCommunicate.sendInfo(gson.toJson(comDataF));
                } catch (Exception e) {
                    Log.i(TAG, "send Error:" + e);
                }
            }
        }
    }*/


/*    //互相communicate的线程
    public class consumeThread extends TimerTask {
        @Override
        public void run() {
            Log.i(TAG, "remoteGameDtos num is " + remoteGameData.size());
            if (remoteGameData.size() != 0) {
                GameDto gameDtoTemp = remoteGameData.poll();
                //在这里初始化Enermy坦克的信息
                if (!remoteDtoInitFlag && gameDtoTemp.getMyTank() != null && gameDtoTemp.getMyBlood() != null && gameDto.getEnemyTank() == null && gameDto.getEnemyBlood() == null) {
                    //初始化敌方的变量......
                    Log.i(TAG, "初始化敌方的变量......");
                    initRemoteDto(gameDtoTemp);
                    //允许敌方坦克发射
                    gameDto.getEnemyTank().setEnableFire(true);
                    //初始化成功的标志
                    remoteDtoInitFlag = true;
                }

                //如果初始化完成，即开始进行数据消费工作
                //消费工作分为两类 1、activity的消费工作   2、passive的消费工作 3公共的消费工作
                if (remoteDtoInitFlag) {
                    *//**设置EnemyTank相关的属性**//*
                    //这里是不论activity还是passive都要处理的部分：
                    //TODO 注意这里要加上不同分辨率的处理.......注意设置角度为负
                    //设置WeaponDegree相关的信息
                    Log.d(TAG, "EnemyTank weapenDegree is " + gameDtoTemp.getMyTank().getWeaponDegree());
                    gameDto.getEnemyTank().setWeaponDegree(-gameDtoTemp.getMyTank().getWeaponDegree());
                    //设置enermy的坦克相关信息
                    //TODO 注意这里，对坐标进行了转换 但是Y坐标为设定为固定值
                    //Log.i(TAG,"recive origin x is "+gameDtoTemp.getMyTank().getTankPosition_x()+",origin y is "+gameDtoTemp.getMyTank().getTankPosition_y() +"SCALE_SCREEN_WIDTH is :"+SCALE_SCREEN_WIDTH);
                    int tempX = StaticVariable.LOCAL_SCREEN_WIDTH - gameDto.getMyTank().getTankPicture().getWidth() - (int) (gameDtoTemp.getMyTank().getTankPosition_x() * SCALE_SCREEN_WIDTH);
                    int tempY = gameDto.getMyTank().getTankPosition_y();
                    double tempBlood = gameDtoTemp.getMyBlood().getBloodNum();
                    //Log.d(TAG,"EnemyTank weapenDegree is "+gameDtoTemp.getMyTank().getWeaponDegree() + " eneryTank x："+tempX+", y:" +tempY);
                    gameDto.getEnemyTank().setTankPosition_x(tempX);
                    gameDto.getEnemyTank().setTankPosition_y(tempY);
                    //设置血条相关信息
                    //设置EnemyBlood相关的属性
                    gameDto.getEnemyBlood().setBloodNum(tempBlood);
                    //TODO 这里的设置，很有问题，很容易出错，想想解决办法 包括子弹和爆炸、bonus以及属于的问题.....
                    //设置bonus相关信息 无论主动被动，都需要知道bonus摧毁没有
                    if (gameDto.getBonus() != null && gameDtoTemp.getBonus() != null) {
                        gameDto.getBonus().setIsBonusFired(gameDtoTemp.getBonus().isBonusFired());
                    }

                    //passive的消费工作  //主要有爆炸、bonus
                    if (StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.PASSIVE) {
                        //对passive端，要设置bonus的相关属性，如果本地的bonus初始化成功，则，开始设置其坐标
                        if (gameDto.getBonus() != null && gameDtoTemp.getBonus() != null) {
                            gameDto.getBonus().setBonus_x((int) (gameDtoTemp.getBonus().getBonus_x() * SCALE_SCREEN_WIDTH));
                            gameDto.getBonus().setBonus_y((int) (gameDtoTemp.getBonus().getBonus_y() * SCALE_SCREEN_HEIGHT));
                        }
                        //对passive端，设置子弹相关属性
                        if (gameDtoTemp.getMyTank().getBulletsFire().size() != 0 && gameDto.getEnemyTank().getBulletsFire().size() != 0) {
                            //直到为true 直到不为负，才给子弹赋值,如果没有绘制完，则会一直在循环体中.....
                            //TODO 测试看要不要这句
                            //这种处理方法很不好，考虑用另一个线程来处理
                            while (!gameDto.getEnemyTank().isBulletDrawOver()) ;
                            //TODO 感觉子弹的处理还是会很麻烦 这里也有可能出错，即本地的子弹数目不匹配的问题
                            //循环设置子弹的绘制属性，其中循环的参数为获取的子弹链属性 子弹一定要从低到高设置起来.......
                            for (int i = 0; i < gameDtoTemp.getMyTank().getBulletsFire().size(); i++) {
                                //gameDto.getEnemyTank().getBulletsFire().get(i).setBulletDistance(gameDtoTemp.getMyTank().getBulletsFire().get(i).getBulletDistance());
                                gameDto.getEnemyTank().getBulletsFire().get(i).setBulletDegree(gameDtoTemp.getMyTank().getBulletsFire().get(i).getBulletDegree());
                                gameDto.getEnemyTank().getBulletsFire().get(i).setBulletType(gameDtoTemp.getMyTank().getBulletsFire().get(i).getBulletType());
                                gameDto.getEnemyTank().getBulletsFire().get(i).setBulletPosition_x((int) (gameDtoTemp.getMyTank().getBulletsFire().get(i).getBulletPosition_x() * SCALE_SCREEN_WIDTH));
                                gameDto.getEnemyTank().getBulletsFire().get(i).setBulletPosition_y((int) (gameDtoTemp.getMyTank().getBulletsFire().get(i).getBulletPosition_y() * SCALE_SCREEN_HEIGHT));
                                gameDto.getEnemyTank().getBulletsFire().get(i).setDrawFlag(gameDtoTemp.getMyTank().getBulletsFire().get(i).isDrawFlag());
                            }
                        }
                    }
                }
            }

        }
    }*/


    /********************************下面是与初始化相关的代码***********************************************/
    /***
     * 初始化主要包括两个过程
     * 1、本地数据的初始化
     * 2、远程连接成功后，数据的初始化，
     * 3两者初始化成功后，才能进行数据的通行
     **/

    //TODO ,追溯一下，从oncreate，如何到达这里.....
    public void initAllDataInfo() {
        //TODO 完成游戏启动前自身数据的初始化
        this.initLocalDto();
        //TODO 确定通信连接后，完成远程连接工作后其他数据的初始化
        Log.i(TAG, "game_mode is :" + StaticVariable.CHOSED_MODE);
        Log.i(TAG, "game_rule is_1:" + StaticVariable.CHOSED_RULE);
        if (StaticVariable.CHOSED_MODE == StaticVariable.GAME_MODE.LOCAL) {
            Log.i(TAG, "START GAME local。。。。。。");
            this.initLocalActivity();
        } else {
            Log.i(TAG, "START GAME remote。。。。。。");
            this.initRemoteActivity();
        }

    }


    /**
     * 本地初始化本地dto相关的代码
     */
    private void initLocalDto() {
        //TODO 初始化explode
        for (int i = 0; i < StaticVariable.EXPLODESPICTURE_GROUND.length; i++) {
            StaticVariable.EXPLODESONGROND[i] = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.EXPLODESPICTURE_GROUND[i]);
        }
        for (int i = 0; i < StaticVariable.EXPLODESPICTURE_TANKE.length; i++) {
            StaticVariable.EXPLODESONTANK[i] = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.EXPLODESPICTURE_TANKE[i]);
        }
        //TODO 测试初始化tank  测试添加的东西在gameservice中试试
        MyTank myTank = (MyTank) initTank(this.gameDto.getTankType(), true);
        gameDto.setMyTank(myTank);
        //TODO 测试初始化玩家控制图标
        PlayerPain playerPain = new PlayerPain();
        gameDto.setMyTank(myTank);
        gameDto.setPlayerPain(playerPain);
        //TODO 测试初始化装载血条的视图
        MyBlood myBlood = (MyBlood) initBlood(true);
        gameDto.setMyBlood(myBlood);
    }

    /**
     * 本地初始化远程dto相关的代码
     */
    private void initLocalEnemy(int tankType) {
        //初始化enemyTank
        EnemyTank enemyTank = (EnemyTank) initTank(tankType, false);
        //初始化blood
        EnemyBlood enemyBlood = (EnemyBlood) initBlood(false);
        gameDto.setEnemyBlood(enemyBlood);
        gameDto.setEnemyTank(enemyTank);
    }

    private Tank initTank(int tankType, Boolean isMyTank) {
        Bitmap tankPicture_temp = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.TANKBASCINFO[tankType].getPicture());
        Bitmap tankPicture = null;
        Bitmap armPicture = null;
        Tank tank = null;
        if (isMyTank) {
            armPicture = BitmapFactory.decodeResource(this.context.getResources(), R.mipmap.gun);
            tankPicture = Tool.reBuildImg(tankPicture_temp, 0, 1, 1, false, true);
            tank = new MyTank(tankPicture, armPicture, tankType, StaticVariable.TANKBASCINFO[tankType]);
        } else {
            armPicture = BitmapFactory.decodeResource(this.context.getResources(), R.mipmap.gun_symmetry);
            tankPicture = Tool.reBuildImg(tankPicture_temp, 0, 1, 1, false, false);
            tank = new EnemyTank(tankPicture, armPicture, tankType, StaticVariable.TANKBASCINFO[tankType]);
        }

        return tank;
    }

    private Blood initBlood(Boolean isMyBlood) {
        //TODO 如果是true会找其他图片
        //TODO 这里暂时先别做.....以后再改....
        Bitmap blood_picture = null;
        blood_picture = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.BLOOD);
        Bitmap power_picture = null;
        power_picture = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.POWERR);
        Bitmap bloodBlock_picture = null;
        bloodBlock_picture = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.BLOODBLOCK);
        Blood blood = null;
        if (isMyBlood) {
            blood = new MyBlood(blood_picture, power_picture, bloodBlock_picture, 1, 1);
        } else {
            Bitmap bloodBlock_picture_temp = Tool.reBuildImg(bloodBlock_picture, 0, 1, 1, false, true);
            blood = new EnemyBlood(blood_picture, power_picture, bloodBlock_picture_temp, 1, 1);
        }
        return blood;
    }

    //敌方数据发射子弹.....
    public void enemyTankOnFire(int degree,double distance,int enemyBulletType){
        //***************发射子弹*************
        EnemyBullet enemyBullet = initEnemyBullet(enemyBulletType,degree,distance);
        //在tank中加入子弹
        //Log.i(TAG,"getFirePath is *************************:"+enemyBullet.getFirePath().size());
/*        for(int i=0;i<enemyBullet.getFirePath().size();i++){
            Log.i(TAG,"getFirePath position is :"+enemyBullet.getFirePath().get(i).getX()+","+enemyBullet.getFirePath().get(i).getY());
        }*/
        this.gameDto.getEnemyTank().addBuleetFire(enemyBullet);
        //Log.i(TAG,"enermyNum is*************************:"+gameDto.getEnemyTank().getBulletsFire().size());
        //***************重置装填的时间*************
        //如果子弹的类型不是连续弹，则设置装填时间
        if(this.gameDto.getMyTank().getSelectedBullets()==StaticVariable.S_S)
        {
            this.gameDto.getEnemyBlood().setPowerNum(1);
        }else{
            this.gameDto.getEnemyBlood().setPowerNum(0);
            //设置禁止发射,直到装填时间回复
            this.gameDto.getEnemyBlood().setAllowFire(false);
        }
    }

    //敌方数据用于初始化子弹
    //TODO 注意在local模式在，子弹的路径采用完全不同的计算法方法....即使用必中的发射方法  .......
    //但是，计算切线的方法太麻烦了.....要计算出抛物线公式之后，然后再带入X做计算，以后处理吧....
    // 这里做将初速度随机增减处理后，计算坐标即可.....
    //对AI模式，子弹的处理....同AI模式即可，即，通过传递真实的子弹路径即可
    //对非AI模式，子弹的处理为每次赋值，都创建新的bullet的list对象即可......
    private EnemyBullet initEnemyBullet(int bulletType,int degree,double distance){
        Bitmap bullet_temp = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.BUTTLE_BASCINFOS[bulletType].getPicture());
        Bitmap bulletPicture = Tool.reBuildImg(bullet_temp,0,1,1,false,true);
        //初始化子弹的角度
        int initDegree = 0;
        double initDistance  = 0;
        //对本地模式的处理 这里不应该写在这里，不过随便啦，就这样呗......
        if (StaticVariable.CHOSED_MODE == StaticVariable.GAME_MODE.LOCAL){
            initDegree = degree;
            initDistance = distance;
            //给一点随机误差
            initDistance = initDistance+Tool.randomDoubleMaker(-0.2,0.2); //射程给0.2的误差
            initDegree = initDegree+(int)Tool.randomDoubleMaker(-10,10); //角度给10的误差
            //TODO 这里将distance做随机处理
            if(initDistance<=0.2||initDistance>=1){
                //小于0则设定一个固定值
                initDistance= 0.5;
            }
            //默认收到的degree均小于0
            if(initDegree>0){
                initDegree = initDegree-10;
            }
            //Log.i(TAG,"initDegree is *************************:"+initDegree);
            //Log.i(TAG,"initDistance is *************************:"+initDistance);
            //对remote模式的处理.....
        }else{
            initDegree = degree;
            initDistance = distance;
        }

        //Log.i(TAG,"init x is *************************:"+this.gameDto.getEnemyTank().getWeaponPoxition_x());
        //Log.i(TAG,"init y is *************************:"+this.gameDto.getEnemyTank().getWeaponPoxition_y());
        //注意这里，角度为负数
        double bulletV_x=StaticVariable.BUTTLE_BASCINFOS[this.gameDto.getEnemyTank().getSelectedBullets()].getSpeed()*initDistance*Math.cos(Math.toRadians(initDegree));
        double bulletV_y=-StaticVariable.BUTTLE_BASCINFOS[this.gameDto.getEnemyTank().getSelectedBullets()].getSpeed()*initDistance*Math.sin(Math.toRadians(initDegree));
        //Log.i(TAG,"initDistance is "+initDistance+" initDegree:"+initDegree+" bulletV_x:" +bulletV_x+" bulletV_y :"+bulletV_y);
        EnemyBullet enemyBullet = new EnemyBullet(bulletPicture,bulletType,bulletV_x,bulletV_y,
                this.gameDto.getEnemyTank().getWeaponPoxition_x(),this.gameDto.getEnemyTank().getWeaponPoxition_y());
        enemyBullet.setBulletDegree(initDegree);
        enemyBullet.setBulletDistance(initDistance);

        //计算并初始化子弹的路径,修改后，enemyTank并不需要多余的path
        //允许发射....
        enemyBullet.setDrawFlag(true);
        //初始化坦克的位置
        //bullet.setBulletPosition_x(this.gameDto.getMyTank().getWeaponPoxition_x());
        //bullet.setBulletPosition_y(this.gameDto.getMyTank().getWeaponPoxition_y());
        return enemyBullet;
    }

    //让我的tank发送子弹
    public void myTankOnFire(int myTankBulletType) {
        //***************发射子弹*************
        MyBullet myBullet = initMyBullet(myTankBulletType);
        //在tank中加入子弹
        this.gameDto.getMyTank().addBuleetFire(myBullet);
        //***************重置装填的时间*************
        //如果子弹的类型不是连续弹，则设置装填时间
        if (this.gameDto.getMyTank().getSelectedBullets() == StaticVariable.S_S) {
            this.gameDto.getMyBlood().setPowerNum(1);
        } else {
            this.gameDto.getMyBlood().setPowerNum(0);
            //设置禁止发射,直到装填时间回复
            this.gameDto.getMyBlood().setAllowFire(false);
        }
        //***************更新bullet的计数*************
        //如果当前的子弹类型不为初始类型，则需要更新计数
        if (gameDto.getMyTank().getSelectedBullets() != StaticVariable.ORIGIN) {
            //子弹计数
            //这里涉及上主线程UI更新的问题，比较麻烦，在android中只能采用message通知的方法
            // selectView并不是采用实时刷新的方法做的，所以比较麻烦
            Message msg = new Message();
            msg.what = StaticVariable.MSG_UPDATE_LEFT_BULLET_NUM;
            gameDto.getSelectButtons().get(R.id.selectButton_2).getMyHandler().sendMessage(msg);
            this.gameDto.getMyTank().setSelectedBulletsNum(gameDto.getSelectButtons().get(R.id.selectButton_2).getBulletNum());
        }
    }
    //初始化我的tank子弹
    private MyBullet initMyBullet(int bulletType){
        Bitmap bullet_temp = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.BUTTLE_BASCINFOS[bulletType].getPicture());
        Bitmap bulletPicture = Tool.reBuildImg(bullet_temp,0,1,1,false,false);
        //TODO 这里可能子弹的方向不同
        double bulletV_x=StaticVariable.BUTTLE_BASCINFOS[this.gameDto.getMyTank().getSelectedBullets()].getSpeed()*this.gameDto.getMyTank().getFirePower()*Math.cos(Math.toRadians(-this.gameDto.getMyTank().getWeaponDegree()));
        double bulletV_y=-StaticVariable.BUTTLE_BASCINFOS[this.gameDto.getMyTank().getSelectedBullets()].getSpeed()*this.gameDto.getMyTank().getFirePower()*Math.sin(Math.toRadians(-this.gameDto.getMyTank().getWeaponDegree()));
        MyBullet myBullet = new MyBullet(bulletPicture,bulletType,bulletV_x,bulletV_y,
                this.gameDto.getMyTank().getWeaponPoxition_x(),this.gameDto.getMyTank().getWeaponPoxition_y());
        //初始化坦克的性能
        myBullet.setBulletDegree(-this.gameDto.getMyTank().getWeaponDegree());
        myBullet.setBulletDistance(this.gameDto.getMyTank().getFirePower());
        //Log.i(TAG,"distance test is :"+distance+" ******************");
        //计算并初始化子弹的路径
        myBullet.setFirePath(Tool.getMyBulletPath(this.gameDto.getMyTank().getWeaponPoxition_x(),
                this.gameDto.getMyTank().getWeaponPoxition_y(),
                this.gameDto.getMyTank().getFirePower(),
                -this.gameDto.getMyTank().getWeaponDegree(),
                false,this.gameDto.getMyTank().getSelectedBullets()));
        //允许绘制路径
        myBullet.setDrawFlag(true);
        //初始化坦克的位置
        //bullet.setBulletPosition_x(this.gameDto.getMyTank().getWeaponPoxition_x());
        //bullet.setBulletPosition_y(this.gameDto.getMyTank().getWeaponPoxition_y());
        return myBullet;
    }

    /**
     * 远程初始化相关的代码
     */

    //发送ID到server/activity端 ，初始化本地的地方tank信息，同时，passive端，首先开始进行主动的通讯工作
    private void initRemoteActivity() {
        //网络模式需要向服务器发送东西.....
        if (StaticVariable.CHOSED_MODE == StaticVariable.GAME_MODE.INTERNET) {
            Tool.sendSelfIdToServer(this.clientCommunicate);
        }
        Log.i(TAG, "mode:" + StaticVariable.CHOSED_RULE);
        if (StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.PASSIVE) {
            while (!remoteDeviceACKflag&&isGameStartFlag) {
                Log.i(TAG, "PASSIVE端发起连接.......");
                if (this.clientCommunicate != null) {
                    Log.i(TAG, "clientCommunicate is ready");
                } else {
                    Log.i(TAG, "clientCommunicate is none");
                }
                Tool.sendSelfIdToActive(this.clientCommunicate);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //在这里等待初始化信息传输完全 ,如果REMOTE_PREPARED_INIT_FLAG不变，则一直在这里等待.....
        while((!REMOTE_PREPARED_INIT_FLAG)&&isGameStartFlag){};
        Log.i(TAG, "remoteTankType is :" + this.remoteTankType);
        initLocalEnemyInfo(this.remoteTankType);
    }

    //local暂时直接开始游戏即可
    private void initLocalActivity() {
        //TODO ，在这里初始化敌方坦克等信息，不再数据接收中处理
        this.initLocalEnemyInfo(this.gameDto.getMyTank().getTankType());
        this.startGame();
    }

    //初始化和本地程序相关的信息，以前是在recive信息重初始化，这里要改变
    private void initLocalEnemyInfo(int tankType) {
        //初始化敌方的变量......
        //Log.i(TAG,"初始化敌方的变量......");
        initLocalEnemy(tankType);
        //允许敌方坦克发射
        gameDto.getEnemyTank().setEnableFire(true);
        this.remoteDataInitFlag = true;
    }

    private void startGame() {
        Message msgInfo = gameActivityHandler.obtainMessage();
        msgInfo.what = StaticVariable.GAME_STARTED;
        gameActivityHandler.sendMessage(msgInfo);
    }

    /*****************************************
     * 这里是与通信相关的方法*****时间差大概在10~30ms之间
     **********************************/

    //主要用来向buffer中填充数据
    @Override
    public void infoRecived(GameSendingData gameData) {
        Log.i(TAG,"infoRecived"+gameData.getEnemyTankDirection());
        remoteGameData.offer(gameData);   //入列
        //下面是与本地模式相关的代码 联网和蓝牙模式，采用在update函数中处理该数据......
        //TODO 一定要在这里强制修改这种模式........
        if (StaticVariable.CHOSED_MODE == StaticVariable.GAME_MODE.LOCAL&&gameData.dataFlag == 0) {
            GameSendingData gamedTempData = remoteGameData.poll();
            //在这里初始化Enermy坦克的信息
            //TODO 游戏的初始化不应该在这里开始，应该在进行infoRecived工作之前开始，一定要注意处理这个......
/*            if (gameDtoTemp.getMyTank() != null && gameDtoTemp.getMyBlood() != null && this.gameDto.getEnemyTank() == null && this.gameDto.getEnemyBlood() == null) {
                //初始化敌方的变量......
                //Log.i(TAG,"初始化敌方的变量......");
                initRemoteDto(gameDtoTemp);
                //允许敌方坦克发射
                gameDto.getEnemyTank().setEnableFire(true);
                this.remoteDtoInitFlag = true;
            }*/
            //如果初始化完成，即开始进行设置工作
            if (this.remoteDataInitFlag) {
                /**设置EnemyTank相关的属性**/

                //TODO 注意这里要加上不同分辨率的处理.......注意设置角度为负 这里，要按照帧同步的逻辑，进行处理......
/*                this.gameDto.getEnemyTank().setWeaponDegree(-gameData.getEnemyTankDegree());
                this.gameDto.getEnemyTank().setTankDirection(-gameData.getEnemyTankDirection());
                //Log.i(TAG,"gameData.getEnemyTankDirection():"+gameData.getEnemyTankDirection());
                //设置enemy坦克的开火属性
                if(gameData.getEnemyTankEnableFire()){
                    this.enemyTankOnFire();
                }*/
                //注意这里，由于方法和蓝牙的有冲突，默认 给0
                this.remoteSetEnmyTank(gamedTempData.getEnemyTankDegree(),gamedTempData.getEnemyTankDirection(),gamedTempData.getEnemyTankEnableFire(),gamedTempData.getEnemyTankBulletDistance(),gamedTempData.getEnemyTankBulletType());

            }

        }
    }

    public void remoteSetEnmyTank(int tankDegree,int tankDirection,boolean tankFireFlag,double distance,int enemyBulletType){
        this.gameDto.getEnemyTank().setWeaponDegree(-tankDegree);
        this.gameDto.getEnemyTank().setTankDirection(-tankDirection);
        //Log.i(TAG,"gameData.getEnemyTankDirection():"+gameData.getEnemyTankDirection());
        //设置enemy坦克的开火属性
        if(tankFireFlag){
            this.enemyTankOnFire(tankDegree,distance,enemyBulletType);
        }
    }
    public void remoteSetMyTank(int tankDegree,int tankDirection,boolean tankFireFlag,int myTankBulletType){
        //Log.i(TAG,"is mytank is null ? "+this.gameDto.getMyTank());
        //Log.i(TAG,"is Enertank is null ? "+this.gameDto.getEnemyTank());
        this.gameDto.getMyTank().setWeaponDegree(tankDegree);
        this.gameDto.getMyTank().setTankDirection(tankDirection);
        //Log.i(TAG,"gameData.getEnemyTankDirection():"+gameData.getEnemyTankDirection());
        //设置enemy坦克的开火属性
        if(tankFireFlag){
            this.myTankOnFire(myTankBulletType);
        }
    }

    /**
     * 与command处理相关的方法类
     */
    @Override
    public void commandRecived(ComDataF comDataF) {
        Log.w(TAG, "reviced cmmand:" + comDataF.getComDataS().getCommad());
        String command = comDataF.getComDataS().getCommad();
/*********************************与游戏初始化相关的命令***************************************************/
        /**activity端接受到passive发送命令,记录远程的Id号码，并发送确认信息**/
        if (command.equals(StaticVariable.INIT_PASSIVE_REQUEST_CONNECT)) {
            if (StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY) {
                Log.w(TAG, "INIT_PASSIVE_REQUEST_CONNECT cmmand:" + comDataF.getComDataS().getCommad());
                StaticVariable.REMOTE_DEVICE_ID = comDataF.getComDataS().getObject();
                //INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT
                Tool.sendACKToPassive(this.clientCommunicate);
                //发送完成后，activity端启动一个server的线程，用于处理和server相关的动作
            } else {
                Log.i(TAG, "*******************身份错误_1************************");
            }
            /** passive接受确认信息，并传输自身的信息数据**/
        } else if (command.equals(StaticVariable.INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT)) {
            this.remoteDeviceACKflag = true;
            if (StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.PASSIVE) {
                Log.w(TAG, "INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT cmmand:" + comDataF.getComDataS().getCommad());
                Tool.sendSelfInfoToActive(this.clientCommunicate,this.gameDto.getMyTank().getTankType());
            } else {
                Log.i(TAG, "*******************身份错误_2************************");
            }
            /** activity接受信息数据，并传输自身的信息数据**/
        } else if (command.equals(StaticVariable.INIT_PASSIVE_RESPONSE_SELFINFO)) {
            if (StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY) {
                Log.w(TAG, "INIT_PASSIVE_RESPONSE_SELFINFO cmmand:" + comDataF.getComDataS().getCommad());
                //activity接受远程的数据信息
                DeviceInfo remoteDeviceInfo = gson.fromJson(comDataF.getComDataS().getObject(), DeviceInfo.class);
                //Log.i(TAG,"activity recived passive info REMOTE_DENSITY: "+remoteDeviceInfo.screanDesntiy +",REMOTE_SCREEN_HEIGHT:"+remoteDeviceInfo.screanHeight+",REMOTE_SCREEN_WIDTH:"+remoteDeviceInfo.screanWidth);
                //Log.i(TAG,"activity LOCAL_SCREEN_WIDTH:"+LOCAL_SCREEN_WIDTH);
                StaticVariable.REMOTE_DENSITY = remoteDeviceInfo.screanDesntiy;
                StaticVariable.REMOTE_SCREEN_HEIGHT = remoteDeviceInfo.screanHeight;
                StaticVariable.REMOTE_SCREEN_WIDTH = remoteDeviceInfo.screanWidth;
                StaticVariable.SCALE_SCREEN_HEIGHT = (float) StaticVariable.LOCAL_SCREEN_HEIGHT / (float) StaticVariable.REMOTE_SCREEN_HEIGHT;
                StaticVariable.SCALE_SCREEN_WIDTH = (float) LOCAL_SCREEN_WIDTH / (float) StaticVariable.REMOTE_SCREEN_WIDTH;
                this.remoteTankType = remoteDeviceInfo.tankType;
                Log.i(TAG, "activity SCALE_SCREEN_WIDTH:"+ SCALE_SCREEN_WIDTH+" and remoteTankType is: "+this.remoteTankType );
                Tool.sendSelfInfoToPassive(this.clientCommunicate,this.gameDto.getMyTank().getTankType());
            } else {
                Log.i(TAG, "*******************身份错误_3************************");
            }
            /**  passive接受信息数据，并传输初始化完成命令，等待初始化完成命令，**/
        } else if (command.equals(StaticVariable.INIT_ACTIVITE_RESPONSE_SELFINFO)) {
            if (StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.PASSIVE) {
                Log.w(TAG, "INIT_ACTIVITE_RESPONSE_SELFINFO cmmand:" + comDataF.getComDataS().getCommad());
                DeviceInfo remoteDeviceInfo = gson.fromJson(comDataF.getComDataS().getObject(), DeviceInfo.class);
                //Log.i(TAG,"passive recived passive info REMOTE_DENSITY: "+remoteDeviceInfo.screanDesntiy +",REMOTE_SCREEN_HEIGHT:"+remoteDeviceInfo.screanHeight+",REMOTE_SCREEN_WIDTH:"+remoteDeviceInfo.screanWidth);
                //Log.i(TAG,"passive LOCAL_SCREEN_WIDTH:"+LOCAL_SCREEN_WIDTH);
                StaticVariable.REMOTE_DENSITY = remoteDeviceInfo.screanDesntiy;
                StaticVariable.REMOTE_SCREEN_HEIGHT = remoteDeviceInfo.screanHeight;
                StaticVariable.REMOTE_SCREEN_WIDTH = remoteDeviceInfo.screanWidth;
                StaticVariable.SCALE_SCREEN_HEIGHT = (float) StaticVariable.LOCAL_SCREEN_HEIGHT / (float) StaticVariable.REMOTE_SCREEN_HEIGHT;
                StaticVariable.SCALE_SCREEN_WIDTH = (float) LOCAL_SCREEN_WIDTH / (float) StaticVariable.REMOTE_SCREEN_WIDTH;
                this.remoteTankType = remoteDeviceInfo.tankType;
                Log.i(TAG, "activity SCALE_SCREEN_WIDTH:"+ SCALE_SCREEN_WIDTH+" and remoteTankType is: "+this.remoteTankType );
                Tool.sendInitFinishedToActive(this.clientCommunicate);
            } else {
                Log.i(TAG, "*******************身份错误_3************************");
            }
/*********************************与游戏控制相关的命令，考虑使用广播来进行控制***************************************************/
            /**   activity初始化完成命令，开始进入游戏，并传输初始化完成命令，**/
        } else if (command.equals(StaticVariable.INIT_PASSIVE_RESPONSE_INIT_FINISHED)) {
            if (StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY) {
                Log.w(TAG, "INIT_PASSIVE_RESPONSE_INIT_FINISHED cmmand:" + comDataF.getComDataS().getCommad());
                Tool.sendInitFinishedToPassive(this.clientCommunicate);
                //TODO 启动游戏_activity
                /*****在这里可以启动游戏了._ACTIVITY模式....******/
                REMOTE_PREPARED_INIT_FLAG = true;
                this.startGame();
            } else {

            }
        } else if (command.equals(StaticVariable.INIT_ACTIVITE_RESPONSE_INIT_FINISHED)) {
            if (StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.PASSIVE) {
                Log.w(TAG, "INIT_ACTIVITE_RESPONSE_INIT_FINISHED cmmand:" + comDataF.getComDataS().getCommad());
                //TODO 启动游戏_passive
                /*****在这里可以启动游戏了.....PASSIVE模式******/
                REMOTE_PREPARED_INIT_FLAG = true;
                this.startGame();
            } else {

            }
        } else if (command.equals(StaticVariable.INIT_PASSIVE_RESPONSE_GAMEOVER)) {
            if (StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY) {
                Log.w(TAG, "INIT_PASSIVE_RESPONSE_GAMEOVER cmmand:" + comDataF.getComDataS().getCommad());
                //TODO 结束游戏_ACTIVITY
                /*****在这里判断游戏是否结束....ACTIVITY模式******/

            } else {

            }
        } else if (command.equals(StaticVariable.INIT_ACTIVITE_RESPONSE_GAMEOVER)) {
            if (StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY) {
                Log.w(TAG, "INIT_ACTIVITE_RESPONSE_GAMEOVER cmmand:" + comDataF.getComDataS().getCommad());
                //TODO 结束游戏_passive
                /*****在这里判断游戏是否结束....passive模式******/

            } else {

            }
        } else if (command.equals(StaticVariable.RESPONSE_FINISHED_CONNECT_DIRECTIRY)) {
            Log.w(TAG, "RESPONSE_FINISHED_CONNECT_DIRECTIRY cmmand:" + comDataF.getComDataS().getCommad());
            //TODO 中断游戏_direct6
            /*****在这里判断游戏是否主动中断....direction******/
        } else if (command.equals(StaticVariable.RESPONSE_FINISHED_CONNECT_UNDIRECTRIY)) {
            Log.w(TAG, "RESPONSE_FINISHED_CONNECT_UNDIRECTRIY cmmand:" + comDataF.getComDataS().getCommad());
            //TODO 中断游戏_undirect
            /*****在这里判断游戏是否主动中断....direction******/


            //注意这里是架构有问题，bonus和子弹和爆炸的触发，只能通过这种方式来通知，下面的消息，只有passive能收到
            // 产生新的子弹
        } else if (command.equals(StaticVariable.ACTIVITY_MAKE_EXPLODE)) {
            Log.i(TAG, "ACTIVITY_MAKE_EXPLODE passive端新建一个explode......");
            Explode explode = gson.fromJson(comDataF.getComDataS().getObject(), Explode.class);
            //TODO 这里的坐标注意变化
            addExplode((int) (explode.getDrawCenter_x() * SCALE_SCREEN_WIDTH),
                    (int) (explode.getDrawCenter_y() * SCALE_SCREEN_HEIGHT),
                    explode.getExplodeType());
            //bonus的触发，通过这种方式，产生新的bonux
        } else if (command.equals(StaticVariable.ACTIVITY_MAKE_BONUS)) {
            Log.i(TAG, "passive端新建一个bonus......");
            Bonus bonusTemp = gson.fromJson(comDataF.getComDataS().getObject(), Bonus.class);
            int bonusType = bonusTemp.getBonusType();
            Bitmap bonusPicture = BitmapFactory.decodeResource(context.getResources(), StaticVariable.BONUSPICTURE[bonusType]);//0~length-1之间的数
            Bonus bonus = new Bonus(bonusPicture, bonusType);
            //设置bonus
            gameDto.setBonus(bonus);
        } else if (command.equals(StaticVariable.MAKE_BULLET)) {
            Log.i(TAG, "passive端新建一个bullet......");
            //TODO 这里可能有类型转换的错误
            MyBullet bullet = gson.fromJson(comDataF.getComDataS().getObject(), MyBullet.class);
            Bitmap bullet_temp = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.BUTTLE_BASCINFOS[bullet.getBulletType()].getPicture());
            Bitmap bulletPicture = Tool.reBuildImg(bullet_temp, 0, 1, 1, false, true);

            double bulletV_x=StaticVariable.BUTTLE_BASCINFOS[this.gameDto.getEnemyTank().getSelectedBullets()].getSpeed()*bullet.getBulletDegree()*Math.cos(Math.toRadians(bullet.getBulletDegree()));
            double bulletV_y=-StaticVariable.BUTTLE_BASCINFOS[this.gameDto.getEnemyTank().getSelectedBullets()].getSpeed()*bullet.getBulletDegree()*Math.sin(Math.toRadians(bullet.getBulletDegree()));
            EnemyBullet enemyBullet = new EnemyBullet(bulletPicture,bullet.getBulletType(),bulletV_x,bulletV_y,
                    this.gameDto.getEnemyTank().getWeaponPoxition_x(),this.gameDto.getEnemyTank().getWeaponPoxition_y());
            enemyBullet.setBulletDegree(bullet.getBulletDegree());
            //允许发射....
            enemyBullet.setDrawFlag(true);
            //增加敌方子弹
            this.gameDto.getEnemyTank().addBuleetFire(enemyBullet);
        }

    }

    /**
     * 主要处理信息数据的显示即可
     */
    @Override
    public void msgRecived(String msg) {
        //Log.w(TAG, "reviced msg:" + msg);
        //这里可能需要设置，不能再主线程中更新UI
        String orginText = this.gameDto.getMsgText().getText().toString();
        orginText = orginText + "\n" + REMOTE_DEVICE_ID + ": " + msg;
        Message msgInfo = gameActivityHandler.obtainMessage();
        msgInfo.what = StaticVariable.UPDATE_MSG_INFO;
        Bundle bundleMsg = new Bundle();
        bundleMsg.putString("MSG", orginText);  //设置子弹数量
        msgInfo.setData(bundleMsg);//mes利用Bundle传递数据
        gameActivityHandler.sendMessage(msgInfo);
    }

    public boolean isLocalTankOnfire() {
        return isLocalTankOnfire;
    }

    public void setLocalTankOnfire(boolean localTankOnfire) {
        isLocalTankOnfire = localTankOnfire;
    }

    public LocalGameProcess getLocalGameProcess() {
        return localGameProcess;
    }

}

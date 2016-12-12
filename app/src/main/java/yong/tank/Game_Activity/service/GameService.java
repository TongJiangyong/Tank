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

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import yong.tank.Communicate.ComData.ComDataF;
import yong.tank.Communicate.ComData.ComDataPackage;
import yong.tank.Communicate.InterfaceGroup.ClientCommunicate;
import yong.tank.Communicate.InterfaceGroup.ObserverCommand;
import yong.tank.Communicate.InterfaceGroup.ObserverInfo;
import yong.tank.Communicate.InterfaceGroup.ObserverMsg;
import yong.tank.Dto.GameDto;
import yong.tank.Dto.testDto;
import yong.tank.R;
import yong.tank.modal.Blood;
import yong.tank.modal.Bonus;
import yong.tank.modal.Explode;
import yong.tank.modal.MyTank;
import yong.tank.modal.PlayerPain;
import yong.tank.modal.Point;
import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

/**
 * Created by hasee on 2016/11/10.
 */

public class GameService implements ObserverInfo,ObserverMsg,ObserverCommand{
    private GameDto gameDto;
    private boolean gameStateFlag=false;
    private static String TAG ="GameService";
    private GameThread gameThread;
    private Context context;
    private Timer timerBonus;
    private Timer timerCommunnicate;
    private boolean connectFlag =false;
    //TODO 测试通讯的代码
    private ClientCommunicate clientCommunicate;
    private Gson gson = new Gson();


    public GameService(GameDto gameDto,Context context) {
        this.gameDto = gameDto;
        this.context = context;
        //TODO 完成游戏启动前初始化的相关配置工作....
        this.initLocal();
        //TODO 完成远程连接工作后启动 这里是做测试用的
        this.initRemote();
    }



    //设置handle的处理
    //注意这里为主线程....handler默认使用主线程的looper
    private  Handler myHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what){
                case StaticVariable.MSG_TOAST:
                    Toast.makeText(context.getApplicationContext(),  msg.getData().getString("message"), Toast.LENGTH_SHORT).show();// 显示时间较
                    break;
                    //网络连接失败
                case StaticVariable.MSG_CONNECT_ERROR:
                    connectError();
                    break;
                    //网络连接成功
                case StaticVariable.MSG_CONNECT_SUCCESS:
                    connectInit();
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

    private void connectError() {
        this.connectFlag=false;
        Toast.makeText(context.getApplicationContext(),  "与对手连接失败", Toast.LENGTH_SHORT).show();
        //TODO 延迟执行退出程序
    }

    private void connectInit() {
        //连接成功后，添加监听
        this.connectFlag=true;
        //TODO 网络连接 成功后，进入init相关的系列....操作，包括数据传输等......

        clientCommunicate.addInfoObserver(this);
        clientCommunicate.addMsgObserver(this);
        clientCommunicate.addCommandObserver(this);
        Toast.makeText(context.getApplicationContext(),  "与对手连接成功", Toast.LENGTH_SHORT).show();
    }
    public void gameStart(){
        if(gameThread==null){
            gameThread= new GameThread();
            Thread thread = new Thread(gameThread);
            thread.start();
            //启动bonus的线程
            this.startMakeBonus();
            //启动对应模式的communicate线程
            clientCommunicate.setMyHandle(myHandler);
            clientCommunicate.startCommunicate();
            //在这里启动数据交互线程，暂时学习一下
            this.startCommunicateThread();
        }else{
            Log.w(TAG,"gameThread is not null");
        }
    }

    private void startCommunicateThread() {
        timerCommunnicate = new Timer();
        CommunicateThread communicateThread = new CommunicateThread();
        //schedule(TimerTask task, long delay, long period)
        //等待试试10s后开始调度，每隔10s产生一个
        Log.w(TAG,"start to communicate");
        //就用100ms进行测试
        timerBonus.schedule(communicateThread,5000,100);
    }

    public void startMakeBonus(){
        timerBonus = new Timer();
        BonusMaker bonusMaker = new BonusMaker();
        //schedule(TimerTask task, long delay, long period)
        //等待试试10s后开始调度，每隔10s产生一个
        Log.w(TAG,"bonus start to maker");
        timerBonus.schedule(bonusMaker,5000,10000);
    }
    public void stopMakeBonus(){
        timerBonus.cancel();
    }

    public void gameStop(){
        if(gameThread!=null){
            gameThread.gameThreadStop();
            //TODO 这里设置一个游戏结束标识符，进行判断,然后置为null


            //关闭网络
            clientCommunicate.stopCommunicate();
        }else{
            Log.w(TAG,"gameThread is  null");
        }
    }




    class GameThread implements Runnable {

        private boolean threadFlag=true;

        @Override
        public void run() {
            while(threadFlag){
                gameDto.getMyTank().setEnableFire(true);
                int num=gameDto.getMyTank().getBulletsFire().size();
                if(num!=0){
                    for(int i=(num-1);i>=0;i--){
                        //TODO 这里注意，统一在爆炸中删除所有子弹，并只能放在最后，不然会有问题
                        //这里设置continue语句来解决这个问题.....
                        //测试击中bonus后用的方法
                        if (testFireBonus(i)){
                            continue;
                        }
                        //测试爆炸用的方法
                        if(testExplode(i)){
                            continue;
                        }

                    }
                }
                try {
                    //逻辑用的时间短一点....
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        public void gameThreadStop() {
            this.threadFlag = true;
        }

    }
    //测试击中bonus的反应
    //1、停止bonus 2、设置selectView 3、削减血条 4/产生爆炸并移除子弹，5、根据当前的tank子弹状态，更新子弹状态
    private boolean testFireBonus(int bullet){
        if(gameDto.getBonus()!=null){
            if(gameDto.getBonus().isInBonusScope(gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                 gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_y())){
                //***************设置selectView***************
                //设置selectView的图像和文字，并设置tank的子弹属性
                // 注意这里因为是bonus比bullet的种类少一个,所以为：bulletType+1
                int bulletType = this.gameDto.getBonus().getBonusType()+1;
                //TODO 将message设为方法
                //这里涉及上主线程UI更新的问题，比较麻烦，在android中只能采用message通知的方法
                // selectView并不是采用实时刷新的方法做的，所以比较麻烦
                Message msg = new Message();
                msg.what = StaticVariable.MSG_UPDATE;
                Bundle bundle = new Bundle();
                bundle.putInt("bullletNum",StaticVariable.BUTTON_NUM_ORIGN);  //设置子弹数量
                bundle.putInt("bullletType",bulletType);  //设置子弹的种类
                bundle.putInt("bullletPicture",StaticVariable.BUTTLE_BASCINFOS[(bulletType)].getPicture());  //设置子弹的图片
                msg.setData(bundle);//mes利用Bundle传递数据
                gameDto.getSelectButtons().get(R.id.selectButton_2).getMyHandler().sendMessage(msg);
                //设置selected为填充状态
                gameDto.getSelectButtons().get(R.id.selectButton_2).setFilled(true);
                //***************根据当前的tank子弹状态，更新子弹状态***************
                //如果当前更新的子弹是bonus的子弹，则更新为最新的bonus子弹
                if(gameDto.getMyTank().getSelectedBullets()!=StaticVariable.ORIGIN){
                    this.gameDto.getMyTank().setSelectedBullets(bulletType);
                    this.gameDto.getMyTank().setSelectedBulletsNum(StaticVariable.BUTTON_NUM_ORIGN);
                }
                //***************削减血条***************
                double boldSubtraction =gameDto.getMyTank().getBulletsFire().get(bullet).getBulletBascInfo().getPower();
                //TODO 这里好好想一下，如何实现注册，然后回调函数.....
                gameDto.getBlood().subtractionBlood(boldSubtraction);
                if(gameDto.getBlood().getBloodNum()<0){
                    //不知道为啥要加这一条Looper.prepare();Looper.loop();可能是TOAST在其他thread中运行的机制？
                    Message msgInfo = myHandler.obtainMessage();
                    msgInfo.what = StaticVariable.MSG_TOAST;
                    Bundle bundleMsg = new Bundle();
                    bundleMsg.putString("message","血条已空");  //设置子弹数量
                    msgInfo.setData(bundleMsg);//mes利用Bundle传递数据
                    myHandler.sendMessage(msgInfo);
                    gameDto.getBlood().setBloodNum(1);
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


    //测试爆炸用的方法
    private boolean testExplode(int bullet) {
        //TODO 测试explode
        //如果打中地面
        if(gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_y()> StaticVariable.LOCAL_SCREEN_HEIGHT /7*5){
            addExplode(gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                    gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_y(),
                    StaticVariable.EXPLODE_TYPE_GROUND);
            gameDto.getMyTank().getBulletsFire().remove(bullet);//移除子弹
            return true;
            //如果打中坦克
        }
        return false;
    }


    //产生爆炸的方法
    public void addExplode(int dx,int dy,int type){
        Explode explode = null;
        if(type==StaticVariable.EXPLODE_TYPE_GROUND){
           explode = new Explode(StaticVariable.EXPLODESONGROND,
                    dx,
                    dy,
                    type);
        }else{
            explode = new Explode(StaticVariable.EXPLODESONTANK,
                    dx,
                    dy,
                    type);
        }
        gameDto.getExplodes().add(explode);//发生爆炸
    }


    //产生bonus的线程
    //这里注意，一定要确定，bonus会被直接用完
    public class BonusMaker extends TimerTask {
        @Override
        public void run() {
            //获取bonus的路径
            Log.w(TAG,"********************************产生一个bonus****************************");
            //随机产生一个bonus，注意这里的bonus和子弹是绑定的
            int bonusType = new Random().nextInt(StaticVariable.BONUSPICTURE.length);
            Bitmap bonusPicture = BitmapFactory.decodeResource(context.getResources(),StaticVariable.BONUSPICTURE[bonusType]);//0~length-1之间的数
            List<Point> bonusPath =Tool.getBonusPath(bonusPicture);
            Bonus bonus = new Bonus(bonusPicture,bonusPath,bonusType);
            //设置bonus
            gameDto.setBonus(bonus);

        }
    }

    //互相communicate的线程
    public class CommunicateThread extends TimerTask {
        @Override
        public void run() {
            if(connectFlag){

                if(connectFlag){
                    Log.w(TAG,"send info");
                    testDto testDto = new testDto(12,"test");
                    ComDataF comDataF = ComDataPackage.packageToF("654321#","12",gson.toJson(testDto));
                    clientCommunicate.sendInfo(gson.toJson(comDataF));
             }

            }


        }
    }



    public ClientCommunicate getClientCommunicate() {
        return clientCommunicate;
    }

    public void setClientCommunicate(ClientCommunicate clientCommunicate) {
        this.clientCommunicate = clientCommunicate;
    }

    /*****************************************这里是与通信相关的方法*****时间差大概在10~30ms之间**********************************/
    @Override
    public void commandRecived(String command) {
        Log.w(TAG,"reviced cmmand:"+command);
    }

    @Override
    public void infoRecived(testDto testDto) {
        Log.w(TAG,"reviced testDto:"+testDto.toString());
    }

    @Override
    public void msgRecived(String msg) {
        Log.w(TAG,"reviced msg:"+msg);
    }


    private void communicateError() {
        Toast.makeText(context.getApplicationContext(),  "网络通信故障", Toast.LENGTH_SHORT).show();
    }
    private void communicateOut() {
        Toast.makeText(context.getApplicationContext(),  "对方断开连接", Toast.LENGTH_SHORT).show();
    }


    /********************************下面是与初始化相关的代码***********************************************/
    /***初始化主要包括两个过程
     * 1、本地数据的初始化
     * 2、远程连接成功后，数据的初始化，
     * 3两者初始化成功后，才能进行数据的通行
     * **/

    /**
     * 本地初始化相关的代码
     */
    private void initLocal() {
        //TODO 初始化explode
        for(int i=0;i<StaticVariable.EXPLODESPICTURE_GROUND.length;i++){
            StaticVariable.EXPLODESONGROND[i]=BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.EXPLODESPICTURE_GROUND[i]);
        }
        for(int i=0;i<StaticVariable.EXPLODESPICTURE_TANKE.length;i++){
            StaticVariable.EXPLODESONTANK[i]=BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.EXPLODESPICTURE_TANKE[i]);
        }
        //TODO 测试初始化tank  测试添加的东西在gameservice中试试
        MyTank myTank =initTank(this.gameDto.getTankType());
        gameDto.setMyTank(myTank);
        //TODO 测试初始化玩家控制图标
        PlayerPain playerPain = new PlayerPain();
        gameDto.setMyTank(myTank);
        gameDto.setPlayerPain(playerPain);
        //TODO 测试初始化装载血条的视图
        Blood blood = initBlood(true);
        gameDto.setBlood(blood);
    }

    private MyTank initTank(int tankType){
        Bitmap tankPicture_temp = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.TANKBASCINFO[tankType].getPicture());
        Bitmap tankPicture = Tool.reBuildImg(tankPicture_temp,0,1,1,false,true);
        Bitmap armPicture = BitmapFactory.decodeResource(this.context.getResources(), R.mipmap.gun);
        MyTank tank = new MyTank(tankPicture,armPicture,tankType, StaticVariable.TANKBASCINFO[tankType]);
        return tank;
    }
    private Blood initBlood(Boolean isMyBlood){
        //TODO 如果是true会找其他图片
        //TODO 这里暂时先别做.....以后再改....
        Bitmap blood_picture=null;
        blood_picture = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.BLOOD);
        Bitmap power_picture=null;
        power_picture = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.POWERR);
        Bitmap bloodBlock_picture=null;
        bloodBlock_picture = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.BLOODBLOCK);
        Blood blood = new Blood(blood_picture, power_picture, bloodBlock_picture,1,1);
        return blood;
    }


    /**
     *远程初始化相关的代码
     */
    private void initRemote() {
    }


}

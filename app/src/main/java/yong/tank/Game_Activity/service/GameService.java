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
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import yong.tank.Communicate.ComData.ComDataF;
import yong.tank.Communicate.InterfaceGroup.ClientCommunicate;
import yong.tank.Communicate.InterfaceGroup.ObserverCommand;
import yong.tank.Communicate.InterfaceGroup.ObserverInfo;
import yong.tank.Communicate.InterfaceGroup.ObserverMsg;
import yong.tank.Dto.GameDto;
import yong.tank.LocalRecord.LocalRecord;
import yong.tank.R;
import yong.tank.modal.Bonus;
import yong.tank.modal.DeviceInfo;
import yong.tank.modal.EnemyBlood;
import yong.tank.modal.EnemyTank;
import yong.tank.modal.Explode;
import yong.tank.modal.MyBlood;
import yong.tank.modal.MyTank;
import yong.tank.modal.PlayerPain;
import yong.tank.modal.Point;
import yong.tank.modal.User;
import yong.tank.modal.abstractGoup.Blood;
import yong.tank.modal.abstractGoup.Tank;
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
    private LocalRecord<User> localUser = new LocalRecord<User>();
    private Queue<GameDto> remoteGameDtos = new LinkedList<GameDto>();
    private boolean remoteDtoInitFlag =false;
    //TODO 测试通讯的代码
    private ClientCommunicate clientCommunicate;
    private Gson gson = new Gson();

    //这是一个专门用于启动程序的handler......，因为架构设计出错，导致service和actiactivity层之间要建立联系，非常不对.....
    private Handler gameActivityHandler;


    public GameService(GameDto gameDto,Context context,Handler gameActivityHandler) {
        this.gameDto = gameDto;
        this.context = context;
        this.gameActivityHandler=gameActivityHandler;

    }



    //设置handle的处理
    //注意这里为主线程....handler默认使用主线程的looper
    private  Handler myHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what){
                case StaticVariable.MSG_TOAST:
                    Toast.makeText(context.getApplicationContext(),  msg.getData().getString("message"), Toast.LENGTH_SHORT).show();// 显示时间较
                    break;
            }

        }

    };


    public void gameStart(){
        if(gameThread==null){
            gameThread= new GameThread();
            Thread thread = new Thread(gameThread);
            thread.start();
            //如果是主动模式则启动bonus的线程
            if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY){
                this.startMakeBonus();
            }
        }else{
            Log.w(TAG,"gameThread is not null");
        }
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
                        if (myFireBonus(i)){
                            continue;
                        }
                        //测试爆炸用的方法
                        if(myTankExplode(i)){
                            continue;
                        }

                    }
                }
                //下面是本地模式的相关代码
                if(StaticVariable.CHOSED_MODE==StaticVariable.GAME_MODE.LOCAL){
                    if(remoteDtoInitFlag){
                        //TODO 检查一下，这个enermyNum为啥为0
                        int enermyNum=gameDto.getEnemyTank().getBulletsFire().size();
                        //Log.i(TAG,"enermyNum is :"+enermyNum);
                        //TODO 这里加上并测试本地模式的程序流程
                        if(enermyNum!=0){
                            for(int i=(enermyNum-1);i>=0;i--){
                                //TODO 这里注意，统一在爆炸中删除所有子弹，并只能放在最后，不然会有问题
                                if(enermyFireBonus(i)){
                                    continue;
                                }
                                if(enermyTankExplode(i)){
                                    continue;
                                }
                            }
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
    //测试我的坦克击中bonus的反应
    //1、停止bonus 2、设置selectView 3、削减血条 4/产生爆炸并移除子弹，5、根据当前的tank子弹状态，更新子弹状态
    private boolean myFireBonus(int bullet){
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
    private boolean enermyFireBonus(int bullet){
        if(gameDto.getBonus()!=null){
            if(gameDto.getBonus().isInBonusScope(gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                    gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_y())){
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
        if(!gameDto.getMyTank().getBulletsFire().get(bullet).isDrawFlag()){
            gameDto.getMyTank().getBulletsFire().remove(bullet);//移除子弹
            return true;
        }
        //如果打中地方坦克   1、产生爆炸 2、移除子弹 3、检查游戏是否结束
        if(gameDto.getEnemyTank().isInCircle(gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_y())){
            /*设置敌方坦克的血条*/
            //***************削减血条***************
            double boldSubtraction =gameDto.getMyTank().getBulletsFire().get(bullet).getBulletBascInfo().getPower();
            //TODO 这里好好想一下，如何实现注册，然后回调函数.....
            gameDto.getEnemyBlood().subtractionBlood(boldSubtraction);
            //***************产生爆炸，移除子弹***************
            addExplode(gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                    gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_y(),
                    StaticVariable.EXPLODE_TYPE_TANK);
            gameDto.getMyTank().getBulletsFire().remove(bullet);//移除子弹
            //***************提示地方坦克的血量,并检查游戏是否结束***************
            if(gameDto.getEnemyBlood().getBloodNum()<0){
                //不知道为啥要加这一条Looper.prepare();Looper.loop();可能是TOAST在其他thread中运行的机制？
                Message msgInfo = myHandler.obtainMessage();
                msgInfo.what = StaticVariable.MSG_TOAST;
                Bundle bundleMsg = new Bundle();
                bundleMsg.putString("message","敌方坦克血条已空，我方胜利");  //设置子弹数量
                msgInfo.setData(bundleMsg);//mes利用Bundle传递数据
                myHandler.sendMessage(msgInfo);
                //测试，用于重启游戏
                gameDto.getEnemyBlood().setBloodNum(1);
            }
            return true;
        }

        //如果打中地面 1、产生爆炸 2、移除子弹
        if(gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_y()> StaticVariable.LOCAL_SCREEN_HEIGHT /7*5){
            addExplode(gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                    gameDto.getMyTank().getBulletsFire().get(bullet).getBulletPosition_y(),
                    StaticVariable.EXPLODE_TYPE_GROUND);
            gameDto.getMyTank().getBulletsFire().remove(bullet);//移除子弹
            return true;
        }
        return false;
    }


    //敌方坦克的子弹击中效果
    // 包括1、判断是否应该移除子弹 2、增加爆炸的效果.....
    private boolean enermyTankExplode(int bullet) {
        //TODO 测试explode
        //如果发现子弹已经停止绘制，则移除
        if(!gameDto.getEnemyTank().getBulletsFire().get(bullet).isDrawFlag()){
            gameDto.getEnemyTank().getBulletsFire().remove(bullet);//移除子弹
            return true;
        }
        //如果打中我方坦克
        if(gameDto.getMyTank().isInCircle(gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_y())){
            /*设置敌方坦克的血条*/
            //***************削减血条***************
            double boldSubtraction =gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletBascInfo().getPower();
            //TODO 这里好好想一下，如何实现注册，然后回调函数.....
            gameDto.getMyBlood().subtractionBlood(boldSubtraction);
            //***************产生爆炸，移除子弹***************
            addExplode(gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                    gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_y(),
                    StaticVariable.EXPLODE_TYPE_TANK);
            gameDto.getEnemyTank().getBulletsFire().remove(bullet);//移除子弹
            //***************提示地方坦克的血量,并检查游戏是否结束***************
            if(gameDto.getMyBlood().getBloodNum()<0){
                //不知道为啥要加这一条Looper.prepare();Looper.loop();可能是TOAST在其他thread中运行的机制？
                Message msgInfo = myHandler.obtainMessage();
                msgInfo.what = StaticVariable.MSG_TOAST;
                Bundle bundleMsg = new Bundle();
                bundleMsg.putString("message","我方坦克血条已空，敌方胜利");  //设置子弹数量
                msgInfo.setData(bundleMsg);//mes利用Bundle传递数据
                myHandler.sendMessage(msgInfo);
                //测试，用于重启游戏
                gameDto.getMyBlood().setBloodNum(1);
            }
            return true;
        }
        //如果打中地面
        if(gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_y()> StaticVariable.LOCAL_SCREEN_HEIGHT /7*5){
            addExplode(gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_x(),
                    gameDto.getEnemyTank().getBulletsFire().get(bullet).getBulletPosition_y(),
                    StaticVariable.EXPLODE_TYPE_GROUND);
            gameDto.getEnemyTank().getBulletsFire().remove(bullet);//移除子弹
            return true;
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
        if(!(StaticVariable.CHOSED_MODE==StaticVariable.GAME_MODE.LOCAL)){
            //启动一个消耗的线程
            Timer timer = new Timer();
            timer.schedule(new consumeThread(), 3000, 20);
        }
    }

    //互相communicate的线程
    private SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public class consumeThread extends TimerTask {
        @Override
        public void run() {
            if(remoteGameDtos.size()!=0){

                remoteGameDtos.poll();
                Log.w(TAG,"队列的剩余大小为:"+remoteGameDtos.size());

            }else{
                //Log.w(TAG,"队列为空");
            }

        }
    }








    /********************************下面是与初始化相关的代码***********************************************/
    /***初始化主要包括两个过程
     * 1、本地数据的初始化
     * 2、远程连接成功后，数据的初始化，
     * 3两者初始化成功后，才能进行数据的通行
     * **/


    public void initAllDataInfo() {
        //TODO 完成游戏启动前自身数据的初始化
        this.initLocalDto();
        //TODO 确定通信连接后，完成远程连接工作后其他数据的初始化
        Log.i(TAG,"game_mode is :"+StaticVariable.CHOSED_MODE );
        if(StaticVariable.CHOSED_MODE == StaticVariable.GAME_MODE.LOCAL){
            Log.i(TAG,"START GAME local。。。。。。");
            this.initLocalActivity();
        }else{
            Log.i(TAG,"START GAME remote。。。。。。");
            this.initRemoteActivity();
        }

    }


    /**
     * 本地初始化本地dto相关的代码
     */
    private void initLocalDto() {
        //TODO 初始化explode
        for(int i=0;i<StaticVariable.EXPLODESPICTURE_GROUND.length;i++){
            StaticVariable.EXPLODESONGROND[i]=BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.EXPLODESPICTURE_GROUND[i]);
        }
        for(int i=0;i<StaticVariable.EXPLODESPICTURE_TANKE.length;i++){
            StaticVariable.EXPLODESONTANK[i]=BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.EXPLODESPICTURE_TANKE[i]);
        }
        //TODO 测试初始化tank  测试添加的东西在gameservice中试试
        MyTank myTank =(MyTank) initTank(this.gameDto.getTankType(),true);
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
    private void initRemoteDto(GameDto gameDtoTemp) {
        EnemyTank enemyTank = (EnemyTank) initTank(gameDtoTemp.getTankType(),false);
        EnemyBlood enemyBlood = (EnemyBlood)initBlood(false);
        gameDto.setEnemyBlood(enemyBlood);
        gameDto.setEnemyTank(enemyTank);
    }

    private Tank initTank(int tankType,Boolean isMyTank){
        Bitmap tankPicture_temp = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.TANKBASCINFO[tankType].getPicture());
        Bitmap tankPicture=null;
        Bitmap armPicture=null;
        Tank tank=null;
        if(isMyTank){
            armPicture = BitmapFactory.decodeResource(this.context.getResources(), R.mipmap.gun);
            tankPicture= Tool.reBuildImg(tankPicture_temp,0,1,1,false,true);
            tank = new MyTank(tankPicture,armPicture,tankType, StaticVariable.TANKBASCINFO[tankType]);
        }else{
            armPicture = BitmapFactory.decodeResource(this.context.getResources(), R.mipmap.gun_symmetry);
            tankPicture= Tool.reBuildImg(tankPicture_temp,0,1,1,false,false);
            tank = new EnemyTank(tankPicture,armPicture,tankType, StaticVariable.TANKBASCINFO[tankType]);
        }

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
        Blood blood =null;
        if(isMyBlood){
            blood = new MyBlood(blood_picture, power_picture, bloodBlock_picture,1,1);
        }else{
            Bitmap bloodBlock_picture_temp= Tool.reBuildImg(bloodBlock_picture,0,1,1,false,true);
            blood = new EnemyBlood(blood_picture, power_picture, bloodBlock_picture_temp,1,1);
        }
        return blood;
    }


    /**
     *远程初始化相关的代码
     */

    //发送ID到server，同时，passive端，首先开始进行主动的通讯工作
    private void initRemoteActivity() {
        Tool.sendSelfIdToServer(this.clientCommunicate);
        if(StaticVariable.CHOSED_RULE==StaticVariable.GAME_RULE.PASSIVE){
            Tool.sendSelfIdToActive(this.clientCommunicate);
        }
    }

    private void initLocalActivity() {
        //local暂时直接开始游戏：
        Message msgInfo = gameActivityHandler.obtainMessage();
        msgInfo.what = StaticVariable.GAME_STARTED;
        gameActivityHandler.sendMessage(msgInfo);
    }

    /*****************************************这里是与通信相关的方法*****时间差大概在10~30ms之间**********************************/

    //主要用来向buffer中填充数据
    @Override
    public void infoRecived(GameDto gameDtoReviced) {
        remoteGameDtos.offer(gameDtoReviced);   //入列
        //下面是与本地模式相关的代码
        if(StaticVariable.CHOSED_MODE==StaticVariable.GAME_MODE.LOCAL){
            GameDto gameDtoTemp = remoteGameDtos.poll();
            //在这里初始化Enermy坦克的信息
            if(gameDtoTemp.getMyTank()!=null&&gameDtoTemp.getMyBlood()!=null&& this.gameDto.getEnemyTank()==null&&this.gameDto.getEnemyBlood()==null){
                //初始化敌方的变量......
                //Log.i(TAG,"初始化敌方的变量......");
                initRemoteDto(gameDtoTemp);
                //允许地方坦克发射
                gameDto.getEnemyTank().setEnableFire(true);
                this.remoteDtoInitFlag = true;
            }

            //如果初始化完成，即开始进行设置工作
            if(this.remoteDtoInitFlag){
                /**设置EnemyTank相关的属性**/
                //TODO 注意这里要加上不同分辨率的处理.......注意设置角度为负
                this.gameDto.getEnemyTank().setWeaponDegree(-gameDtoTemp.getMyTank().getWeaponDegree());
                this.gameDto.getEnemyTank().setTankPosition_x(StaticVariable.LOCAL_SCREEN_WIDTH-gameDtoTemp.getMyTank().getTankPosition_x()-this.gameDto.getMyTank().getTankPicture().getWidth());
                this.gameDto.getEnemyTank().setTankPosition_y(gameDtoTemp.getMyTank().getTankPosition_y());
                /**设置EnemyBlood相关的属性**/
                //this.gameDto.getEnemyBlood().setBloodNum((gameDtoTemp.getMyBlood().getBloodNum()));
            }

        }
    }

    /**
     *与command处理相关的方法类
     */
    @Override
    public void commandRecived(ComDataF comDataF) {
        Log.w(TAG,"reviced cmmand:"+comDataF.getComDataS().getCommad());
        String command = comDataF.getComDataS().getCommad();
/*********************************与游戏初始化相关的命令***************************************************/
        /**activity端接受到passive发送命令,记录远程的Id号码，并发送确认信息**/
        if(command.equals(StaticVariable.INIT_PASSIVE_REQUEST_CONNECT)){
            if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY){
                Log.w(TAG,"INIT_PASSIVE_REQUEST_CONNECT cmmand:"+comDataF.getComDataS().getCommad());
                StaticVariable.REMOTE_DEVICE_ID = comDataF.getComDataS().getObject();
                //INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT
                Tool.sendACKToPassive(this.clientCommunicate);
            }else{
                Log.i(TAG,"*******************身份错误_1************************");
            }
            /** passive接受确认信息，并传输自身的信息数据**/
        }else if(command.equals(StaticVariable.INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT)){
            if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.PASSIVE){
                Log.w(TAG,"INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT cmmand:"+comDataF.getComDataS().getCommad());
                Tool.sendSelfInfoToActive(this.clientCommunicate);
            }else{
                Log.i(TAG,"*******************身份错误_2************************");
            }
            /** activity接受信息数据，并传输自身的信息数据**/
        }else if(command.equals(StaticVariable.INIT_PASSIVE_RESPONSE_SELFINFO)){
            if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY){
                Log.w(TAG,"INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT cmmand:"+comDataF.getComDataS().getCommad());
               //activity接受远程的数据信息
                DeviceInfo remoteDeviceInfo = gson.fromJson(comDataF.getComDataS().getObject(), DeviceInfo.class);
                StaticVariable.REMOTE_DENSITY=remoteDeviceInfo.screanDesntiy;
                StaticVariable.REMOTE_SCREEN_HEIGHT=remoteDeviceInfo.screanHeight;
                StaticVariable.REMOTE_SCREEN_WIDTH=remoteDeviceInfo.screanWidth;
                Tool.sendSelfInfoToPassive(this.clientCommunicate);
            }else{
                Log.i(TAG,"*******************身份错误_3************************");
            }
            /**  passive接受信息数据，并传输初始化完成命令，等待初始化完成命令，**/
        }else if(command.equals(StaticVariable.INIT_ACTIVITE_RESPONSE_SELFINFO)){
            if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.PASSIVE){
                Log.w(TAG,"INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT cmmand:"+comDataF.getComDataS().getCommad());
                DeviceInfo remoteDeviceInfo = gson.fromJson(comDataF.getComDataS().getObject(), DeviceInfo.class);
                StaticVariable.REMOTE_DENSITY=remoteDeviceInfo.screanDesntiy;
                StaticVariable.REMOTE_SCREEN_HEIGHT=remoteDeviceInfo.screanHeight;
                StaticVariable.REMOTE_SCREEN_WIDTH=remoteDeviceInfo.screanWidth;
                Tool.sendInitFinishedToActive(this.clientCommunicate);
            }else{
                Log.i(TAG,"*******************身份错误_3************************");
            }
/*********************************与游戏控制相关的命令，考虑使用广播来进行控制***************************************************/
             /**   activity初始化完成命令，开始进入游戏，并传输初始化完成命令，**/
        }else if(command.equals(StaticVariable.INIT_PASSIVE_RESPONSE_INIT_FINISHED)){
            if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY){
                Log.w(TAG,"INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT cmmand:"+comDataF.getComDataS().getCommad());
                Tool.sendInitFinishedToPassive(this.clientCommunicate);
                //TODO 启动游戏_activity
                /*****在这里可以启动游戏了._ACTIVITY模式....******/

            }else{

            }
        }else if(command.equals(StaticVariable.INIT_ACTIVITE_RESPONSE_INIT_FINISHED)){
            if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.PASSIVE){
                Log.w(TAG,"INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT cmmand:"+comDataF.getComDataS().getCommad());
                //TODO 启动游戏_passive
                /*****在这里可以启动游戏了.....PASSIVE模式******/

            }else{

            }
        }else if(command.equals(StaticVariable.INIT_PASSIVE_RESPONSE_GAMEOVER)){
            if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY){
                Log.w(TAG,"INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT cmmand:"+comDataF.getComDataS().getCommad());
                //TODO 结束游戏_ACTIVITY
                /*****在这里判断游戏是否结束....ACTIVITY模式******/

            }else{

            }
        }else if(command.equals(StaticVariable.INIT_ACTIVITE_RESPONSE_GAMEOVER)){
            if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY){
                Log.w(TAG,"INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT cmmand:"+comDataF.getComDataS().getCommad());
                //TODO 结束游戏_passive
                /*****在这里判断游戏是否结束....passive模式******/

            }else{

            }
        }else if(command.equals(StaticVariable.RESPONSE_FINISHED_CONNECT_DIRECTIRY)) {
                Log.w(TAG, "INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT cmmand:" + comDataF.getComDataS().getCommad());
                 //TODO 中断游戏_direct
                /*****在这里判断游戏是否主动中断....direction******/
        }else if(command.equals(StaticVariable.RESPONSE_FINISHED_CONNECT_UNDIRECTRIY)){
                Log.w(TAG,"INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT cmmand:"+comDataF.getComDataS().getCommad());
                //TODO 中断游戏_undirect
                /*****在这里判断游戏是否主动中断....direction******/
        }else{

        }
    }

    /**
     *主要处理信息数据的显示即可
     */
    @Override
    public void msgRecived(String msg) {
        Log.w(TAG,"reviced msg:"+msg);
    }

}

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
import yong.tank.Communicate.ComData.ComDataPackage;
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
import yong.tank.modal.EnemyBullet;
import yong.tank.modal.EnemyTank;
import yong.tank.modal.Explode;
import yong.tank.modal.MyBlood;
import yong.tank.modal.MyBullet;
import yong.tank.modal.MyTank;
import yong.tank.modal.PlayerPain;
import yong.tank.modal.Point;
import yong.tank.modal.User;
import yong.tank.modal.abstractGoup.Blood;
import yong.tank.modal.abstractGoup.Tank;
import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

import static yong.tank.tool.StaticVariable.LOCAL_SCREEN_WIDTH;
import static yong.tank.tool.StaticVariable.REMOTE_DEVICE_ID;
import static yong.tank.tool.StaticVariable.SCALE_SCREEN_HEIGHT;
import static yong.tank.tool.StaticVariable.SCALE_SCREEN_WIDTH;

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
    //初始化远程DTO信息完成，即已完成全部初始化工作
    private boolean remoteDtoInitFlag =false;
    //初始化远程交换完成，但是远程DTO等变量还未初始化


    private boolean remoteDeviceACKflag = false;

    private SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
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


    public void logicalUpdate(){
        if(gameThread==null) {
            //可以在gameThrea中多写点内容
            gameThread = new GameThread();
            //如果是主动模式则启动bonus的线程 被动端不启动Bonus
            if (StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY) {
                this.startMakeBonus();
            }
            //如果不是本地模式，则启动一个20ms的数据生产者线程
            if(StaticVariable.CHOSED_MODE!=StaticVariable.GAME_MODE.LOCAL){
                Timer timer = new Timer();
                timer.schedule(new productorThread(), 100, 30);
            }
        }
        Log.i(TAG,"************gameThread.runGame()*************");
        //更新gameThread的逻辑
        gameThread.runGame();
    }



    public void startMakeBonus(){
        timerBonus = new Timer();
        BonusMaker bonusMaker = new BonusMaker();
        //schedule(TimerTask task, long delay, long period)
        //等待试试10s后开始调度，每隔10s产生一个
        Log.w(TAG,"*********bonus start to maker**************");
        timerBonus.schedule(bonusMaker,5000,10000);
    }
    public void stopMakeBonus(){
        timerBonus.cancel();
    }

    public void gameStop(){
        if(gameThread!=null){
            //关闭bonus线程
            if(StaticVariable.CHOSED_RULE== StaticVariable.GAME_RULE.ACTIVITY){
                this.stopMakeBonus();
            }

            //TODO 这里设置一个游戏结束标识符，进行判断,然后置为null
            //关闭网络
            clientCommunicate.stopCommunicate();
        }else{
            Log.w(TAG,"gameThread is  null");
        }
    }


    //GameThread主要用来检测tank爆炸，以及在本地子弹击中bonus的反应
    //之前是使用独立的线程进行运行和检测，这里考虑其他的处理方法.....：
    class GameThread {
        GameThread(){
            gameDto.getMyTank().setEnableFire(true);
        }

        public void runGame() {
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
        }
    }
    //测试我的坦克击中bonus的反应
    //1、停止bonus 2、设置selectView 4/产生爆炸并移除子弹，5、根据当前的tank子弹状态，更新子弹状态
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
        //如果打中地方坦克   1、产生爆炸 2、移除子弹 3、削减血条  3、检查游戏是否结束
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
        //TODO 测试explodeewr
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
            //***************提示敌方坦克的血量,并检查游戏是否结束***************
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
        //每次增加爆炸，则向client发送一个bonus
        if(StaticVariable.CHOSED_RULE== StaticVariable.GAME_RULE.ACTIVITY){
            Tool.sendNewExplode(clientCommunicate,explode);
        }
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
            //向passive传送bonus
            if(StaticVariable.CHOSED_RULE== StaticVariable.GAME_RULE.ACTIVITY){
                Tool.sendNewBonus(clientCommunicate,bonus);
            }
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
            //发送和接收的时间均为33.....
            timer.schedule(new consumeThread(), 3000, 30);
        }
    }
    public class productorThread extends TimerTask {
        @Override
        public void run() {
            //如果初始化prepare完成,就开始传输数据
            if(StaticVariable.remotePrepareInitFlag){
                ComDataF comDataF =null;
                String gameDtoString =null;
                try{
                    //不断发送信息数据
                    comDataF = ComDataPackage.packageToF(StaticVariable.REMOTE_DEVICE_ID+"#",StaticVariable.COMMAND_INFO,gson.toJson(gameDto));
                    clientCommunicate.sendInfo(gson.toJson(comDataF));
                }catch (Exception e){
                    Log.i(TAG,"send Error:" +e);
                }
            }
        }
    }


    //互相communicate的线程
    public class consumeThread extends TimerTask {
        @Override
        public void run() {
            Log.i(TAG,"remoteGameDtos num is "+remoteGameDtos.size());
            if(remoteGameDtos.size()!=0){
                GameDto gameDtoTemp = remoteGameDtos.poll();
                //在这里初始化Enermy坦克的信息
                if(!remoteDtoInitFlag&&gameDtoTemp.getMyTank()!=null&&gameDtoTemp.getMyBlood()!=null&& gameDto.getEnemyTank()==null&&gameDto.getEnemyBlood()==null){
                    //初始化敌方的变量......
                    Log.i(TAG,"初始化敌方的变量......");
                    initRemoteDto(gameDtoTemp);
                    //允许敌方坦克发射
                    gameDto.getEnemyTank().setEnableFire(true);
                    //初始化成功的标志
                    remoteDtoInitFlag = true;
                }

                //如果初始化完成，即开始进行数据消费工作
                //消费工作分为两类 1、activity的消费工作   2、passive的消费工作 3公共的消费工作
                if(remoteDtoInitFlag){
                    /**设置EnemyTank相关的属性**/
                    //这里是不论activity还是passive都要处理的部分：
                    //TODO 注意这里要加上不同分辨率的处理.......注意设置角度为负
                    //设置WeaponDegree相关的信息
                    Log.d(TAG,"EnemyTank weapenDegree is "+gameDtoTemp.getMyTank().getWeaponDegree());
                    gameDto.getEnemyTank().setWeaponDegree(-gameDtoTemp.getMyTank().getWeaponDegree());
                    //设置enermy的坦克相关信息
                    //TODO 注意这里，对坐标进行了转换 但是Y坐标为设定为固定值
                    //Log.i(TAG,"recive origin x is "+gameDtoTemp.getMyTank().getTankPosition_x()+",origin y is "+gameDtoTemp.getMyTank().getTankPosition_y() +"SCALE_SCREEN_WIDTH is :"+SCALE_SCREEN_WIDTH);
                    int tempX = StaticVariable.LOCAL_SCREEN_WIDTH-gameDto.getMyTank().getTankPicture().getWidth()-(int)(gameDtoTemp.getMyTank().getTankPosition_x()*SCALE_SCREEN_WIDTH);
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
                    if(gameDto.getBonus()!=null&&gameDtoTemp.getBonus()!=null){
                        gameDto.getBonus().setIsBonusFired(gameDtoTemp.getBonus().isBonusFired());
                    }

                    //passive的消费工作  //主要有爆炸、bonus
                    if(StaticVariable.CHOSED_RULE== StaticVariable.GAME_RULE.PASSIVE){
                        //对passive端，要设置bonus的相关属性，如果本地的bonus初始化成功，则，开始设置其坐标
                        if(gameDto.getBonus()!=null&&gameDtoTemp.getBonus()!=null){
                            gameDto.getBonus().setBonus_x((int)(gameDtoTemp.getBonus().getBonus_x()*SCALE_SCREEN_WIDTH));
                            gameDto.getBonus().setBonus_y((int)(gameDtoTemp.getBonus().getBonus_y()*SCALE_SCREEN_HEIGHT));
                        }
                        //对passive端，设置子弹相关属性
                        if(gameDtoTemp.getMyTank().getBulletsFire().size()!=0&&gameDto.getEnemyTank().getBulletsFire().size()!=0){
                            //直到为true 直到不为负，才给子弹赋值,如果没有绘制完，则会一直在循环体中.....
                            //TODO 测试看要不要这句
                            //这种处理方法很不好，考虑用另一个线程来处理
                            while(!gameDto.getEnemyTank().isBulletDrawOver());
                            //TODO 感觉子弹的处理还是会很麻烦 这里也有可能出错，即本地的子弹数目不匹配的问题
                            //循环设置子弹的绘制属性，其中循环的参数为获取的子弹链属性 子弹一定要从低到高设置起来.......
                            for(int i = 0;i<gameDtoTemp.getMyTank().getBulletsFire().size();i++){
                                //gameDto.getEnemyTank().getBulletsFire().get(i).setBulletDistance(gameDtoTemp.getMyTank().getBulletsFire().get(i).getBulletDistance());
                                gameDto.getEnemyTank().getBulletsFire().get(i).setBulletDegree(gameDtoTemp.getMyTank().getBulletsFire().get(i).getBulletDegree());
                                gameDto.getEnemyTank().getBulletsFire().get(i).setBulletType(gameDtoTemp.getMyTank().getBulletsFire().get(i).getBulletType());
                                gameDto.getEnemyTank().getBulletsFire().get(i).setBulletPosition_x((int)(gameDtoTemp.getMyTank().getBulletsFire().get(i).getBulletPosition_x()*SCALE_SCREEN_WIDTH));
                                gameDto.getEnemyTank().getBulletsFire().get(i).setBulletPosition_y((int)(gameDtoTemp.getMyTank().getBulletsFire().get(i).getBulletPosition_y()*SCALE_SCREEN_HEIGHT));
                                gameDto.getEnemyTank().getBulletsFire().get(i).setDrawFlag(gameDtoTemp.getMyTank().getBulletsFire().get(i).isDrawFlag());
                            }
                        }
                    }
                }
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
        Log.i(TAG,"game_rule is_1:"+StaticVariable.CHOSED_RULE);
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
        //初始化enemyTank
        EnemyTank enemyTank = (EnemyTank) initTank(gameDtoTemp.getTankType(),false);
        //初始化blood
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

    //发送ID到server/activity端 ，同时，passive端，首先开始进行主动的通讯工作
    private void initRemoteActivity() {
        //网络模式需要向服务器发送东西.....
        if(StaticVariable.CHOSED_MODE==StaticVariable.GAME_MODE.INTERNET){
            Tool.sendSelfIdToServer(this.clientCommunicate);
        }
        Log.i(TAG,"mode:"+StaticVariable.CHOSED_RULE);
        if(StaticVariable.CHOSED_RULE==StaticVariable.GAME_RULE.PASSIVE){
            while(!remoteDeviceACKflag){
                Log.i(TAG,"PASSIVE端发起连接.......");
                if(this.clientCommunicate!=null){
                    Log.i(TAG,"clientCommunicate is ready");
                }else{
                    Log.i(TAG,"clientCommunicate is none");
                }
                Tool.sendSelfIdToActive(this.clientCommunicate);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //local暂时直接开始游戏即可
    private void initLocalActivity() {
        this.startGame();
    }
    private void startGame(){
        Message msgInfo = gameActivityHandler.obtainMessage();
        msgInfo.what = StaticVariable.GAME_STARTED;
        gameActivityHandler.sendMessage(msgInfo);
    }

    /*****************************************这里是与通信相关的方法*****时间差大概在10~30ms之间**********************************/

    //主要用来向buffer中填充数据
    @Override
    public void infoRecived(GameDto gameDtoReviced) {
        remoteGameDtos.offer(gameDtoReviced);   //入列
        //下面是与本地模式相关的代码 联网和蓝牙模式，采用消费者模式消费数据，不采用这种模式
        if(StaticVariable.CHOSED_MODE==StaticVariable.GAME_MODE.LOCAL){
            GameDto gameDtoTemp = remoteGameDtos.poll();
            //在这里初始化Enermy坦克的信息
            if(gameDtoTemp.getMyTank()!=null&&gameDtoTemp.getMyBlood()!=null&& this.gameDto.getEnemyTank()==null&&this.gameDto.getEnemyBlood()==null){
                //初始化敌方的变量......
                //Log.i(TAG,"初始化敌方的变量......");
                initRemoteDto(gameDtoTemp);
                //允许敌方坦克发射
                gameDto.getEnemyTank().setEnableFire(true);
                this.remoteDtoInitFlag = true;
            }
            //如果初始化完成，即开始进行设置工作
            if(this.remoteDtoInitFlag){
                /**设置EnemyTank相关的属性**/

                //TODO 注意这里要加上不同分辨率的处理.......注意设置角度为负
                this.gameDto.getEnemyTank().setWeaponDegree(-gameDtoTemp.getMyTank().getWeaponDegree());
                this.gameDto.getEnemyTank().setTankPosition_x(LOCAL_SCREEN_WIDTH-gameDtoTemp.getMyTank().getTankPosition_x()-this.gameDto.getMyTank().getTankPicture().getWidth());
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
            this.remoteDeviceACKflag = true;
            if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.PASSIVE){
                Log.w(TAG,"INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT cmmand:"+comDataF.getComDataS().getCommad());
                Tool.sendSelfInfoToActive(this.clientCommunicate);
            }else{
                Log.i(TAG,"*******************身份错误_2************************");
            }
            /** activity接受信息数据，并传输自身的信息数据**/
        }else if(command.equals(StaticVariable.INIT_PASSIVE_RESPONSE_SELFINFO)){
            if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY){
                Log.w(TAG,"INIT_PASSIVE_RESPONSE_SELFINFO cmmand:"+comDataF.getComDataS().getCommad());
               //activity接受远程的数据信息
                DeviceInfo remoteDeviceInfo = gson.fromJson(comDataF.getComDataS().getObject(), DeviceInfo.class);
                //Log.i(TAG,"activity recived passive info REMOTE_DENSITY: "+remoteDeviceInfo.screanDesntiy +",REMOTE_SCREEN_HEIGHT:"+remoteDeviceInfo.screanHeight+",REMOTE_SCREEN_WIDTH:"+remoteDeviceInfo.screanWidth);
                //Log.i(TAG,"activity LOCAL_SCREEN_WIDTH:"+LOCAL_SCREEN_WIDTH);
                StaticVariable.REMOTE_DENSITY=remoteDeviceInfo.screanDesntiy;
                StaticVariable.REMOTE_SCREEN_HEIGHT=remoteDeviceInfo.screanHeight;
                StaticVariable.REMOTE_SCREEN_WIDTH=remoteDeviceInfo.screanWidth;
                StaticVariable.SCALE_SCREEN_HEIGHT =(float)StaticVariable.LOCAL_SCREEN_HEIGHT/(float)StaticVariable.REMOTE_SCREEN_HEIGHT;
                StaticVariable.SCALE_SCREEN_WIDTH = (float)LOCAL_SCREEN_WIDTH/(float)StaticVariable.REMOTE_SCREEN_WIDTH;
                Log.i(TAG,"activity SCALE_SCREEN_WIDTH:"+SCALE_SCREEN_WIDTH);
                Tool.sendSelfInfoToPassive(this.clientCommunicate);
            }else{
                Log.i(TAG,"*******************身份错误_3************************");
            }
            /**  passive接受信息数据，并传输初始化完成命令，等待初始化完成命令，**/
        }else if(command.equals(StaticVariable.INIT_ACTIVITE_RESPONSE_SELFINFO)){
            if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.PASSIVE){
                Log.w(TAG,"INIT_ACTIVITE_RESPONSE_SELFINFO cmmand:"+comDataF.getComDataS().getCommad());
                DeviceInfo remoteDeviceInfo = gson.fromJson(comDataF.getComDataS().getObject(), DeviceInfo.class);
                //Log.i(TAG,"passive recived passive info REMOTE_DENSITY: "+remoteDeviceInfo.screanDesntiy +",REMOTE_SCREEN_HEIGHT:"+remoteDeviceInfo.screanHeight+",REMOTE_SCREEN_WIDTH:"+remoteDeviceInfo.screanWidth);
                //Log.i(TAG,"passive LOCAL_SCREEN_WIDTH:"+LOCAL_SCREEN_WIDTH);
                StaticVariable.REMOTE_DENSITY=remoteDeviceInfo.screanDesntiy;
                StaticVariable.REMOTE_SCREEN_HEIGHT=remoteDeviceInfo.screanHeight;
                StaticVariable.REMOTE_SCREEN_WIDTH=remoteDeviceInfo.screanWidth;
                StaticVariable.SCALE_SCREEN_HEIGHT =(float)StaticVariable.LOCAL_SCREEN_HEIGHT/(float)StaticVariable.REMOTE_SCREEN_HEIGHT;
                StaticVariable.SCALE_SCREEN_WIDTH = (float)LOCAL_SCREEN_WIDTH/(float)StaticVariable.REMOTE_SCREEN_WIDTH;
                Log.i(TAG,"passive SCALE_SCREEN_WIDTH:"+SCALE_SCREEN_WIDTH);
                Tool.sendInitFinishedToActive(this.clientCommunicate);
            }else{
                Log.i(TAG,"*******************身份错误_3************************");
            }
/*********************************与游戏控制相关的命令，考虑使用广播来进行控制***************************************************/
             /**   activity初始化完成命令，开始进入游戏，并传输初始化完成命令，**/
        }else if(command.equals(StaticVariable.INIT_PASSIVE_RESPONSE_INIT_FINISHED)){
            if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY){
                Log.w(TAG,"INIT_PASSIVE_RESPONSE_INIT_FINISHED cmmand:"+comDataF.getComDataS().getCommad());
                Tool.sendInitFinishedToPassive(this.clientCommunicate);
                //TODO 启动游戏_activity
                /*****在这里可以启动游戏了._ACTIVITY模式....******/
                StaticVariable.remotePrepareInitFlag = true;
                this.startGame();

            }else{

            }
        }else if(command.equals(StaticVariable.INIT_ACTIVITE_RESPONSE_INIT_FINISHED)){
            if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.PASSIVE){
                Log.w(TAG,"INIT_ACTIVITE_RESPONSE_INIT_FINISHED cmmand:"+comDataF.getComDataS().getCommad());
                //TODO 启动游戏_passive
                /*****在这里可以启动游戏了.....PASSIVE模式******/
                StaticVariable.remotePrepareInitFlag = true;
                this.startGame();
            }else{

            }
        }else if(command.equals(StaticVariable.INIT_PASSIVE_RESPONSE_GAMEOVER)){
            if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY){
                Log.w(TAG,"INIT_PASSIVE_RESPONSE_GAMEOVER cmmand:"+comDataF.getComDataS().getCommad());
                //TODO 结束游戏_ACTIVITY
                /*****在这里判断游戏是否结束....ACTIVITY模式******/

            }else{

            }
        }else if(command.equals(StaticVariable.INIT_ACTIVITE_RESPONSE_GAMEOVER)){
            if(StaticVariable.CHOSED_RULE == StaticVariable.GAME_RULE.ACTIVITY){
                Log.w(TAG,"INIT_ACTIVITE_RESPONSE_GAMEOVER cmmand:"+comDataF.getComDataS().getCommad());
                //TODO 结束游戏_passive
                /*****在这里判断游戏是否结束....passive模式******/

            }else{

            }
        }else if(command.equals(StaticVariable.RESPONSE_FINISHED_CONNECT_DIRECTIRY)) {
                Log.w(TAG, "RESPONSE_FINISHED_CONNECT_DIRECTIRY cmmand:" + comDataF.getComDataS().getCommad());
                 //TODO 中断游戏_direct6
                /*****在这里判断游戏是否主动中断....direction******/
        }else if(command.equals(StaticVariable.RESPONSE_FINISHED_CONNECT_UNDIRECTRIY)){
                Log.w(TAG,"RESPONSE_FINISHED_CONNECT_UNDIRECTRIY cmmand:"+comDataF.getComDataS().getCommad());
                //TODO 中断游戏_undirect
                /*****在这里判断游戏是否主动中断....direction******/


        //注意这里是架构有问题，bonus和子弹和爆炸的触发，只能通过这种方式来通知，下面的消息，只有passive能收到
        // 产生新的子弹
        }else if(command.equals(StaticVariable.ACTIVITY_MAKE_EXPLODE)){
            Log.i(TAG,"ACTIVITY_MAKE_EXPLODE passive端新建一个explode......");
            Explode explode=gson.fromJson(comDataF.getComDataS().getObject(), Explode.class);
            //TODO 这里的坐标注意变化
            addExplode((int)(explode.getDrawCenter_x()*SCALE_SCREEN_WIDTH),
                    (int)(explode.getDrawCenter_y()*SCALE_SCREEN_HEIGHT),
                    explode.getExplodeType());
         //bonus的触发，通过这种方式，产生新的bonux
        }else if(command.equals(StaticVariable.ACTIVITY_MAKE_BONUS)){
            Log.i(TAG,"passive端新建一个bonus......");
            Bonus bonusTemp = gson.fromJson(comDataF.getComDataS().getObject(), Bonus.class);
            int bonusType = bonusTemp.getBonusType();
            Bitmap bonusPicture = BitmapFactory.decodeResource(context.getResources(),StaticVariable.BONUSPICTURE[bonusType]);//0~length-1之间的数
            Bonus bonus = new Bonus(bonusPicture,bonusType);
            //设置bonus
            gameDto.setBonus(bonus);
        }else if(command.equals(StaticVariable.MAKE_BULLET)){
            Log.i(TAG,"passive端新建一个bullet......");
            //TODO 这里可能有类型转换的错误
            MyBullet bullet =  gson.fromJson(comDataF.getComDataS().getObject(), MyBullet.class);
            Bitmap bullet_temp = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.BUTTLE_BASCINFOS[bullet.getBulletType()].getPicture());
            Bitmap bulletPicture = Tool.reBuildImg(bullet_temp,0,1,1,false,true);
            EnemyBullet enemyBullet = new EnemyBullet(bulletPicture,bullet.getBulletType());
            enemyBullet.setBulletDegree(bullet.getBulletDegree());
            //允许发射....
            enemyBullet.setDrawFlag(true);
            //增加敌方子弹
            this.gameDto.getEnemyTank().addBuleetFire(enemyBullet);
        }

    }

    /**
     *主要处理信息数据的显示即可
     */
    @Override
    public void msgRecived(String msg) {
        Log.w(TAG,"reviced msg:"+msg);
        String orginText = this.gameDto.getMsgText().getText().toString();
        orginText=orginText+ "\n"+REMOTE_DEVICE_ID+": "+msg;
        this.gameDto.getMsgText().setText(orginText);

    }

}

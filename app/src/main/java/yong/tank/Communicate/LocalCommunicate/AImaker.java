package yong.tank.Communicate.LocalCommunicate;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import yong.tank.Communicate.ComData.ComDataF;
import yong.tank.Communicate.ComData.ComDataPackage;
import yong.tank.Communicate.InterfaceGroup.ObserverCommand;
import yong.tank.Communicate.InterfaceGroup.ObserverInfo;
import yong.tank.Communicate.InterfaceGroup.ObserverMsg;
import yong.tank.Communicate.InterfaceGroup.Subject;
import yong.tank.Data.GameSendingData;
import yong.tank.Dto.GameDto;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/12/7.
 */

//这里的AI只是简单的做数据转发，并没有做其他细致的工作.....
public class AImaker implements Subject {
    public static final String TAG = "AImaker";
    private Handler handler;
    private GameDto gameDto;
    private boolean threadFlag = true;
    private Context context;
    private int countTime = 0;
    private int AITankDirection = 0;
    //这里的随机为2s内的整数即可.....
    private int randomTime = 30-new Random().nextInt(25);
    private Gson gson = new Gson();
    private SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    // 存放观察者
    private List<ObserverMsg> observerMsgs = new ArrayList<ObserverMsg>();
    private List<ObserverCommand> observerCommands = new ArrayList<ObserverCommand>();
    private List<ObserverInfo> observerInfos = new ArrayList<ObserverInfo>();
    public AImaker(Context context,GameDto gameDto, Handler handler){
        this.gameDto = gameDto;
        this.handler = handler;
        this.context = context;
    }



    public void runAImaker() {
                //TODO 捕捉转化的try catch
                ComDataF comDataF =null;
                String gameSendingDataString =null;
                try{
                    //装载要传送的数据 ,本地需要使用的数据
                    /*//敌方的坦克方向
                    private int EnemyTankDirection;
                    //敌方的坦克角度
                    private int EnemyTankDegree;
                    //敌方的坦克是否要发射
                    private int EnemyTankEnableFire;
                    //敌方的坦克发射力度
                    private int EnemyTankBulletDistance;
                    //敌方否该产生bonus
                    private int EnemyTankEnableBonus;
                    //敌方的血条比例
                    private double EnemyTankBloodNum;*/
                    //TODO 以随机数设计AI的行走
                    countTime++;
                    if(countTime>=randomTime){
                        countTime = 0;
                        randomTime = 30-new Random().nextInt(26);
                        AITankDirection = 1-new Random().nextInt(3);
                        //Log.i(TAG,"AITankDirection:"+AITankDirection);
                    }
                    //TODO 以输入值设计计算AI的角度；
                      //这里需要设计服装的抛物线设计公式......
                    GameSendingData gameSendingData = new GameSendingData(0);
                    //gameSendingData.setEnemyTankDirection(this.gameDto.getMyTank().getTankDirection());
                    gameSendingData.setEnemyTankDirection(AITankDirection);
                    gameSendingData.setEnemyTankDegree(this.gameDto.getMyTank().getWeaponDegree());
                    gameSendingData.setEnemyTankBulletDistance(this.gameDto.getMyTank().getFirePower());
                    gameSendingData.setEnemyTankBulletType(this.gameDto.getMyTank().getSelectedBullets());
                    gameSendingData.setEnemyTankEnableFire(false);
                    //TODO 初始化要完成的工作........，即付给remote相应的变量.....
                    if(this.gameDto.getEnemyTank()!=null&&this.gameDto.getEnemyBlood()!=null){
                        if(this.gameDto.getEnemyTank().getEnableFire()&&this.gameDto.getEnemyBlood().getAllowFire()&&this.gameDto.getEnemyTank().getWeaponPoxition_x()!=0){
                            //enermy开火
                            gameSendingData.setEnemyTankEnableFire(true);
                        }
                    }

                    gameSendingDataString = gson.toJson(gameSendingData);
                //TODO 发送gameDto数据 这里随便给个0
                    comDataF = ComDataPackage.packageToF ("0",StaticVariable.COMMAND_INFO,gameSendingDataString);
                }catch (Exception e){
                    Log.i(TAG,"Error:" +e);
                }
                if(comDataF!=null){
                    this.notifyWatchers(comDataF);
                    try {
                        //Log.i(TAG,"发送数据为："+gameSendingDataString);
                        Log.d(TAG,"发送数据的字节大小为："+gameSendingDataString.getBytes("UTF-8").length);
                       } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
    }



    public void stopThread(){
        this.threadFlag = false;
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
    public void removeInfoObserver(ObserverInfo observerInfo) {
        observerInfos.remove(observerInfos);
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
    public void notifyWatchers(ComDataF comDataF) {
        //处理聊天信息
        if(comDataF.getComDataS().getCommad().equals(StaticVariable.COMMAND_MSG)){
            for(ObserverMsg o:observerMsgs){
                //传入string
                o.msgRecived(comDataF.getComDataS().getObject());
            }
            //处理info信息
        }else if(comDataF.getComDataS().getCommad().equals(StaticVariable.COMMAND_INFO)){
            for(ObserverInfo o:observerInfos){
                //传入对象
                o.infoRecived(ComDataPackage.packageToObject(comDataF.getComDataS().getObject()));
            }
            //处理command相关的信息
        }else {
            for(ObserverCommand o:observerCommands){
                //传入command
                o.commandRecived(comDataF);
            }
        }
    }
}

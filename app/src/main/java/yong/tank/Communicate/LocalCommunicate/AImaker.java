package yong.tank.Communicate.LocalCommunicate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yong.tank.Communicate.ComData.ComDataF;
import yong.tank.Communicate.ComData.ComDataPackage;
import yong.tank.Communicate.InterfaceGroup.ObserverCommand;
import yong.tank.Communicate.InterfaceGroup.ObserverInfo;
import yong.tank.Communicate.InterfaceGroup.ObserverMsg;
import yong.tank.Communicate.InterfaceGroup.Subject;
import yong.tank.Dto.GameDto;
import yong.tank.modal.EnemyBullet;
import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

/**
 * Created by hasee on 2016/12/7.
 */

public class AImaker implements Runnable , Subject {
    public static final String TAG = "AImaker";
    private Handler handler;
    private GameDto gameDto;
    private boolean threadFlag = true;
    private Context context;
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



    public void run() {
        //TODO 初始化要完成的工作........，即付给remote相应的变量.....
        while(this.threadFlag){
            try {
               if(this.gameDto.getEnemyTank()!=null&&this.gameDto.getEnemyBlood()!=null){
                   if(this.gameDto.getEnemyTank().getEnableFire()&&this.gameDto.getEnemyBlood().getAllowFire()&&this.gameDto.getEnemyTank().getWeaponPoxition_x()!=0){
                       //enermy开火
                       tankOnFire();
                   }
                }
                //TODO 捕捉转化的try catch
                ComDataF comDataF =null;
                String gameDtoString =null;
                try{
                gameDtoString = gson.toJson(this.gameDto);
                //TODO 发送gameDto数据 这里随便给个0
                comDataF = ComDataPackage.packageToF ("0",StaticVariable.COMMAND_INFO,gameDtoString);
                }catch (Exception e){
                    Log.i(TAG,"Error:" +e);
                }
                if(comDataF!=null){
                    this.notifyWatchers(comDataF);
                    try {
                        Log.w(TAG,"发送数据的字节大小为："+gameDtoString.getBytes("UTF-8").length);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                Log.w(TAG,"发送数据时间为："+formatTime.format(new Date()));
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    //Aimaker发射子弹.....
    public void tankOnFire(){
        //***************发射子弹*************
        EnemyBullet enemyBullet = initBullet(this.gameDto.getMyTank().getSelectedBullets());
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

    //初始化子弹
    //TODO 注意在local模式在，子弹的路径采用完全不同的计算法方法....即使用必中的发射方法  .......
    //但是，计算切线的方法太麻烦了.....要计算出抛物线公式之后，然后再带入X做计算，以后处理吧....
    // 这里做将初速度随机增减处理后，计算坐标即可.....
    //对AI模式，子弹的处理....同AI模式即可，即，通过传递真实的子弹路径即可
    //对非AI模式，子弹的处理为每次赋值，都创建新的bullet的list对象即可......
    private EnemyBullet initBullet(int bulletType){
        Bitmap bullet_temp = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.BUTTLE_BASCINFOS[bulletType].getPicture());
        Bitmap bulletPicture = Tool.reBuildImg(bullet_temp,0,1,1,false,true);
        EnemyBullet enemyBullet = new EnemyBullet(bulletPicture,StaticVariable.BUTTLE_BASCINFOS[bulletType]);
        //初始化子弹的角度
        int initDegree = 0;
        double initDistance  = 0;
        initDegree = this.gameDto.getMyTank().getWeaponDegree();
        initDistance = this.gameDto.getMyTank().getFirePower();
        //TODO 这里将distance做随机处理
        if(initDistance<=0){
            //小于0则设定一个固定值
            initDistance= 0.5;
        }
        //Log.i(TAG,"initDegree is *************************:"+initDegree);
        //Log.i(TAG,"initDistance is *************************:"+initDistance);
        //Log.i(TAG,"init x is *************************:"+this.gameDto.getEnemyTank().getWeaponPoxition_x());
        //Log.i(TAG,"init y is *************************:"+this.gameDto.getEnemyTank().getWeaponPoxition_y());
        //注意这里，角度为负数
        enemyBullet.setBulletDegree(initDegree);
        enemyBullet.setBulletDistance(initDistance);

        //计算并初始化子弹的路径
        enemyBullet.setFirePath(Tool.getEnermyBulletPath(this.gameDto.getEnemyTank().getWeaponPoxition_x(),
                this.gameDto.getEnemyTank().getWeaponPoxition_y(),
                initDistance,
                initDegree,
                false,this.gameDto.getMyTank().getSelectedBullets()));
        //允许发射....
        enemyBullet.setDrawFlag(true);
        //初始化坦克的位置
        //bullet.setBulletPosition_x(this.gameDto.getMyTank().getWeaponPoxition_x());
        //bullet.setBulletPosition_y(this.gameDto.getMyTank().getWeaponPoxition_y());
        return enemyBullet;
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

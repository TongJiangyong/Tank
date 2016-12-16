package yong.tank.Communicate.LocalCommunicate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

import yong.tank.Communicate.ComData.ComDataF;
import yong.tank.Communicate.ComData.ComDataPackage;
import yong.tank.Communicate.InterfaceGroup.ObserverCommand;
import yong.tank.Communicate.InterfaceGroup.ObserverInfo;
import yong.tank.Communicate.InterfaceGroup.ObserverMsg;
import yong.tank.Communicate.InterfaceGroup.Subject;
import yong.tank.Dto.GameDto;
import yong.tank.modal.MyBullet;
import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

/**
 * Created by hasee on 2016/12/7.
 */

public class AImaker implements Runnable , Subject {
    private Handler handler;
    private GameDto gameDto;
    private boolean threadFlag = true;
    // 存放观察者
    private List<ObserverMsg> observerMsgs = new ArrayList<ObserverMsg>();
    private List<ObserverCommand> observerCommands = new ArrayList<ObserverCommand>();
    private List<ObserverInfo> observerInfos = new ArrayList<ObserverInfo>();
    public AImaker(GameDto gameDto,Handler handler){
        this.gameDto = gameDto;
        this.handler = handler;
    }



    public void run() {
        while(this.threadFlag){
            try {
                //休眠20ms
                if(this.gameDto.getEnemyTank().getEnableFire()){
                    //TODO 产生一个新的子弹，计算子弹发射路径 这里一定要注意和其他两种模式的区别，即
                    //其他两种模式的子弹，都是只传输当前坐标，但是lcoal模式的子弹，要设置好所有的路径，......！！！！
                }
                //TODO 发送gameDto数据
                //this.notifyWatchers();
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    //初始化子弹
    //TODO 注意在local模式在，子弹的路径采用完全不同的计算法方法....即使用必中的发射方法  .......
    //但是，计算切线的方法太麻烦了.....要计算出抛物线公式之后，然后再带入X做计算，以后处理吧....这里做将初速度随机增减处理后，计算坐标即可.....
    private MyBullet initBullet(int bulletType){
        Bitmap bullet_temp = BitmapFactory.decodeResource(this.context.getResources(), StaticVariable.BUTTLE_BASCINFOS[bulletType].getPicture());
        Bitmap bulletPicture = Tool.reBuildImg(bullet_temp,0,1,1,false,false);
        MyBullet myBullet = new MyBullet(bulletPicture,StaticVariable.BUTTLE_BASCINFOS[bulletType]);
        //初始化坦克的性能
        myBullet.setBulletDegree(tankDegree);
        myBullet.setBulletDistance(distance);
        //计算并初始化子弹的路径
        myBullet.setFirePath(Tool.getBulletPath(this.gameDto.getMyTank().getWeaponPoxition_x(),
                this.gameDto.getMyTank().getWeaponPoxition_y(),
                distance,
                tankDegree,
                false,this.gameDto.getMyTank().getSelectedBullets()));
        //允许绘制路径
        myBullet.setDrawFlag(true);
        //初始化坦克的位置
        //bullet.setBulletPosition_x(this.gameDto.getMyTank().getWeaponPoxition_x());
        //bullet.setBulletPosition_y(this.gameDto.getMyTank().getWeaponPoxition_y());
        return myBullet;
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

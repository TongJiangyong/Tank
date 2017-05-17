package yong.tank.tool;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;

import yong.tank.R;
import yong.tank.modal.BulletBascInfo;
import yong.tank.modal.TankBascInfo;
import yong.tank.modal.User;

import static yong.tank.R.mipmap.s;

/**
 * Created by hasee on 2016/10/28.
 */

public class StaticVariable {

    public static long GAME_START_TIME = 0 ;
    //设置游戏的逻辑帧数为25
    public static int LOGICAL_FRAME = 25;
    //设置游戏的关键帧增长率
    public static int KEY_FRAME_COUNT = 3;
    //设置关键帧的帧为： ----3-6-9-12-15-18-----
    public static long KEY_FRAME = 3;
    //设置 游戏的最大跳过帧数为5：
    public static int  MAX_FRAMESKIP = 5;
    public static int SKIP_TICKS = 1000 / LOGICAL_FRAME;

    //设置TANK的射程圈比例
    public static double TANK_SHOT_CIRCLE = 1.40;
    //初始化物理场  这里的物理场应该和手机屏幕适应，设置规则为1/2*g*t*t = 屏幕高度 ，其中，t设置为0.5s 即G = 8*屏幕 高度
    public static double GRAVITY=0;
    //设置游戏的类名
    public static String[] VIEW_LIST ={
            "yong.tank.Game_Activity.View.GameView",
            "yong.tank.Game_Activity.View.BloodView",
            "yong.tank.Game_Activity.View.BonusView",
            "yong.tank.Game_Activity.View.ExplodeView",
            "yong.tank.Game_Activity.View.PlayerView",
};
    //设置游戏的模式
    public enum GAME_MODE {
        LOCAL, INTERNET, BLUETOOTH
    }
    //设置游戏的角色：主动/被动
    public enum GAME_RULE {
        ACTIVITY,PASSIVE
    }
    public static GAME_MODE CHOSED_MODE = GAME_MODE.LOCAL;
    public final static int[] GAMEMODE= {0,1,2};
    public static GAME_RULE CHOSED_RULE =GAME_RULE.ACTIVITY;
    //设置tank的图片
    public static int[] TANKPICTURE={
            R.mipmap.tank_0,
            R.mipmap.tank_1,
            R.mipmap.tank_2,
            R.mipmap.tank_3,
           // R.mipmap.tank_4,  暂时不加这个，以后再加......
    };

    public static int[] TANKPICTURE_NO_ARM={
            R.mipmap.tank_0_no_arm,
            R.mipmap.tank_1_no_arm,
            R.mipmap.tank_2_no_arm,
            R.mipmap.tank_3_no_arm,
            // R.mipmap.tank_4,  暂时不加这个，以后再加......
    };

    public static int[] MAPPICTURE={
            R.mipmap.bg_game,
    };
    //远程设备的基本信息 高宽、密度
    public static int REMOTE_SCREEN_WIDTH;
    public static int REMOTE_SCREEN_HEIGHT;
    public static float SCALE_SCREEN_HEIGHT;
    public static float SCALE_SCREEN_WIDTH;
    public static float REMOTE_DENSITY;
    //本地设备的基本信息 高宽、密度
    public static float LOCAL_DENSITY;
    public static int LOCAL_SCREEN_WIDTH;
    public static int LOCAL_SCREEN_HEIGHT;

    //tank的基本信息 TankBascInfo(int type, int blood, int speed, int power, int picture, String tankName, String describeInfo) {
    //speed 走完半程所需的秒数*10 为了配合绘制斜杠度量衡的方法
    public static TankBascInfo[] TANKBASCINFO = {
            new TankBascInfo(0,30,50,30,StaticVariable.TANKPICTURE_NO_ARM[0],"T-34","简介：很牛B的坦克A"+"\n"+",有多牛呢？"),
            new TankBascInfo(1,30,40,50,StaticVariable.TANKPICTURE_NO_ARM[1],"M4谢尔曼","简介：很牛B的坦克B"+"\n"+",比上面牛"),
            new TankBascInfo(2,30,30,40,StaticVariable.TANKPICTURE_NO_ARM[2],"虎式-1","简介：很牛B的坦克C"+"\n"+"，上五楼不费劲"),
            new TankBascInfo(3,30,50,70,StaticVariable.TANKPICTURE_NO_ARM[3],"豹式-2","简介：很牛B的坦克D"+"\n"+"，最后的，总是最好的"),
    };
    //设置坦克的填装时间为2s
    public static int TANK_LOADING_TIME = 2;
    public static String HELPINFO = "这里加一些帮助信息";
    public static String STATEMENTINFO =
            "   这个程序是自己为了熟悉android studio和apk开发流程所编写的学习程序,\n" +
            "   程序中代码和程序中所用到图片等资源都来源于网络\n" +
            "   代码和参考的用例程序均托管在github中，地址为：\n" +
            "   https://github.com/TongJiangyong/Tank";
    public static int SELECT_WORD_SIZE =13;
    public static int SELECT_DECRIBE_SIZE =15;
    //设置程序画笔的统一宽度
    public static int STROKEWIDTH=3;
    public static int BLOOD =  R.mipmap.blood;
    public static int POWERR =  R.mipmap.power;
    public static int BLOODBLOCK =  R.mipmap.bloodbox_2;

    public static int MYTANKEFORWARD = 1;
    public static int MYTANKEBACK = -1;
    public static int TANKESTOP = 0;
    public static int ENTANKEFORWARD = 1;
    public static int ENTANKEBACK = -1;

//初始化子弹的变量
//public BulletBascInfo(int type, int speed, int power, int picture, String bulletName)
    //speed的设定为子弹如果平射，1s走过的距离是屏幕宽度的多少倍
    public static BulletBascInfo[] BUTTLE_BASCINFOS = {
            new BulletBascInfo(0,1.7,0.2,R.mipmap.origin,"普通弹"), //0
            new BulletBascInfo(0,1.8,0.4,R.mipmap.armor,"穿甲弹"),  //1
            new BulletBascInfo(0,1.7,0.3,R.mipmap.ice,"冰弹"),    //2
            new BulletBascInfo(0,1.9,0.2,R.mipmap.s_s,"加速弹"),   //3
            new BulletBascInfo(0,130,50, s,"子母弹_母"),
    };
    //初始化子弹的类型
    public static int ORIGIN = 0;
    public static int ARMOR = 1;
    public static int ICE = 2;
    public static int S_S = 3;
    //初始化子弹信息
    public static int BUTTON_NUM_ORIGN = 10;
    public static int BUTTON_NUM_MAX = 999;
    public static int[][] TANK_BULLET_YPTE ={
            {StaticVariable.ORIGIN,BUTTON_NUM_MAX},
            {StaticVariable.ARMOR,StaticVariable.BUTTON_NUM_ORIGN},
            {StaticVariable.ICE,StaticVariable.BUTTON_NUM_ORIGN},
            {StaticVariable.S_S,StaticVariable.BUTTON_NUM_ORIGN},
    };

    public static int INTERVAL_TIME = 1;    //初始化与路径相关参数 ，即每隔5帧画一幅图
    public static  int PREVIEWPATHLENGTH = 50; //priewpath有12个点,测试使用50个
    public static int PATHLENGTH  = 50; //path有40个点

    //初始化爆炸的图像
    public static int[] EXPLODESPICTURE_GROUND={
            R.mipmap.baozha1,
            R.mipmap.baozha2,
            R.mipmap.baozha3,
            R.mipmap.baozha4,
            R.mipmap.baozha5,
            R.mipmap.baozha6,
            R.mipmap.baozha7,
            R.mipmap.baozha8,
            R.mipmap.baozha9,
            //R.mipmap.baozha0,
    };
    public static int[] EXPLODESPICTURE_TANKE={
            R.mipmap.tank_baozha1,
            R.mipmap.tank_baozha2,
            R.mipmap.tank_baozha3,
            R.mipmap.tank_baozha4,
            R.mipmap.tank_baozha5,
            //R.mipmap.baozha0,
            // R.mipmap.tank_4,  暂时不加这个，以后再加......
    };
    public static Bitmap[]  EXPLODESONGROND = new Bitmap[EXPLODESPICTURE_GROUND.length];
    public static Bitmap[]  EXPLODESONTANK =  new Bitmap[EXPLODESPICTURE_TANKE.length];
    public static int EXPLODE_TYPE_GROUND = 0;
    public static int EXPLODE_TYPE_TANK = 1;
   //设置bonus的图像,注意，和子弹的顺序要对应
   public static int[] BONUSPICTURE={
           R.mipmap.armor_1, //F->穿甲弹
           R.mipmap.ice_1,//F->冰弹
           R.mipmap.s_1,//F->加速弹
           // R.mipmap.tank_4,  暂时不加这个，以后再加......
   };
    //bonus的速度设置为5s穿过屏幕，这里的speed，是指一帧走过的像素点
    public static int BONUS_SPEED = 0;
    public static int BONUS_SCALE = 0;
    //bonus走完一个屏幕需要的时间
    public static int BONUS_TIME = 5;
    //调节bonus震动的频率
    public static int BONUS_STEP = 6;
    public static int BONUS_Y_INIT = 0;
    //与message相关
    //gameservice中toast更新
    public final static int MSG_TOAST = 0;
    //button中message更新
    public final static int MSG_UPDATE = 1;
    //网络连接成功
    public final static int MSG_CONNECT_SUCCESS = 2;
    //网络连接失败
    public final static int MSG_CONNECT_ERROR = 3;
    //网络通信故障
    public final static int MSG_COMMUNICATE_ERROR=4;
    //网络主动断开
    public final static int MSG_COMMUNICATE_OUT=5;



    //定义蓝牙相关的量：
    public static final String BLUE_DEVICE_NAME = "YONG";
    public static final String BLUE_FAILED_MESSAGE = "BLUE_FAILED_MESSAGE";
    public static final String BLUE_LOST_MESSAGE = "BLUE_LOST_MESSAGE";
    //蓝牙连接状态
    public static  int  BLUE_STATE =0;
    //蓝牙触发代码
    public static final int REQUEST_CODE_BLUETOOTH_ON = 1313;
    //选择要连接的蓝牙设备后，触发的代码
    public static final int CHOSED_BLUT_DEVICE=1312;
    //蓝牙连接出错
    public final static int BLUE_CONNECT_ERROR=6;
    //蓝牙通讯错误
    public final static int BLUE_COMMUNICATE_ERROR=7;
    //蓝牙主动连接成功
    public final static int BLUE_CONNECT_SUCCESS_ACTIVE = 8;
    //蓝牙被动连接成功
    public final static int BLUE_CONNECT_SUCCESS_PASSIVE = 9;
    //允许使用蓝牙
    public final static int BLUE_ENABLE_SEND_WRITE = 10;
    //blue的toast
    public final static int BLUE_TOAST = 100;

    //本地初始化成功
    public final static int LOCAL_INIT_OVER = 11;
    //开始游戏
    public final static int GAME_STARTED = 12;

    /* 信息交互coomand相关的代码 */
    public final static String INIT_SEND_ID_SERVER ="1"; //发送数据到server端
    public final static String INIT_PASSIVE_REQUEST_CONNECT ="2"; //passive发送连接命令到activity端，并传递自身的ID号、确认信息
    public final static String INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT ="3"; //activity确认连接到的passiveId，并发送确认信息
    public final static String INIT_PASSIVE_RESPONSE_SELFINFO ="4";    //passive接受确认信息，并传输自身的信息数据
    public final static String INIT_ACTIVITE_RESPONSE_SELFINFO ="5";    //activity接受信息数据，并传输自身的信息数据
    public final static String INIT_PASSIVE_RESPONSE_INIT_FINISHED ="6";  //passive接受信息数据，并传输初始化完成命令，等待初始化完成命令，然后开始游戏
    public final static String INIT_ACTIVITE_RESPONSE_INIT_FINISHED ="7";  //activity初始化完成命令，开始进入游戏，并传输初始化完成命令
    public final static String INIT_PASSIVE_RESPONSE_GAMEOVER ="8";         //PASSIVE发送一轮游戏结束后的命令
    public final static String INIT_ACTIVITE_RESPONSE_GAMEOVER ="9";        //activity发送一轮游戏结束后的命令
    public final static String RESPONSE_FINISHED_CONNECT_DIRECTIRY ="10";        //发送断开命令------互相直接发送
    public final static String RESPONSE_FINISHED_CONNECT_UNDIRECTRIY ="11";        //发送断开命令------服务器发送
    //定义的command：
    public  final static String COMMAND_MSG =  "12";
    public  final static String COMMAND_INFO =  "13";

    public final static String ACTIVITY_MAKE_BONUS ="14";
    public final static String ACTIVITY_MAKE_EXPLODE ="15";
    public final static String MAKE_BULLET ="16";
    //server已经准备好的标志
    public final static String SERVER_READY = "17";


//定义网络中双方是否初始化完全，DTO未初始化
    //TODO 注意在断开时，要结束这个变量
    public static boolean REMOTE_PREPARED_INIT_FLAG =false;


    /* 网络连接中，另一个设备的ID*/
    public static String REMOTE_DEVICE_ID = null;
    /* 网络连接中，自己的用户信息*/
    public static User LOCAL_USER_INFO = null;
    /* 网络连接中，房间的ID*/
    public static String REMOTE_ROOM_ID = null;
   /* 与存储相关的数据 */
    public final static String TANK_USER_INFO="tank_userinfo.bat";
    public final static String TANK_RECORD_INFO="tank_recordinfo.bat";
    //好像不能通过这种方法获取root的internal目录 ,间接用这种方法来获取
    //public final static File USER_FILE = new File(Environment.getExternalStorageDirectory().toString() + File.separator + StaticVariable.TANK_USER_INFO);
    public static File USER_FILE = null;
    public static File RECORD_FILE = new File(Environment.getExternalStorageDirectory().toString() + File.separator + StaticVariable.TANK_RECORD_INFO);

    public final static int VISIBLE_TIME =200;


    /* 服务器初始化相关的ip */
    //服务器路径
    public static  String API_SERVER = "http://192.168.1.122:8080/webService";
    //public static  String API_SERVER = "http://192.168.191.1:8080/webService";
    public static  String API_SERVER_XIAN = "http://115.154.191.5:8080/webService";
    //设置通讯相关的部分 String ip, int port
    public static String SERVER_IP = "192.168.1.122";
    //public static String SERVER_IP = "192.168.191.1";
    //public static String SERVER_IP = "192.168.1.122";
    //public static String SERVER_IP_XIAN = "115.154.191.5";
    public static int SERVER_PORT =  9999;

    public static final int READ_BYTE =8192;
}

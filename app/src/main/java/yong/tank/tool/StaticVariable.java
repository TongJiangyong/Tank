package yong.tank.tool;

import android.graphics.Bitmap;

import yong.tank.R;
import yong.tank.modal.BulletBascInfo;
import yong.tank.modal.TankBascInfo;

import static yong.tank.R.mipmap.s;

/**
 * Created by hasee on 2016/10/28.
 */

public class StaticVariable {

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public final static int[] GAMEMODE= {1,2,3};
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
    //tank的基本信息 TankBascInfo(int type, int blood, int speed, int power, int picture, String tankName, String describeInfo) {
    public static TankBascInfo[] TANKBASCINFO = {
            new TankBascInfo(0,30,50,30,StaticVariable.TANKPICTURE_NO_ARM[0],"T-34","简介：很牛B的坦克A,有多牛呢？"),
            new TankBascInfo(1,30,40,50,StaticVariable.TANKPICTURE_NO_ARM[1],"M4谢尔曼","简介：很牛B的坦克B，比上面牛"),
            new TankBascInfo(2,30,30,40,StaticVariable.TANKPICTURE_NO_ARM[2],"虎式-1","简介：很牛B的坦克C，上五楼不费劲"),
            new TankBascInfo(3,30,50,70,StaticVariable.TANKPICTURE_NO_ARM[3],"豹式-2","简介：很牛B的坦克D，最后的，总是最好的"),
    };
    public static String HELPINFO = "这里加一些帮助信息";
    public static String STATEMENTINFO =
            "   这个程序是自己为了熟悉android studio和apk开发流程所编写的学习程序,\n" +
            "   程序中代码和程序中所用到图片等资源都来源于网络\n" +
            "   代码和参考的用例程序均托管在github中，地址为：\n" +
            "   https://github.com/TongJiangyong/Tank";
    public static int selectWordSize=40;
    public static int selectDescribeSize=60;
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
    public static BulletBascInfo[] bulletBascInfos = {
            new BulletBascInfo(0,150,50,R.mipmap.origin,"普通弹"),
            new BulletBascInfo(0,20,50,R.mipmap.armor,"穿甲弹"),
            new BulletBascInfo(0,20,50,R.mipmap.ice,"冰弹"),
            new BulletBascInfo(0,20,50,R.mipmap.s_s,"加速弹"),
            new BulletBascInfo(0,20,50, s,"子母弹_母"),
    };
    //初始化物理场  这里的物理场应该和手机屏幕适应，但是没有想到好方法
    public static double GRAVITY=11;
    public static double INTERVAL_TIME = 0.5;    //初始化与路径相关参数
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
    public static int BONUS_SPEED = 10;
    public static int BONUS_STEP =50 ;
    public static final int BONUS_Y = 5;
}

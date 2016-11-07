package yong.tank.tool;

import yong.tank.R;
import yong.tank.modal.TankBascInfo;

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
            new TankBascInfo(0,30,30,30,StaticVariable.TANKPICTURE_NO_ARM[0],"T-34","简介：很牛B的坦克A,有多牛呢？"),
            new TankBascInfo(1,30,40,50,StaticVariable.TANKPICTURE_NO_ARM[1],"M4谢尔曼","简介：很牛B的坦克B，比上面牛"),
            new TankBascInfo(2,30,50,40,StaticVariable.TANKPICTURE_NO_ARM[2],"虎式-1","简介：很牛B的坦克C，上五楼不费劲"),
            new TankBascInfo(3,30,20,70,StaticVariable.TANKPICTURE_NO_ARM[3],"豹式-2","简介：很牛B的坦克D，最后的，总是最好的"),
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
    public static int BLOODBLOCK =  R.mipmap.bloodbox_3;

    public static int MYTANKEFORWARD = 1;
    public static int MYTANKEBACK = -1;
    public static int TANKESTOP = 0;
    public static int ENTANKEFORWARD = 1;
    public static int ENTANKEBACK = -1;
}

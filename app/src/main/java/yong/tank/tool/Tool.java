package yong.tank.tool;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import yong.tank.Communicate.ComData.ComDataF;
import yong.tank.Communicate.ComData.ComDataPackage;
import yong.tank.Communicate.InterfaceGroup.ClientCommunicate;
import yong.tank.LocalRecord.LocalRecord;
import yong.tank.SelectTank_Activity.modal.PictureInfo;
import yong.tank.modal.Bonus;
import yong.tank.modal.DeviceInfo;
import yong.tank.modal.Explode;
import yong.tank.modal.Point;
import yong.tank.modal.User;
import yong.tank.modal.abstractGoup.Bullet;

import static yong.tank.tool.StaticVariable.ACTIVITY_MAKE_BONUS;
import static yong.tank.tool.StaticVariable.ACTIVITY_MAKE_EXPLODE;
import static yong.tank.tool.StaticVariable.INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT;
import static yong.tank.tool.StaticVariable.INIT_ACTIVITE_RESPONSE_GAMEOVER;
import static yong.tank.tool.StaticVariable.INIT_ACTIVITE_RESPONSE_INIT_FINISHED;
import static yong.tank.tool.StaticVariable.INIT_ACTIVITE_RESPONSE_SELFINFO;
import static yong.tank.tool.StaticVariable.INIT_PASSIVE_REQUEST_CONNECT;
import static yong.tank.tool.StaticVariable.INIT_PASSIVE_RESPONSE_GAMEOVER;
import static yong.tank.tool.StaticVariable.INIT_PASSIVE_RESPONSE_INIT_FINISHED;
import static yong.tank.tool.StaticVariable.INIT_PASSIVE_RESPONSE_SELFINFO;
import static yong.tank.tool.StaticVariable.MAKE_BULLET;
import static yong.tank.tool.StaticVariable.RESPONSE_FINISHED_CONNECT_DIRECTIRY;

/**
 * Created by hasee on 2016/10/28.
 */

public class Tool {

    //这里统一使用matrix来处理图像.....但是感觉有点麻烦....没办法啊.....

    /**
     *
     * @param orginBitmap 原始图像
     * @param degrees  旋转角度
     * @param src_x   x轴缩放
     * @param sc_y     y轴缩放
     * @param x_symmetrical   是否x轴旋转
     * @param y_symmetrical   是否y轴旋转
     * @return
     */
    public static Bitmap reBuildImg(Bitmap orginBitmap,float degrees,float src_x,float sc_y,boolean x_symmetrical ,boolean y_symmetrical){
        Bitmap reBuildImg=null;
        Matrix matrix = new Matrix();
        if(y_symmetrical){
            float[] matrix_value1=new float[]{-1f,0f,0f,0f,1f,0f,0f,0f,1f};
            matrix.setValues(matrix_value1);
            reBuildImg = Bitmap.createBitmap(orginBitmap, 0, 0, orginBitmap.getWidth(), orginBitmap.getHeight(), matrix, true);
            return  reBuildImg;
        }
        if(x_symmetrical){
            float[] matrix_value=new float[]{1f,0f,0f,0f,-1f,0f,0f,0f,1f};
            matrix.setValues(matrix_value);
            reBuildImg = Bitmap.createBitmap(orginBitmap, 0, 0, orginBitmap.getWidth(), orginBitmap.getHeight(), matrix, true);
            return  reBuildImg;
        }
        // 这里为平移的方法.....matrix.postTranslate
        //旋转
        //matrix.setTranslate(0,orginBitmap.getHeight()/2);

        matrix.setRotate(degrees,orginBitmap.getWidth()/2,orginBitmap.getHeight()/2);
        //缩放
        matrix.postScale(src_x,sc_y);
        reBuildImg = Bitmap.createBitmap(orginBitmap, 0, 0, orginBitmap.getWidth(), orginBitmap.getHeight(), matrix, true);
        return  reBuildImg;
    }

    //释放bitmap的空间
    public static Bitmap releaseBitmap(Bitmap a){
        if (a != null && !a.isRecycled()) {/*a.recycle();*/a=null;}
        return a ;
    }

    //设置每一个选择图片的Rect，rect有类判断是否点在rect中contain....


    /**
     *
     * @param mapPicture  图片
     * @param x      图片左上角横坐标
     * @param y        图片左上角纵坐标
     */
    public static  void  setRect(PictureInfo mapPicture, int x, int y) {
        if(mapPicture.getRect()==null){
            Rect rect =new Rect(x,y,x+mapPicture.getPicture().getWidth(),y+mapPicture.getPicture().getHeight());
            mapPicture.setRect(rect);
        }

    }

    //绘制同一中心的图像
    /**
     *
     * @param canvas
     * @param originPicture
     * @param laterPicture
     */
    public static void drawCentral(Canvas canvas, PictureInfo originPicture, Bitmap laterPicture){
        float picture_x =originPicture.getRect().centerX()-laterPicture.getWidth()/2;
        float picture_y = originPicture.getRect().centerY()-laterPicture.getHeight()/2;
        canvas.drawBitmap(laterPicture,picture_x ,picture_y , null);
    }


    //******************************************************************************************************************************
    //获取每张小图片的宽度
    public static float getlittlepicture_x(){
        return 0;
    }
    //获取每张小图片的高度
    public static float getlittlepicture_y(){
        return 0;
    }

    //******************************************************************************************************************************

    //一个改变图片长宽的方法   w 是新的宽度。。h是新的高度
    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {

        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg,0, 0, width,height, matrix, true);
        return resizedBitmap;
    }

    public static Bitmap resizeImage(Bitmap bitmap, float w, float h) {

        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        float newWidth = w;
        float newHeight = h;
        float scaleWidth = ( newWidth) / width;
        float scaleHeight = (newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg,0, 0, width,height, matrix, true);
        return resizedBitmap;
    }
//******************************************************************************************************************************

    //判断两个矩形是否相撞、、、以左上角的点为基准
    public static boolean checkcrash(float x1,float y1,float w1 , float h1,
                                     float x2,float y2,float w2 , float h2){


        if(

                x2>=x1 && x2<=x1+w1 && y2>=y1 && y2<=y1+h1||   //x2的左上角的顶点
                        x2+w2 >= x1 && x2+w2<=x1+w1 && y2>=y1 && y2<=y1+h1|| //x2右上角的点
                        x2>=x1 && x2<=x1+w1 && y2 + h2 <= y1 + h1 && y2+h2>=y1||
                        x2+w2 >= x1 && x2+w2 <= x1+w1 && y2+h2 <= y1+ h1 && y2+h2 >= y1||
                        (
                                x1>=x2 && x1<=x2+w2 && y1>=y2 && y1<=y2+h2||   //x2的左上角的顶点
                                        x1+w1 >= x2 && x1+w1<=x2+w2 && y1>=y2 && y1<=y2+h2|| //x2右上角的点
                                        x1>=x2 && x1<=x2+w2 && y1 + h1 <= y2 + h2 && y1+h1>=y2||
                                        x1+w1 >= x2 && x1+w1 <= x2+w2 && y1+h1 <= y2+ h2 && y1+h1 >= y2
                        )

            //被注释掉的地方有错。。。但是目前不知道怎么错的。。

                ){//x2左下角的点
            return true; //两个矩形相碰撞
        }


        return false;//两个矩形没有碰撞

    }


    public static boolean checkcrash(int x1,int y1,int w1 , int h1,
                                     int x2,int y2,int w2 , int h2){

        if(
                x2>=x1 && x2<=x1+w1 && y2>=y1 && y2<=y1+h1||   //x2的左上角的顶点
                        x2+w2 >= x1 && x2+w2<=x1+w1 && y2>=y1 && y2<=y1+h1|| //x2右上角的点
                        x2>=x1 && x2<=x1+w1 && y2 + h2 <= y1 + h1 && y2+h2>=y1||
                        x2+w2 >= x1 && x2+w2 <= x1+w1 && y2+h2 <= y1+ h1 && y2+h2 >= y1||
                        (
                                x1>=x2 && x1<=x2+w2 && y1>=y2 && y1<=y2+h2||   //x2的左上角的顶点
                                        x1+w1 >= x2 && x1+w1<=x2+w2 && y1>=y2 && y1<=y2+h2|| //x2右上角的点
                                        x1>=x2 && x1<=x2+w2 && y1 + h1 <= y2 + h2 && y1+h1>=y2||
                                        x1+w1 >= x2 && x1+w1 <= x2+w2 && y1+h1 <= y2+ h2 && y1+h1 >= y2
                        )

                ){//x2左下角的点
            return true; //两个矩形相碰撞
        }


        return false;//两个矩形没有碰撞
    }
    //******************************************************************************************************************************
//判断一个点是否在某个区域里面
    public static boolean pointinarea(int px, int py ,int x,int y,int w,int h){
        if(px>=x && px<=x+w && py >= y &&py <= y+h){
            return true ;     //这个点这这个矩形里面
        }
        return false ;       //这个点没有在这个区域里面
    }

    public static boolean pointinarea(float px, float py ,float x,float y,float w,float h){
        if(px>=x && px<=x+w && py >= y &&py <= y+h){
            return true ;     //这个点这这个矩形里面
        }
        return false ;       //这个点没有在这个区域里面
    }
//******************************************************************************************************************************

    public static int Random(int xiaoyu){//随机数发生器   小于“xiaoyu ” 大于0
        return (int)(Math.random()/0.0001)%xiaoyu;

    }

    public static int Random(float xiaoyu){//随机数发生器   小于“xiaoyu ” 大于0
        return (int) ((int)(Math.random()/0.0001)%xiaoyu);

    }
    public static Bitmap shifang(Bitmap a){
        if (a != null && !a.isRecycled()) {/*a.recycle();*/a=null;}
        return a ;
    }

    //计算bonus的路径
    public static List<Point> getBonusPath(Bitmap bonusPicture) {
        //这里关联speed和distance，暂时不处理
        List<Point> bulletPath = new ArrayList<>();
        int direction =new Random().nextInt(2); //生成随机方向
        int bonus_x; //这里应该等于bonuspicture的宽度
        int bonus_y_init = StaticVariable.LOCAL_SCREEN_HEIGHT /StaticVariable.BONUS_Y;  //初始为1/5处的地方
        int bonus_y;
        int speed=StaticVariable.BONUS_SPEED;
        //TODO 振幅为图片的宽度乘以比例
        int scale = StaticVariable.LOCAL_SCREEN_HEIGHT/10;
        if(direction==0){
            bonus_x=0;
        }else{
            bonus_x= StaticVariable.LOCAL_SCREEN_WIDTH;
            speed = -speed;
        }
        while(bonus_x>=0&&bonus_x<=StaticVariable.LOCAL_SCREEN_WIDTH){
            bonus_x=bonus_x+Tool.dip2px(StaticVariable.LOCAL_DENSITY, speed);
            //注意这里除法是易错点
            bonus_y=bonus_y_init +(int)(Math.sin((double)bonus_x/StaticVariable.BONUS_STEP)*scale);
            Point point = new Point(bonus_x,bonus_y,0,false);
            bulletPath.add(point);
        }
        return bulletPath;
    }
    //将这个抽象为函数，然后调用....
    //路径计算好以后，怎么给子弹
    //写成一个函数，然后计算返回一系列的点和角度即可.....List<Point>
    //这里感觉不对，以后做处理看看....先处理互相传输数据的模式......
    public static List<Point> getMyBulletPath(int init_x, int init_y, double bulletDistance, int bulletDegree, boolean isPreView, int selectedBullets) {
        //这里关联speed和distance，暂时不处理
        List<Point> bulletPath = new ArrayList<Point>();
        //this.gameDto.getMyTank().getSelectedBullets()
        double bulletV_x=StaticVariable.BUTTLE_BASCINFOS[selectedBullets].getSpeed()*bulletDistance*Math.cos(Math.toRadians(bulletDegree));
        double bulletV_y=-StaticVariable.BUTTLE_BASCINFOS[selectedBullets].getSpeed()*bulletDistance*Math.sin(Math.toRadians(bulletDegree));
        int pathNum = 0;
        if (isPreView) {
            pathNum = StaticVariable.PREVIEWPATHLENGTH;
        } else {
            pathNum = StaticVariable.PATHLENGTH;
        }
        for (int i = 0; i < pathNum; i++) {
            //这里计算时，采用向下为正，向右为正的方法
            bulletV_y = bulletV_y + StaticVariable.GRAVITY * StaticVariable.INTERVAL_TIME;
            int newPosition_x = (init_x + Tool.dip2px(StaticVariable.LOCAL_DENSITY,(float)(bulletV_x * StaticVariable.INTERVAL_TIME)));
            //bulletPosition_x+=v_x*t;
            int newPosition_y = (init_y + Tool.dip2px(StaticVariable.LOCAL_DENSITY,(float)(bulletV_y * StaticVariable.INTERVAL_TIME + StaticVariable.GRAVITY * StaticVariable.INTERVAL_TIME * StaticVariable.INTERVAL_TIME / 2)));
            //bulletPosition_y+=v_y*t-g*t*t/2;
            double test = bulletV_y / bulletV_x;
            bulletDegree = (int) Math.toDegrees(Math.atan(test));
            Point point = new Point(init_x,init_y, bulletDegree,false);
            //System.out.println( "bulletV_x:" + init_x + " bulletV_y:" + init_y);
            bulletPath.add(point);
            init_x=newPosition_x;
            init_y=newPosition_y;
            //Log.w(TAG, "bulletDegree:" + bulletDegree + "bulletDistance:" + bulletDistance + " bulletPosition_x:" + init_x + " bulletPosition_y:" + init_y);
            //time = time + StaticVariable.INTERVAL;
        }
        return bulletPath;
    }

    public static List<Point> getEnermyBulletPath(int init_x, int init_y, double bulletDistance, int bulletDegree, boolean isPreView, int selectedBullets) {
        //这里关联speed和distance，暂时不处理
        List<Point> bulletPath = new ArrayList<Point>();
        //this.gameDto.getMyTank().getSelectedBullets()
        double bulletV_x=StaticVariable.BUTTLE_BASCINFOS[selectedBullets].getSpeed()*bulletDistance*Math.cos(Math.toRadians(Math.abs(bulletDegree)));
        double bulletV_y=-StaticVariable.BUTTLE_BASCINFOS[selectedBullets].getSpeed()*bulletDistance*Math.sin(Math.toRadians(Math.abs(bulletDegree)));
        int pathNum = 0;
        if (isPreView) {
            pathNum = StaticVariable.PREVIEWPATHLENGTH;
        } else {
            pathNum = StaticVariable.PATHLENGTH;
        }
        for (int i = 0; i < pathNum; i++) {
            //这里计算时，采用向下为正，向右为正的方法
            bulletV_y = bulletV_y + StaticVariable.GRAVITY * StaticVariable.INTERVAL_TIME;
            int newPosition_x = (init_x - Tool.dip2px(StaticVariable.LOCAL_DENSITY,(float)(bulletV_x * StaticVariable.INTERVAL_TIME)));
            //bulletPosition_x+=v_x*t;
            int newPosition_y = (init_y + Tool.dip2px(StaticVariable.LOCAL_DENSITY,(float)(bulletV_y * StaticVariable.INTERVAL_TIME + StaticVariable.GRAVITY * StaticVariable.INTERVAL_TIME * StaticVariable.INTERVAL_TIME / 2)));
            //bulletPosition_y+=v_y*t-g*t*t/2;
            double test = bulletV_y / bulletV_x;
            bulletDegree = (int) Math.toDegrees(Math.atan(test));
            Point point = new Point(init_x,init_y, bulletDegree,false);
            //System.out.println( "bulletV_x:" + init_x + " bulletV_y:" + init_y);
            bulletPath.add(point);
            init_x=newPosition_x;
            init_y=newPosition_y;
            //Log.w(TAG, "bulletDegree:" + bulletDegree + "bulletDistance:" + bulletDistance + " bulletPosition_x:" + init_x + " bulletPosition_y:" + init_y);
            //time = time + StaticVariable.INTERVAL;
        }
        return bulletPath;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float density, float dpValue) {
        //final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float density, float pxValue) {
        //final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / density + 0.5f);
    }
    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(float scaledDensity, float pxValue) {
        //final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / scaledDensity + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(float scaledDensity, float spValue) {
        //final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scaledDensity + 0.5f);
    }

    /******************************命令集合*************************************************
     *
     *  public final static String INIT_SEND_ID_SERVER ="1"; //发送数据到server端
        public final static String INIT_PASSIVE_REQUEST_CONNECT ="2"; //passive发送连接命令到activity端，并传递自身的ID号、确认信息
        public final static String INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT ="3"; //activity确认连接到的passiveId，并发送确认信息
        public final static String INIT_PASSIVE_RESPONSE_SELFINFO ="4";    //passive接受确认信息，并传输自身的信息数据
        public final static String INIT_ACTIVITE_RESPONSE_SELFINFO ="5";    //activity接受信息数据，并传输自身的信息数据
        public final static String INIT_PASSIVE_RESPONSE_INIT_FINISHED ="6";  //passive接受信息数据，并传输初始化完成命令，等待初始化完成命令，然后开始游戏
        public final static String INIT_ACTIVITE_RESPONSE_INIT_FINISHED ="7";  //activity初始化完成命令，开始进入游戏，并传输初始化完成命令
        public final static String INIT_PASSIVE_RESPONSE_GAMEOVER ="8";         //PASSIVE发送一轮游戏结束后的命令
        public final static String INIT_ACTIVITE_RESPONSE_GAMEOVER ="9";        //activity发送一轮游戏结束后的命令
        public final static String  RESPONSE_FINISHED_CONNECT ="10";        //发送断开命令
     *****************************************************************************/
    private static Gson gson = new Gson();
    private static LocalRecord<User> localUser = new LocalRecord<User>();

    /**
     * 发送自己的ID给服务器
     * @param clientCommunicate
     */
    public static void  sendSelfIdToServer(ClientCommunicate clientCommunicate){
        User user = localUser.readInfoLocal(StaticVariable.USER_FILE);
        ComDataF comDataF = ComDataPackage.packageToF(("0#"+user.getId()),StaticVariable.INIT_SEND_ID_SERVER,null);
        clientCommunicate.sendInfo(gson.toJson(comDataF));
    }
    /**
     * passive发送连接命令到activity端，并传递自身的ID号、确认信息
     * @param clientCommunicate
     */
    public static void  sendSelfIdToActive(ClientCommunicate clientCommunicate){
        User user = localUser.readInfoLocal(StaticVariable.USER_FILE);
        String remoteId = StaticVariable.REMOTE_DEVICE_ID;
        ComDataF comDataF = ComDataPackage.packageToF((remoteId+"#"), INIT_PASSIVE_REQUEST_CONNECT,String.valueOf(user.getId()));
        clientCommunicate.sendInfo(gson.toJson(comDataF));
    }
    /**
     * activity确认连接到的passiveId，并发送确认信息
     * @param clientCommunicate
     */
    public static void  sendACKToPassive(ClientCommunicate clientCommunicate){
        String remoteId = StaticVariable.REMOTE_DEVICE_ID;
        ComDataF comDataF = ComDataPackage.packageToF((remoteId+"#"), INIT_ACTIVITE_RESPONSE_CONFIRM_CONNECT,null);
        clientCommunicate.sendInfo(gson.toJson(comDataF));
    }
    /**
     * passive接受确认信息，并传输自身的信息数据
     * @param clientCommunicate
     */
    public static void  sendSelfInfoToActive(ClientCommunicate clientCommunicate){
        String remoteId = StaticVariable.REMOTE_DEVICE_ID;
        //传输的数据包括： 高，宽，密度
        DeviceInfo deviceInfo = new DeviceInfo(StaticVariable.LOCAL_DENSITY,StaticVariable.LOCAL_SCREEN_WIDTH,StaticVariable.LOCAL_SCREEN_HEIGHT);
        ComDataF comDataF = ComDataPackage.packageToF((remoteId+"#"), INIT_PASSIVE_RESPONSE_SELFINFO,gson.toJson(deviceInfo));
        clientCommunicate.sendInfo(gson.toJson(comDataF));
    }
    /**
     * activity接受信息数据，并传输自身的信息数据
     * @param clientCommunicate
     */
    public static void  sendSelfInfoToPassive(ClientCommunicate clientCommunicate){
        String remoteId = StaticVariable.REMOTE_DEVICE_ID;
        //传输的数据包括： 高，宽，密度
        DeviceInfo deviceInfo = new DeviceInfo(StaticVariable.LOCAL_DENSITY,StaticVariable.LOCAL_SCREEN_WIDTH,StaticVariable.LOCAL_SCREEN_HEIGHT);
        ComDataF comDataF = ComDataPackage.packageToF((remoteId+"#"), INIT_ACTIVITE_RESPONSE_SELFINFO,gson.toJson(deviceInfo));
        clientCommunicate.sendInfo(gson.toJson(comDataF));
    }
    /**
     * activity初始化完成命令，开始进入游戏，并传输初始化完成命令
     * @param clientCommunicate
     */
    public static void  sendInitFinishedToPassive(ClientCommunicate clientCommunicate){
        String remoteId = StaticVariable.REMOTE_DEVICE_ID;
        ComDataF comDataF = ComDataPackage.packageToF((remoteId+"#"), INIT_ACTIVITE_RESPONSE_INIT_FINISHED,null);
        clientCommunicate.sendInfo(gson.toJson(comDataF));
    }
    /**
     * passive接受信息数据，并传输初始化完成命令，等待初始化完成命令，
     * @param clientCommunicate
     */
    public static void  sendInitFinishedToActive(ClientCommunicate clientCommunicate){
        String remoteId = StaticVariable.REMOTE_DEVICE_ID;
        ComDataF comDataF = ComDataPackage.packageToF((remoteId+"#"), INIT_PASSIVE_RESPONSE_INIT_FINISHED,null);
        clientCommunicate.sendInfo(gson.toJson(comDataF));
    }
    /**
     * PASSIVE发送一轮游戏结束后的命令
     * @param clientCommunicate
     */
    public static void  sendGameFinishedToActive(ClientCommunicate clientCommunicate){
        String remoteId = StaticVariable.REMOTE_DEVICE_ID;
        ComDataF comDataF = ComDataPackage.packageToF((remoteId+"#"), INIT_PASSIVE_RESPONSE_GAMEOVER,null);
        clientCommunicate.sendInfo(gson.toJson(comDataF));
    }
    /**
     * Pactivity发送一轮游戏结束后的命令
     * @param clientCommunicate
     */
    public static void  sendGameFinishedToPassive(ClientCommunicate clientCommunicate){
        String remoteId = StaticVariable.REMOTE_DEVICE_ID;
        ComDataF comDataF = ComDataPackage.packageToF((remoteId+"#"), INIT_ACTIVITE_RESPONSE_GAMEOVER,null);
        clientCommunicate.sendInfo(gson.toJson(comDataF));
    }
    /**
     * 发送断开命令
     * @param clientCommunicate
     */
    public static void  sendInterruptToRemote(ClientCommunicate clientCommunicate){
        String remoteId = StaticVariable.REMOTE_DEVICE_ID;
        ComDataF comDataF = ComDataPackage.packageToF((remoteId+"#"), RESPONSE_FINISHED_CONNECT_DIRECTIRY,null);
        clientCommunicate.sendInfo(gson.toJson(comDataF));
    }


    /**
     * ACTIVITY发送生成一个新的bonus命令
     * @param clientCommunicate
     */
    public static void  sendNewBonus(ClientCommunicate clientCommunicate, Bonus bonus){
        String remoteId = StaticVariable.REMOTE_DEVICE_ID;
        ComDataF comDataF = ComDataPackage.packageToF((remoteId+"#"), ACTIVITY_MAKE_BONUS,gson.toJson(bonus));
        clientCommunicate.sendInfo(gson.toJson(comDataF));
    }

    /**
     * ACTIVITY发送生成一个新的explode命令
     * @param clientCommunicate
     */
    public static void  sendNewExplode(ClientCommunicate clientCommunicate, Explode explode){
        String remoteId = StaticVariable.REMOTE_DEVICE_ID;
        ComDataF comDataF = ComDataPackage.packageToF((remoteId+"#"), ACTIVITY_MAKE_EXPLODE,gson.toJson(explode));
        clientCommunicate.sendInfo(gson.toJson(comDataF));
    }

    /**
     * ACTIVITY发送生成一个新的子弹命令
     * @param clientCommunicate
     */
    //TODO 这里可能有类型转换的问题，需要测试
    public static void  sendNewBullet(ClientCommunicate clientCommunicate, Bullet bullet){
        String remoteId = StaticVariable.REMOTE_DEVICE_ID;
        ComDataF comDataF = ComDataPackage.packageToF((remoteId+"#"), MAKE_BULLET,gson.toJson(bullet));
        clientCommunicate.sendInfo(gson.toJson(comDataF));
    }

}

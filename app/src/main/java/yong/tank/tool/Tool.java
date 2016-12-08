package yong.tank.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import yong.tank.SelectTank_Activity.modal.PictureInfo;
import yong.tank.modal.Point;

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

    //生成子弹路径
    //计算bonus的路径
    public static List<Point> getBonusPath(Bitmap bonusPicture) {
        //这里关联speed和distance，暂时不处理
        List<Point> bulletPath = new ArrayList<>();
        int direction =new Random().nextInt(2); //生成随机方向
        int bonus_x; //这里应该等于bonuspicture的宽度
        int bonus_y_init = StaticVariable.SCREEN_HEIGHT/StaticVariable.BONUS_Y;  //初始为1/5处的地方
        int bonus_y;
        int speed=StaticVariable.BONUS_SPEED;
        //TODO 振幅为图片的宽度乘以比例
        int scale = bonusPicture.getHeight();
        if(direction==0){
            bonus_x=0;
        }else{
            bonus_x= StaticVariable.SCREEN_WIDTH;
            speed = -speed;
        }
        while(bonus_x>=0&&bonus_x<=StaticVariable.SCREEN_WIDTH){
            bonus_x=bonus_x+speed;
            //注意这里除法是易错点
            bonus_y=bonus_y_init +(int)(Math.sin((double)bonus_x/StaticVariable.BONUS_STEP)*scale);
            Point point = new Point(bonus_x,bonus_y,0,false);
            bulletPath.add(point);
        }
        return bulletPath;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}

package yong.tank.SelectRoom.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import yong.tank.R;
import yong.tank.SelectRoom.modal.MapPicture;
import yong.tank.SelectRoom.modal.TankPicture;
import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

import static yong.tank.tool.StaticVariable.SCREEN_HEIGHT;

/**
 * Created by hasee on 2016/10/27.
 * 暂时设定，初始化的图像，都在图像类中进行加载！
 * P并不能直接提供数据给view层
 * P只能通过View层暴露的方法操控view的行为
 * P/C只能通过dto间接的设置数据
 */

public class SelectView extends View {
    private Context context;
    private Bitmap brackets_left;
    private Bitmap brackets_right;
    private MapPicture[] mapPictures;
    private Bitmap selectionbox;
    private Bitmap bascMeasure;
    private TankPicture[] tankPictures;//tank的图片
    private static String TAG ="SelectView";
    public SelectView(Context context) {
        super(context);
        this.context=context;
        //TODO 这里用来初始化加载一些图片，暂时不用工具类，采用在使用的地方加载图片的方法，，因为android
        //自身的机制原因.....
        initBitmap();
    }

    //TODO 者利用来绘制图片
    protected void onDraw(Canvas canvas) {
        this.drawSaticPicture(canvas);
        //canvas.drawBitmap(brackets_left, 0, 0, null);// 绘制背景
    }

    //初始化所有的图像
    private void initBitmap(){
        Bitmap brackets_left_Temp  = BitmapFactory.decodeResource(getResources(), R.mipmap.brackets_left);
        brackets_left = Tool.reBuildImg(brackets_left_Temp,0,1,1,true,false);
        Tool.releaseBitmap(brackets_left_Temp);
        Bitmap brackets_right_Temp  = BitmapFactory.decodeResource(getResources(), R.mipmap.brackets_right);
        brackets_right = Tool.reBuildImg(brackets_right_Temp,0,1,1,true,false);
        Tool.releaseBitmap(brackets_right_Temp);
        //TODO 初始化所有的坦克 使用xml文件配置
        tankPictures = new TankPicture[StaticVariable.TANKPICTURE.length];
        for(int i=0;i<StaticVariable.TANKPICTURE.length;i++)
        {
            //这里一定要注意，对象数组的使用方法！！！
            tankPictures[i] =new TankPicture();
            tankPictures[i].setPicture(BitmapFactory.decodeResource(getResources(), StaticVariable.TANKPICTURE[i]));
        }
        //加载所有的地图
        mapPictures = new MapPicture[StaticVariable.MAPPICTURE.length];
        for(int i=0;i<StaticVariable.MAPPICTURE.length;i++)
        {
            mapPictures[i] =new MapPicture();
            Bitmap map_Temp  = BitmapFactory.decodeResource(getResources(), StaticVariable.MAPPICTURE[i]);
            //Log.w(TAG,"map_Temp:"+(float)tankPictures[i].getPicture().getWidth()/(float)map_Temp.getWidth());
            mapPictures[i].setPicture(Tool.reBuildImg(map_Temp,
                    0,
                    (float)tankPictures[i].getPicture().getWidth()/(float)map_Temp.getWidth(),
                    (float)tankPictures[i].getPicture().getHeight()/(float)map_Temp.getHeight(),
                    false,false));
            Tool.releaseBitmap(map_Temp);
        }
        //加载选择框
        selectionbox  = BitmapFactory.decodeResource(getResources(), R.mipmap.selectionbox);
        //加载血条等信息
        bascMeasure  = BitmapFactory.decodeResource(getResources(), R.mipmap.measure);
        //加载选择的图片
    }

    //绘制静态的图片 设置图片，统一的通过这种方法进行设置，配置旋转，使用matrix....，这里暂时使用
    //硬编码进行图片配置
    private void drawSaticPicture(Canvas canvas){
        /*****************************绘制隔板********************************/
        //TODO 这里的绘制用xml文件配置
        //第一个
        Rect srcTank_left = new Rect(0, 0, brackets_left.getWidth(), brackets_left.getHeight());
        Rect dstTank_left = new Rect(StaticVariable.SCREEN_WIDTH/4-brackets_left.getWidth()*2,
                SCREEN_HEIGHT/8,
                StaticVariable.SCREEN_WIDTH/4-brackets_left.getWidth(),
                SCREEN_HEIGHT*7/8);               //这里对图像进行了拉伸
        //第二个
        Rect srcTank_right = new Rect(0, 0, brackets_left.getWidth(), brackets_left.getHeight());
        Rect dstTank_right = new Rect(StaticVariable.SCREEN_WIDTH/2-brackets_right.getWidth()*2,
                SCREEN_HEIGHT/8,
                StaticVariable.SCREEN_WIDTH/2-brackets_right.getWidth(),
                SCREEN_HEIGHT*7/8);                                  //这里对图像进行了拉伸
        //第三个
        Rect srcMap_left = new Rect(0, 0, brackets_left.getWidth(), brackets_left.getHeight());
        Rect dstMap_left = new Rect(StaticVariable.SCREEN_WIDTH*3/4-brackets_right.getWidth()*2,
                SCREEN_HEIGHT/8,
                StaticVariable.SCREEN_WIDTH*3/4-brackets_right.getWidth(),
                SCREEN_HEIGHT*7/8);                                  //这里对图像进行了拉伸
        //第四个
        Rect srcMap_right = new Rect(0, 0, brackets_left.getWidth(), brackets_left.getHeight());
        Rect dstMap_right = new Rect(StaticVariable.SCREEN_WIDTH-brackets_right.getWidth()*2,
                SCREEN_HEIGHT/8,
                StaticVariable.SCREEN_WIDTH-brackets_right.getWidth(),
                SCREEN_HEIGHT*7/8);                                  //这里对图像进行了拉伸
        canvas.drawBitmap(brackets_left, srcTank_left, dstTank_left, null);// 绘制背景
        canvas.drawBitmap(brackets_right, srcTank_right, dstTank_right, null);// 绘制背景
        canvas.drawBitmap(brackets_left, srcMap_left, dstMap_left, null);// 绘制背景
        canvas.drawBitmap(brackets_right, srcMap_right, dstMap_right, null);// 绘制背景
        //绘制坦克图片
        for(int i=0;i<tankPictures.length;i++)
        {

            float padding = i*tankPictures[i].getPicture().getHeight()+tankPictures[i].getPicture().getHeight();
            float tankPicture_x = StaticVariable.SCREEN_WIDTH/16;
            float tankPicture_y = SCREEN_HEIGHT/16+padding;
            canvas.drawBitmap(tankPictures[i].getPicture(), tankPicture_x, tankPicture_y, null);// 绘制背景
            //设置图片的位置:
            Tool.setRect(tankPictures[i],(int)tankPicture_x,(int)tankPicture_y);
        }
        //TODO 以后要进行地图的缩放
        //绘制地图图片
        for(int i=0;i<mapPictures.length;i++)
        {
            float padding = i*mapPictures[i].getPicture().getHeight()+mapPictures[i].getPicture().getHeight();
            float mapPicture_x =StaticVariable.SCREEN_WIDTH/2+mapPictures[i].getPicture().getWidth()/4;
            float mapPicture_y = StaticVariable.SCREEN_HEIGHT/16+padding;
            canvas.drawBitmap(mapPictures[i].getPicture(),mapPicture_x ,mapPicture_y , null);// 绘制背景
            Tool.setRect(mapPictures[i],(int)mapPicture_x,(int)mapPicture_y);
        }
        //TODO 可以考虑方法,因为其他地方也用到了
        //绘制选择框
        //绘制放大的tank图像
        //绘制tank的基本信息
        //绘制说明文字

    }



    public void showTankSelected(int i) {

    }


    public void showMapSelected(int i) {
    }


    public MapPicture[] getMapPictures() {
        return mapPictures;
    }

    public void setMapPictures(MapPicture[] mapPictures) {
        this.mapPictures = mapPictures;
    }

    public TankPicture[] getTankPictures() {
        return tankPictures;
    }

    public void setTankPictures(TankPicture[] tankPictures) {
        this.tankPictures = tankPictures;
    }



    /* //这里用来判断，点击是否在图像范围内
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.v);
        canvas.drawBitmap(bitmap, 0, 0, null);

        //创建和位图一样位置的Rect
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        if(rect.contains((int)x, (int)y)){
            System.out.println("范围之内");
        }
        else{
            System.out.println("范围之外");
        }test onTouch度：" + bitmap.getHeight());
        System.out.println("点击X：" + x + "点击Y：" + y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            x = event.getX();
            y = event.getY();
            // 重绘
            invalidate();
        }
        return true;
    }*/
}

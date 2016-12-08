package yong.tank.SelectTank_Activity.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import yong.tank.R;
import yong.tank.SelectTank_Activity.modal.MapPicture;
import yong.tank.SelectTank_Activity.modal.TankPicture;
import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

import static yong.tank.tool.StaticVariable.SCREEN_HEIGHT;

/**
 * Created by hasee on 2016/10/27.
 * 暂时设定，初始化的图像，都在图像类中进行加载！
 * P并不能直接提供数据给view层
 * P只能通过View层暴露的方法操控view的行为
 * P/C只能通过dto间接的设置数据
 * //这里的硬编码很麻烦，不要再动了.....尼玛
 */

public class SelectView extends View {
    private Context context;
    private Bitmap brackets_left;
    private Bitmap brackets_right;
    private MapPicture[] mapPictures;
    private Bitmap selectionbox;
    private Bitmap bascMeasure;
    private Bitmap speedMeasure;
    private Bitmap bloodMeasure;
    private Bitmap powerMeasure;
    private Bitmap codeName;
    private Bitmap[] previewTanks=new Bitmap[StaticVariable.TANKPICTURE.length];
    private TankPicture[] tankPictures;//tank的图片
    private static String TAG ="SelectView";
    private int selectedTank=0;
    private int selectedMap=0;
    private int name_Y=0;
    private int speed_Y=0;
    private int blood_Y=0;
    private int power_Y=0;
    private int describe_Y=0;
    private int total_X=0;
    private int blood_X=0;
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
        //加载预备展示tank的图片
        for(int i=0;i<StaticVariable.TANKPICTURE.length;i++)
        {
            //这里一定要注意，对象数组的使用方法！！！
            previewTanks[i] = Tool.reBuildImg(tankPictures[i].getPicture(),0,1,1,false,true);
        }
        //加载血条，力气，能量，血量
        bascMeasure  = BitmapFactory.decodeResource(getResources(), R.mipmap.measure);
        powerMeasure  = BitmapFactory.decodeResource(getResources(), R.mipmap.stength);
        bloodMeasure = BitmapFactory.decodeResource(getResources(), R.mipmap.bloodmeasurement_2);
        speedMeasure = BitmapFactory.decodeResource(getResources(), R.mipmap.speed);
        codeName = BitmapFactory.decodeResource(getResources(), R.mipmap.name);
        //加载文字信息

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
            //Log.d(TAG,"TEST2:"+i+" "+tankPictures[i].getPicture());
            float padding = i*tankPictures[i].getPicture().getHeight()+tankPictures[i].getPicture().getHeight();
            float tankPicture_x = StaticVariable.SCREEN_WIDTH/20;
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
        //绘制初始化的选择框 默认选择第一个
        Tool.drawCentral(canvas,tankPictures[selectedTank],selectionbox);
        //绘制选择的坦克 选择坦克的位置在与括号相关的位置......即括号的左顶点开始绘制，绘制到中部顶点为止
        Rect src_selectTank = new Rect(0, 0, previewTanks[selectedTank].getWidth(),  previewTanks[selectedTank].getHeight());
        Rect des_selectTank = new Rect(
                dstTank_left.right,
                dstTank_left.top,
                dstTank_right.centerX()-dstTank_left.width()/2,
                dstTank_right.centerY()*2/3);
        canvas.drawBitmap(previewTanks[selectedTank],src_selectTank,des_selectTank, null);// 绘制背景
        //绘制tank的基本信息 在预览图像下面绘制 顺序为：代号，血量，力量，速度
        //TODO 绘制说明文字 硬编码
        total_X=des_selectTank.left;
        blood_X=total_X+bascMeasure.getWidth()*3;
        name_Y=des_selectTank.left;
        blood_Y=name_Y+codeName.getHeight();
        power_Y=blood_Y+bloodMeasure.getHeight();
        speed_Y=power_Y+powerMeasure.getHeight();
        describe_Y=speed_Y+speedMeasure.getHeight()+Tool.sp2px(context.getResources().getDisplayMetrics().scaledDensity,StaticVariable.SELECT_DECRIBE_SIZE)+3;
        canvas.drawBitmap(codeName,des_selectTank.left,name_Y, null);// 绘制代号
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(StaticVariable.STROKEWIDTH);
        paint.setTextSize(Tool.sp2px(this.context.getResources().getDisplayMetrics().scaledDensity,StaticVariable.SELECT_WORD_SIZE));
        paint.setColor(Color.CYAN);
        paint.setColor(Color.RED);
        canvas.drawBitmap(bloodMeasure,total_X,blood_Y, null);// 绘制血量
        canvas.drawBitmap(powerMeasure,total_X,power_Y, null);// 绘制力量
        canvas.drawBitmap(speedMeasure,total_X,speed_Y, null);// 绘制速度

        this.drawTankInfo(canvas ,selectedTank); //绘制初始化
        //canvas.drawText("tank很牛A", total_X+90, name_Y+StaticVariable.SELECT_WORD_SIZE, paint);// 绘制代号名称

        //canvas.drawBitmap(bascMeasure,total_X+77+20,blood_Y, null);// 绘制基本血条

    }

    //绘制动态的方法
    public void drawTankInfo(Canvas canvas ,int i){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(StaticVariable.STROKEWIDTH);
        paint.setTextSize(Tool.sp2px(this.context.getResources().getDisplayMetrics().scaledDensity,StaticVariable.SELECT_WORD_SIZE));
        paint.setColor(Color.RED);
        //绘制代号
        canvas.drawText(StaticVariable.TANKBASCINFO[i].getTankName(), total_X+bascMeasure.getWidth()*4, name_Y+Tool.sp2px(context.getResources().getDisplayMetrics().scaledDensity,StaticVariable.SELECT_WORD_SIZE), paint);// 绘制代号名称

        this.drawDramticInfo(canvas,StaticVariable.TANKBASCINFO[i].getBlood(),blood_Y);// 绘制血条
        this.drawDramticInfo(canvas,StaticVariable.TANKBASCINFO[i].getPower(),power_Y);// 绘制力量
        this.drawDramticInfo(canvas,StaticVariable.TANKBASCINFO[i].getSpeed(),speed_Y);// 绘制速度
        //这里的绘制可能需要多行，处理即可，这里就不做计算了
        paint.setTextSize(Tool.sp2px(this.context.getResources().getDisplayMetrics().scaledDensity,StaticVariable.SELECT_DECRIBE_SIZE));
        canvas.drawText(StaticVariable.TANKBASCINFO[i].getDescribeInfo(), total_X, describe_Y, paint); //绘制describe
    }
    //绘制基本的方法
    public void drawDramticInfo(Canvas canvas ,int num,int y){
        int circle = num/10;
        int measureInterval = bascMeasure.getWidth()*4/5;
        //Log.d(TAG,"************************");
        for(int i=0;i<circle;i++)
        {
            //Log.d(TAG,"CESHI :"+measureInterval*i);
            canvas.drawBitmap(bascMeasure,blood_X+measureInterval*i,y, null);
        }

    }


    public void showTankSelected(int i) {
        selectedTank=i;
        this.postInvalidate();
    }


    public void showMapSelected(int i) {
        selectedMap=i;
        this.postInvalidate();
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

    public int getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(int selectedMap) {
        this.selectedMap = selectedMap;
    }

    public int getSelectedTank() {
        return selectedTank;
    }

    public void setSelectedTank(int selectedTank) {
        this.selectedTank = selectedTank;
    }
}

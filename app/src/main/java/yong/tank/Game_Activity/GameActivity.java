package yong.tank.Game_Activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import yong.tank.Dto.GameDto;
import yong.tank.Game_Activity.View.SelectView;
import yong.tank.Game_Activity.View.ViewBase;
import yong.tank.Game_Activity.control.GameControler;
import yong.tank.Game_Activity.control.PlayControler;
import yong.tank.Game_Activity.service.GameService;
import yong.tank.R;
import yong.tank.modal.Blood;
import yong.tank.modal.MyTank;
import yong.tank.modal.PlayerPain;
import yong.tank.tool.StaticVariable;
import yong.tank.tool.Tool;

/**
 * Created by hasee on 2016/10/30.
 */

public class GameActivity extends Activity implements View.OnClickListener {
    private int tankType ;
    private int mapType ;
    private static String TAG = "GameActivity";
    private PlayControler playControler;
    private GameControler gameControler;
    private GameService gameService;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setContentView(R.layout.activity_game); //一般放在super后面比较好,但是这里，因为要横屏，所以放在后面
        RelativeLayout activity_game = (RelativeLayout) findViewById(R.id.activity_game);
        GameDto gameDto = new GameDto();
        tankType=this.getIntent().getIntExtra("tankType", 0);
        mapType=this.getIntent().getIntExtra("mapType", 0);
        //TODO 初始化explode
        for(int i=0;i<StaticVariable.EXPLODESPICTURE_GROUND.length;i++){
            StaticVariable.EXPLODESONGROND[i]=BitmapFactory.decodeResource(getResources(), StaticVariable.EXPLODESPICTURE_GROUND[i]);
        }
        for(int i=0;i<StaticVariable.EXPLODESPICTURE_TANKE.length;i++){
            StaticVariable.EXPLODESONTANK[i]=BitmapFactory.decodeResource(getResources(), StaticVariable.EXPLODESPICTURE_TANKE[i]);
        }
        //TODO 测试tank
        MyTank myTank =initTank(tankType);
        gameDto.setMyTank(myTank);
        //TODO 测试玩家控制图标
        PlayerPain playerPain = new PlayerPain();
        gameDto.setMyTank(myTank);
        gameDto.setPlayerPain(playerPain);
        //TODO 测试装载血条的视图
        Blood blood = initBlood(true);
        gameDto.setBlood(blood);
        //TODO 考虑可否用反射处理这个问题,最后考虑用spring处理这个问题

        //绘制gameView
/*        ViewBase gameView =new GameView(this,gameDto);
        gameView.setZOrderOnTop(true); //设置canves为透明必须要加.....
        //绘制playerView
        ViewBase playerView =new PlayerView(this,gameDto);
        playerView.setZOrderOnTop(true); //设置canves为透明必须要加.....
        //绘制bloodView
        ViewBase bloodView =new BloodView(this,gameDto);
        bloodView.setZOrderOnTop(true); //设置canves为透明必须要加.....
        //绘制explodeView
        ViewBase explodeView =new ExplodeView(this,gameDto);
        explodeView.setZOrderOnTop(true); //设置canves为透明必须要加.....
        //绘制bonuxView
        ViewBase bonusVew = new BonusView(this,gameDto);
        bonusVew.setZOrderOnTop(true); //设置canves为透明必须要加.....*/
        //绘制selectView   这里添加selectView之前，还要设置view的基本位置信息，主要在layout中处理位置
        SelectView selectView = (SelectView)findViewById(R.id.selectView);
        selectView.initButton();
        selectView.getSelectButton_1().setOnClickListener(this);
        selectView.getSelectButton_2().setOnClickListener(this);
        gameDto.setSelectButtons(selectView.getSelectButtons());
        List<ViewBase> views=initViews(gameDto);
        for(ViewBase v:views){
            //v.startThread();
            v.setZOrderOnTop(true);
            activity_game.addView(v);
        }
/*        activity_game.addView(gameView);
        activity_game.addView(playerView);
        activity_game.addView(bloodView);
        activity_game.addView(bonusVew);
        activity_game.addView(explodeView);*/
        //activity_game.addView(selectView);  这里不用加，因为已经在里面
        //比较特殊的，加入selectView 即这里对selectView做另一种方法的处理：
        /*********程序控制器**********/
        gameService = new GameService(gameDto,this);
        gameControler = new GameControler(gameService,this);
        /*********设置玩家控制器**********/
        playControler = new PlayControler(this,gameDto,gameControler);
        //TODO 所有初始化等工作完成以后，就开始游戏：
        gameControler.startGame();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Toast.makeText(this, "event.getX() "+event.getX(), Toast.LENGTH_SHORT).show();
        playControler.setMotion(event);
        return false;
    }

    private MyTank initTank(int tankType){
        Bitmap tankPicture_temp = BitmapFactory.decodeResource(getResources(), StaticVariable.TANKBASCINFO[tankType].getPicture());
        Bitmap tankPicture = Tool.reBuildImg(tankPicture_temp,0,1,1,false,true);
        Bitmap armPicture = BitmapFactory.decodeResource(getResources(), R.mipmap.gun);
        MyTank tank = new MyTank(tankPicture,armPicture,tankType, StaticVariable.TANKBASCINFO[tankType]);
        return tank;
    }
    private Blood initBlood(Boolean isMyBlood){
        //TODO 如果是true会找其他图片
        //TODO 这里暂时先别做.....以后再改....
        Bitmap blood_picture=null;
        blood_picture = BitmapFactory.decodeResource(getResources(), StaticVariable.BLOOD);
        Bitmap power_picture=null;
        power_picture = BitmapFactory.decodeResource(getResources(), StaticVariable.POWERR);
        Bitmap bloodBlock_picture=null;
        bloodBlock_picture = BitmapFactory.decodeResource(getResources(), StaticVariable.BLOODBLOCK);
        Blood blood = new Blood(blood_picture, power_picture, bloodBlock_picture,1,1);
        return blood;
    }

    //选择按钮的处理方式
    @Override
    public void onClick(View view) {
        //在这里传入selectView,然后进行逻辑处理即可....
        this.playControler.setClick(view);
    }


    //初始化图像绘制界面
    public List<ViewBase> initViews(GameDto gameDto)  {
        List<ViewBase> views= new ArrayList<>(StaticVariable.VIEW_LIST.length);
        for(String className:StaticVariable.VIEW_LIST){
            Log.i(TAG,"className:"+className);
            Class<?>cls = null;
            try {
                cls = Class.forName(className);
            } catch (ClassNotFoundException e) {
                Log.i(TAG,"e");
                e.printStackTrace();
            }
            Constructor<?> ctr = null;
            try {
                ctr = cls.getConstructor(Context.class,GameDto.class);
            } catch (NoSuchMethodException e) {
                Log.i(TAG,"e");
                e.printStackTrace();
            }
            ViewBase v= null;
            try {
                v = (ViewBase) ctr.newInstance(this,gameDto);
            } catch (InstantiationException e) {
                Log.i(TAG,"e");
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                Log.i(TAG,"e");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                Log.i(TAG,"e");
                e.printStackTrace();
            }
            views.add(v);

        }
        return views;
    }


}
package yong.tank.Game_Activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
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
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/10/30.
 */

public class GameActivity extends Activity implements View.OnClickListener {
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
        //获取前面传过来的数据
        gameDto.setTankType(this.getIntent().getIntExtra("tankType", 0));
        gameDto.setMapType(this.getIntent().getIntExtra("mapType", 0));

        //绘制selectView   这里添加selectView之前，还要设置view的基本位置信息，主要在layout中处理位置
        SelectView selectView = (SelectView)findViewById(R.id.selectView);
        selectView.initButton();
        selectView.getSelectButton_1().setOnClickListener(this);
        selectView.getSelectButton_2().setOnClickListener(this);
        gameDto.setSelectButtons(selectView.getSelectButtons());
        List<ViewBase> views=initViews(gameDto);
        for(ViewBase v:views){
            //一定要加上这一句
            v.setZOrderOnTop(true);
            activity_game.addView(v);
        }
        //activity_game.addView(selectView);  这里不用加，因为已经在里面
        //比较特殊的，加入selectView 即这里对selectView做另一种方法的处理：
        /*********程序控制器**********/
        gameService = new GameService(gameDto,this);
        gameControler = new GameControler(gameService,this,views);
        /*********设置玩家控制器**********/
        playControler = new PlayControler(this,gameDto,gameControler);
        //TODO 所有初始化等工作完成以后，就开始游戏：
        //TODO 这里要设定，只有初始化完全成功后，才能调用statGame方法开始游戏......
        gameControler.startGame();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Toast.makeText(this, "event.getX() "+event.getX(), Toast.LENGTH_SHORT).show();
        playControler.setMotion(event);
        return false;
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
            //Log.i(TAG,"className:"+className);
            Class<?>cls = null;
            try {
                cls = Class.forName(className);
            } catch (ClassNotFoundException e) {
                Log.i(TAG,e.toString());
                e.printStackTrace();
            }
            Constructor<?> ctr = null;
            try {
                ctr = cls.getConstructor(Context.class,GameDto.class);
            } catch (NoSuchMethodException e) {
                Log.i(TAG,e.toString());
                e.printStackTrace();
            }
            ViewBase v= null;
            try {
                v = (ViewBase) ctr.newInstance(this,gameDto);
            } catch (InstantiationException e) {
                Log.i(TAG,e.toString());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                Log.i(TAG,e.toString());
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                Log.i(TAG,e.toString());
                e.printStackTrace();
            }
            views.add(v);

        }
        return views;
    }


}

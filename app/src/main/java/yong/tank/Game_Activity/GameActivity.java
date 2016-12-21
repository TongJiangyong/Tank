package yong.tank.Game_Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import yong.tank.Communicate.InterfaceGroup.ClientCommunicate;
import yong.tank.Communicate.InternetCommunicate.ClentInternet;
import yong.tank.Communicate.LocalCommunicate.ClientLocal;
import yong.tank.Communicate.bluetoothCommunicate.ClientBluetooth;
import yong.tank.Dto.GameDto;
import yong.tank.Game_Activity.View.SelectView;
import yong.tank.Game_Activity.View.ViewBase;
import yong.tank.Game_Activity.control.GameControler;
import yong.tank.Game_Activity.control.PlayControler;
import yong.tank.Game_Activity.presenter.GamePresenter;
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
    private Button msgButton;
    private Button msgSend;
    private ImageView msgView;
    private TextView msgText;
    private GameService gameService;
    private ClientCommunicate clientCommunicate;
    private GamePresenter gamePresenter;
    private boolean startFlag =false;

    //启动游戏正式开始的handler
    private  Handler gameActivityHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what){
                case StaticVariable.GAME_STARTED:
                    Log.i(TAG,"接受两次....");
                    startGame();
                    break;
            }

        }

    };
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setContentView(R.layout.activity_game); //一般放在super后面比较好,但是这里，因为要横屏，所以放在后面
        RelativeLayout activity_game = (RelativeLayout) findViewById(R.id.activity_game);
        gamePresenter = new GamePresenter(this,this);
        GameDto gameDto = new GameDto();
        //获取前面传过来的数据
        gameDto.setTankType(this.getIntent().getIntExtra("tankType", 0));
        gameDto.setMapType(this.getIntent().getIntExtra("mapType", 0));

        //绘制selectView   这里添加selectView之前，还要设置view的基本位置信息，主要在layout中处理位置
        SelectView selectView = (SelectView)findViewById(R.id.selectView);
        msgButton = (Button)findViewById(R.id.msgButton);
        msgView = (ImageView)findViewById(R.id.msgView);
        msgText = (TextView)findViewById(R.id.msgText);
        msgSend = (Button)findViewById(R.id.msgSend);
        if(StaticVariable.CHOSED_MODE ==StaticVariable.GAME_MODE.LOCAL){
            msgButton.setVisibility(View.GONE);
        }
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
        gameService = new GameService(gameDto,this,gameActivityHandler);
        gameControler = new GameControler(gameService,this,views);
        /*********设置玩家控制器**********/
        playControler = new PlayControler(this,gameDto,gameControler);
        //TODO 所有初始化等工作完成以后，就开始游戏：
        //TODO 这里要设定，只有初始化完全成功后，才能调用statGame方法开始游戏......这个方法，以后再调用
        //即，针对不同的模式，启动方式完全不同.....原因：设计缺陷......
        //设置网络的组装，也在这儿......
        //如果是蓝牙模式，则需要设置蓝牙
        //如果是联网模式，则需要设置联网
        //对上诉的互联模式，统一让初始化完成后，才能全部开始游戏
        //如果是普通的模式，则也要设定一个初始化的handle，统一控制游戏的启动.....
        boolean prepareFlag = false;
        if(StaticVariable.CHOSED_MODE == StaticVariable.GAME_MODE.INTERNET) {
            //如果是联网模式
            this.clientCommunicate = new ClentInternet(StaticVariable.SERVER_IP, StaticVariable.SERVER_PORT);
            //进入联网模式的确认连接步骤
            this.gamePresenter.prepareInternet(this.clientCommunicate);
            //联网模式确认连接步骤及，能与远程的tcp网络通信

        }else if(StaticVariable.CHOSED_MODE == StaticVariable.GAME_MODE.BLUETOOTH){
            //如果是蓝牙模式
            this.clientCommunicate = new ClientBluetooth();
            //进入蓝牙模式的确认连接步骤为，确定连上
            this.gamePresenter.prepareBlue(this.clientCommunicate);
        }else{
            //如果是本地模式
            this.clientCommunicate = new ClientLocal(gameDto,this);
            //进入本地模式的确认连接步骤 可以不做
            this.gamePresenter.prepareLocal(this.clientCommunicate);
        }
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Toast.makeText(this, "event.getX() "+event.getX(), Toast.LENGTH_SHORT).show();
        if(startFlag){
            playControler.setMotion(event);
            return false;
        }
        return  false;
    }


    //选择按钮的处理方式
    @Override
    public void onClick(View view) {
        //在这里传入electView,然后进行逻辑处理即可....
        switch (view.getId()) {
            case R.id.msgButton:
                //显示出msg的界面......
                if(msgView.getVisibility()==View.GONE){
                    msgView.setVisibility(View.VISIBLE);
                    msgText.setVisibility(View.VISIBLE);
                    msgSend.setVisibility(View.VISIBLE);
                }else{
                    msgView.setVisibility(View.GONE);
                    msgText.setVisibility(View.GONE);
                    msgSend.setVisibility(View.GONE);
                }

                break;
            case  R.id.msgSend:
                //TODO deal with send method
                break;
            default:
                break;
        }
        if(startFlag){
            this.playControler.setClick(view);
        }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Log.w(TAG,"TESTBLUE");
        // 蓝牙启动后返回的result
        if (requestCode == StaticVariable.REQUEST_CODE_BLUETOOTH_ON)
        {
            switch (resultCode)
            {
                // 点击确认按钮
                case Activity.RESULT_OK:
                    gamePresenter.enableBluetooth();
                    break;
                // 点击取消按钮或点击返回键
                case Activity.RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        }
        //选取设备后，触发的代码
        if(requestCode == StaticVariable.CHOSED_BLUT_DEVICE){
            switch (resultCode)
            {
                // 点击确认按钮
                case Activity.RESULT_OK:
                    Log.i(TAG,"toBlueTankChose");
                    gamePresenter.toBlueTankChose(resultCode, data);
                    break;
                case Activity.DEFAULT_KEYS_SHORTCUT:
                    Log.i(TAG,"等待蓝牙设备连入.....");
                    break;
                default:
                    Log.i(TAG,"return error");
                    //关掉相关的blue tooth
                    gamePresenter.turnOffCommunicate();
                    break;
            }

        }
    }

    public void showToast(String info){
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }

    //开始进行通信初始化的完整流程.......
    public void initCommunicate(){
        this.gameControler.getGameService().setClientCommunicate(this.clientCommunicate);
        //初始化全部的数据
        this.gameControler.getGameService().initAllDataInfo();
    }

    //开始游戏
    public void startGame(){
        //允许游戏操作...
        gameControler.startGame();
        //允许按钮操作
        this.startFlag = true;
        Log.i(TAG,"开始游戏.....");
        this.showToast("开始游戏...");
    }



}

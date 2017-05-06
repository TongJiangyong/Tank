package yong.tank.Title_Activity.View;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import yong.tank.LocalRecord.LocalRecord;
import yong.tank.R;
import yong.tank.Title_Activity.presenter.ITitlePresenter;
import yong.tank.Title_Activity.presenter.TitlePresenter;
import yong.tank.modal.FrightRecord;
import yong.tank.modal.User;
import yong.tank.tool.StaticVariable;

public class MainActivity extends Activity implements ITitleView, View.OnClickListener{
    private static String TAG = "MainActivity";
    private Button battleCom;
    private Button battleBlue;
    private Button battleNet;
    private Button battleHelp;
    private ITitlePresenter titlePresenter;
    private LocalRecord<User> localUser = new LocalRecord<User>();
    // Member object for the chat services
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticVariable.GAME_START_TIME = System.currentTimeMillis();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setContentView(R.layout.activity_main);  //注意，这一句话一定要在initButton之前，否则，会找不到button，即findViewById会找不到....
        DisplayMetrics metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metric);
        //初始化本地的用户信息
        initLocalUser();
        initButton();
        titlePresenter =new TitlePresenter(this,this);
        //设置本地设备的信息：
        StaticVariable.LOCAL_SCREEN_WIDTH = metric.widthPixels;  // 屏幕宽度（像素）
        StaticVariable.LOCAL_SCREEN_HEIGHT = metric.heightPixels;  // 屏幕高度（像素）
        StaticVariable.LOCAL_DENSITY = this.getResources().getDisplayMetrics().density ;
        //TODO 重新计算一些参数
        //bonus的速度  5s走完
        StaticVariable.BONUS_SPEED = (int)((float)StaticVariable.LOCAL_SCREEN_WIDTH/(float)(StaticVariable.BONUS_TIME*StaticVariable.LOGICAL_FRAME));
        //bonus的初始Y位置   画面1/5处
        StaticVariable.BONUS_Y_INIT = (int)((float)StaticVariable.LOCAL_SCREEN_HEIGHT/5);
        //bonus的振幅为   画面的1/10
        StaticVariable.BONUS_SCALE = (int)((float)StaticVariable.LOCAL_SCREEN_HEIGHT/10);
        //设置场景的重力
        StaticVariable.GRAVITY= StaticVariable.LOCAL_SCREEN_HEIGHT * 8 ;
        Log.i(TAG, "Test in company");
    }

    private void initLocalUser() {
        //初始化获取根目录的文件......
        StaticVariable.USER_FILE= new File(this.getFilesDir() , StaticVariable.TANK_USER_INFO);
        //则创建一个新的User，并写入
        if(localUser.readInfoLocal(StaticVariable.USER_FILE)==null){
            User user =new User();
            FrightRecord frightRecord = new FrightRecord();
            user.setFrightRecord(frightRecord);
            localUser.saveInfoLocal(user,StaticVariable.USER_FILE);
            StaticVariable.LOCAL_USER_INFO = user;
        }else{
            StaticVariable.LOCAL_USER_INFO=localUser.readInfoLocal(StaticVariable.USER_FILE);
        }



    }

    private void initButton() {
        battleCom = (Button) findViewById(R.id.battle_computer);
        battleBlue = (Button) findViewById(R.id.battle_blue);
        battleNet = (Button) findViewById(R.id.battle_net);
        battleHelp = (Button) findViewById(R.id.help);
        battleCom.setOnClickListener(this);
        battleBlue.setOnClickListener(this);
        battleNet.setOnClickListener(this);
        battleHelp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.battle_computer:
                //Log.i(TAG,"battle_computer");
                titlePresenter.toComputer();
                break;
            case R.id.battle_blue:
                //Log.i(TAG,"battle_computer");
                titlePresenter.toBluetooth();
                break;
            case R.id.battle_net:
                //Log.i(TAG,"battle_computer");
                titlePresenter.toNet();
                break;
            case R.id.help:
                //Log.i(TAG,"battle_computer");
                titlePresenter.tohelp();
                break;
            default:
                break;
        }
    }
    @Override
    public void showToast(String info){
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.w(TAG,"TESTBLUE");
        // 蓝牙启动后返回的result
        if (requestCode == StaticVariable.REQUEST_CODE_BLUETOOTH_ON) {
            Log.i(TAG,"requestCode:"+requestCode);
            Log.i(TAG,"resultCode:"+resultCode);
            switch (resultCode) {
                // 点击确认按钮 注意这里的值与设备可见时间一致
                case StaticVariable.VISIBLE_TIME:
                    Log.i(TAG,"enable RESULT_OK");
                    titlePresenter.enableBluetooth();
                    break;
                // 点击取消按钮或点击返回键
                case Activity.RESULT_CANCELED:
                    Log.i(TAG,"enable RESULT_CANCELED");
                    break;
                default:
                    break;
            }
        }
    }

}

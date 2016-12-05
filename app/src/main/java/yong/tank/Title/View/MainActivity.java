package yong.tank.Title.View;

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

import yong.tank.R;
import yong.tank.Title.presenter.ITitlePresenter;
import yong.tank.Title.presenter.TitlePresenter;
import yong.tank.tool.StaticVariable;

public class MainActivity extends Activity implements ITitleView, View.OnClickListener{
    private static String TAG = "MainActivity";
    private Button battleCom;
    private Button battleBlue;
    private Button battleNet;
    private Button battleHelp;
    private ITitlePresenter titlePresenter;
    // Member object for the chat services
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setContentView(R.layout.activity_main);  //注意，这一句话一定要在initButton之前，否则，会找不到button，即findViewById会找不到....
        DisplayMetrics metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metric);
        StaticVariable.SCREEN_WIDTH = metric.widthPixels;  // 屏幕宽度（像素）
        StaticVariable.SCREEN_HEIGHT = metric.heightPixels;  // 屏幕高度（像素）
        initButton();
        titlePresenter =new TitlePresenter(this,this);
        Log.i(TAG, "Test in company");
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
                    titlePresenter.enableBluetooth();
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
                    titlePresenter.toBlueTankChose(resultCode, data);
                    break;
                default:
                    break;
            }

        }
    }

}

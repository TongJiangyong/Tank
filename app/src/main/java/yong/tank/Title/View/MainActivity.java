package yong.tank.Title.View;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import yong.tank.R;
import yong.tank.Title.presenter.ITitlePresenter;
import yong.tank.Title.presenter.TitlePresenter;

public class MainActivity extends AppCompatActivity implements ITitleView, View.OnClickListener{
    private static String TAG = "MainActivity";
    private Button battleCom;
    private Button battleBlue;
    private Button battleNet;
    private Button battleHelp;
    private ITitlePresenter titlePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setContentView(R.layout.activity_main);  //注意，这一句话一定要在initButton之前，否则，会找不到button，即findViewById会找不到....
        initButton();
        titlePresenter =new TitlePresenter(this,this);
        Log.d(TAG, "Test in company");
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
                //Log.d(TAG,"battle_computer");
                titlePresenter.toComputer();
                break;
            case R.id.battle_blue:
                //Log.d(TAG,"battle_computer");
                titlePresenter.toBluetooth();
                break;
            case R.id.battle_net:
                //Log.d(TAG,"battle_computer");
                titlePresenter.toNet();
                break;
            case R.id.help:
                //Log.d(TAG,"battle_computer");
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

}

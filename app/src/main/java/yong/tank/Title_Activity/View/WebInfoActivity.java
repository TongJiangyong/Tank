package yong.tank.Title_Activity.View;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import yong.tank.R;
import yong.tank.Title_Activity.presenter.WebInfoPresenter;
import yong.tank.modal.WebInfo;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/12/10.
 */

public class WebInfoActivity extends Activity implements View.OnClickListener{
    private static String TAG = "WebInfoActivity";
    private WebInfoPresenter webInfoPresenter;
    private Button creatRoom;
    private Button refreshRoom;
    public WebInfo webInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setContentView(R.layout.activity_webinfo);
        webInfo = (WebInfo)findViewById(R.id.web_info);
        //构造webInfo上的信息
        webInfo.initWebInfo();
        creatRoom = (Button)findViewById(R.id.creatNewRoom);
        refreshRoom = (Button)findViewById(R.id.refreshRoom);
        webInfoPresenter = new WebInfoPresenter(this,this);
        creatRoom.setOnClickListener(this);
        refreshRoom.setOnClickListener(this);
        //初始化记录信息
        webInfoPresenter.recordInit();
        //初始化房间信息
        webInfoPresenter.roomInfoInit();
        //User userinfo =localUser.readInfoLocal(StaticVariable.USER_FILE);
        //Log.i(TAG, "print personnal info："+userinfo.toString() );
        if(StaticVariable.DEBUG) {
            Log.i(TAG, "Test  WebInfoActivity");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.creatNewRoom:
                //Log.i(TAG,"battle_computer");
                webInfoPresenter.creatRoom();
                break;
            case R.id.refreshRoom:
                //Log.i(TAG,"battle_computer");
                webInfoPresenter.refreshRoom();
                break;
            default:
                break;
        }
    }

    public void showToast(String info){
            Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }
}

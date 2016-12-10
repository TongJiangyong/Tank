package yong.tank.Title_Activity.View;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import yong.tank.R;
import yong.tank.Title_Activity.presenter.WebInfoPresenter;
import yong.tank.modal.WebInfo;

/**
 * Created by hasee on 2016/12/10.
 */

public class WebInfoActivity extends Activity implements View.OnClickListener{
    private static String TAG = "WebInfoActivity";
    private WebInfoPresenter webInfoPresenter;
    private Button creatRoom;
    public TextView accountText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        webInfoPresenter = new WebInfoPresenter(this,this);
        WebInfo webInfo = (WebInfo)findViewById(R.id.web_info);
        setContentView(webInfo);
        Log.i(TAG, "Test  WebInfoActivity");
    }

    @Override
    public void onClick(View view) {

    }

    public void showToast(String info){
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }
}
